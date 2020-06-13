/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanCreate;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanDestroy;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarFinish;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isAlphaNumeric;

/**
 * This class loads the clan related data.
 * @author JoeAlisson
 */
public class ClanTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanTable.class);
    private final IntMap<Clan> clans = new CHashIntMap<>();

    private ClanTable() {
    }

    private void load() {
        getDAO(ClanDAO.class).findAll().forEach(data -> {
            var clan = new Clan(data);
            clans.put(data.getId(), clan);

            if (data.getDissolvingExpiryTime() != 0) {
                scheduleRemoveClan(clan);
            }
        });

        allianceCheck();
        restoreClanWars();
    }

    private void allianceCheck() {
        for (Clan clan : clans.values()) {
            final int allyId = clan.getAllyId();
            if (allyId != 0 && clan.getId() != allyId && !clans.containsKey(allyId)) {
                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.changeAllyCrest(0, true);
                clan.updateClanInDB();
                LOGGER.info("Removed alliance from clan: {}", clan);
            }
        }
    }

    private void restoreClanWars() {
        getDAO(ClanDAO.class).findAllWars().forEach(warData -> {
            var attacker = getClan(warData.getAttacker());
            var attacked = getClan(warData.getAttacked());

            if (nonNull(attacker) && nonNull(attacked)) {
                var clanWar = new ClanWar(warData);
                attacker.addWar(attacked.getId(), clanWar);
                attacked.addWar(attacker.getId(), clanWar);
            } else {
                LOGGER.warn("Restore wars one of clans is null attacker: {} attacked: {}", attacker, attacked);
            }
        });
    }

    public Collection<Clan> getClans() {
        return clans.values();
    }

    public int getClanCount() {
        return clans.size();
    }

    public Clan getClan(int clanId) {
        return clans.get(clanId);
    }

    public Clan getClanByName(String clanName) {
        return clans.values().stream().filter(c -> c.getName().equalsIgnoreCase(clanName)).findFirst().orElse(null);
    }

    public Clan createClan(Player player, String clanName) {
        if (null == player) {
            return null;
        }

        if (10 > player.getLevel()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
            return null;
        }
        if (0 != player.getClanId()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
            return null;
        }
        if (System.currentTimeMillis() < player.getClanCreateExpiryTime()) {
            player.sendPacket(SystemMessageId.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
            return null;
        }
        if (!isAlphaNumeric(clanName) || (2 > clanName.length())) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return null;
        }
        if (16 < clanName.length()) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return null;
        }

        if (null != getClanByName(clanName)) {
            // clan name is already taken
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
            sm.addString(clanName);
            player.sendPacket(sm);
            return null;
        }

        final Clan clan = new Clan(IdFactory.getInstance().getNextId(), clanName);
        final ClanMember leader = new ClanMember(clan, player);
        clan.setLeader(leader);
        leader.setPlayerInstance(player);
        clan.store();
        player.setClan(clan);
        player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        player.setClanPrivileges(new EnumIntBitmask<>(ClanPrivilege.class, true));

        clans.put(clan.getId(), clan);

        // should be update packet only
        player.sendPacket(new PledgeShowInfoUpdate(clan));
        PledgeShowMemberListAll.sendAllTo(player);
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(SystemMessageId.YOUR_CLAN_HAS_BEEN_CREATED);
        player.broadcastUserInfo(UserInfoType.RELATION, UserInfoType.CLAN);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanCreate(player, clan));
        return clan;
    }

    public synchronized void destroyClan(Clan clan) {
        clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));

        ClanEntryManager.getInstance().removeFromClanList(clan.getId());

        final int castleId = clan.getCastleId();
        if (castleId == 0) {
            for (Siege siege : SiegeManager.getInstance().getSieges()) {
                siege.removeSiegeClan(clan);
            }
        }

        final ClanHall hall = ClanHallManager.getInstance().getClanHallByClan(clan);
        if (hall != null) {
            hall.setOwner(null);
        }

        final ClanMember leaderMember = clan.getLeader();
        if (leaderMember == null) {
            clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
        } else {
            clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);
        }

        for (ClanMember member : clan.getMembers()) {
            clan.removeClanMember(member.getObjectId(), 0);
        }

        var clanId = clan.getId();
        clans.remove(clanId);
        IdFactory.getInstance().releaseId(clanId);
        getDAO(ClanDAO.class).deleteClan(clanId);
        CrestTable.getInstance().removeCrests(clan);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanDestroy(leaderMember, clan));
    }

    public void scheduleRemoveClan(Clan clan) {
        ThreadPool.schedule(() -> {
            if (clan.getDissolvingExpiryTime() != 0) {
                destroyClan(clan);
            }
        }, Math.max(clan.getDissolvingExpiryTime() - System.currentTimeMillis(), 300000));
    }

    public boolean isAllyExists(String allyName) {
        for (Clan clan : clans.values()) {
            if ((clan.getAllyName() != null) && clan.getAllyName().equalsIgnoreCase(allyName)) {
                return true;
            }
        }
        return false;
    }

    public void deleteClanWars(int clanId1, int clanId2) {
        final Clan clan1 = getInstance().getClan(clanId1);
        final Clan clan2 = getInstance().getClan(clanId2);

        EventDispatcher.getInstance().notifyEventAsync(new OnClanWarFinish(clan1, clan2));

        clan1.deleteWar(clan2.getId());
        clan2.deleteWar(clan1.getId());
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

        getDAO(ClanDAO.class).deleteClanWar(clanId1, clanId2);
    }

    public List<Clan> getClanAllies(int allianceId) {
        final List<Clan> clanAllies = new ArrayList<>();
        if (allianceId != 0) {
            for (Clan clan : clans.values()) {
                if ((clan != null) && (clan.getAllyId() == allianceId)) {
                    clanAllies.add(clan);
                }
            }
        }
        return clanAllies;
    }

    public void forEachClan(Consumer<Clan> action) {

    }

    public void shutdown() {
        for (Clan clan : clans.values()) {
            clan.updateInDB();
            clan.getWarList().values().forEach(ClanWar::save);
        }
    }

    public static void init() {
        getInstance().load();
    }

    public static ClanTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanTable INSTANCE = new ClanTable();
    }
}
