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
package org.l2j.gameserver.engine.siege;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.SiegeDAO;
import org.l2j.gameserver.data.database.data.SiegeParticipant;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.ControlTower;
import org.l2j.gameserver.model.actor.instance.FlameTower;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.SystemMessageId.S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED;
import static org.l2j.gameserver.network.SystemMessageId.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Siege extends AbstractEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Siege.class);

    private final Castle castle;
    private final IntMap<SiegeParticipant> attackers = new CHashIntMap<>();
    private final IntMap<SiegeParticipant> defenders = new CHashIntMap<>();
    private final List<ControlTower> controlTowers = new ArrayList<>();
    private final List<FlameTower> flameTowers = new ArrayList<>();

    private Clan owner;
    private SiegeState state = SiegeState.NONE;

    public Siege(Castle castle) {
        this.castle = requireNonNull(castle);
        initOwner(castle);
    }

    private void initOwner(Castle castle) {
        owner = castle.getOwner();
        if(nonNull(owner)) {
            final var siegeClan = new SiegeParticipant(owner.getId(), SiegeParticipantStatus.OWNER, castle.getId());
            defenders.put(owner.getId(), siegeClan);
            getDAO(SiegeDAO.class).save(siegeClan);
        }
    }

    @Override
    public void sendMessage(SystemMessageId messageId) {

    }

    void registerAttacker(Clan clan) {
        var siegeClan = new SiegeParticipant(clan.getId(), SiegeParticipantStatus.ATTACKER, castle.getId());
        attackers.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void registerDefender(Clan clan) {
        var siegeClan = new SiegeParticipant(clan.getId(), SiegeParticipantStatus.WAITING, castle.getId());
        defenders.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void removeSiegeClan(Clan clan) {
        attackers.remove(clan.getId());
        defenders.remove(clan.getId());
        getDAO(CastleDAO.class).deleteSiegeClanByCastle(clan.getId(), castle.getId());
    }

    @Override
    public void sendPacket(ServerPacket packet) {

    }

    boolean isRegistered(Clan clan) {
        return attackers.containsKey(clan.getId()) || defenders.containsKey(clan.getId());
    }

    public Castle getCastle() {
        return castle;
    }

    public SiegeState getState() {
        return state;
    }

    boolean isInPreparation() {
        return state == SiegeState.PREPARATION;
    }

    int registeredAttackersAmount() {
        return attackers.size();
    }

    int registeredDefendersAmount() {
        return defenders.size();
    }

    void setState(SiegeState state) {
        this.state = state;
    }

    public int currentStateRemainTime() {
        return switch (state) {
            case PREPARATION -> SiegeEngine.getInstance().remainTimeToStart();
            case STARTED -> SiegeEngine.getInstance().remainTimeToFinish();
            default -> 0;
        };
    }

    public Collection<SiegeParticipant> getDefenderClans() {
        return defenders.values();
    }

    public Collection<SiegeParticipant> getAttackerClans() {
        return attackers.values();
    }

    void registerMercenaryRecruitment(Clan clan, long reward) {
        SiegeParticipant clanSiegeData = getSiegeParticipant(clan);

        if(nonNull(clanSiegeData)) {
            clanSiegeData.setRecruitingMercenary(true);
            clanSiegeData.setMercenaryReward(reward);
            getDAO(SiegeDAO.class).save(clanSiegeData);
        }
    }

    boolean isRecruitingMercenary(Clan clan) {
        SiegeParticipant clanSiegeData = getSiegeParticipant(clan);
        return nonNull(clanSiegeData) && clanSiegeData.isRecruitingMercenary();
    }

    SiegeParticipant getSiegeParticipant(Clan clan) {
        var siegeClan = attackers.get(clan.getId());
        if (isNull(siegeClan)) {
            siegeClan = defenders.get(clan.getId());
        }
        return siegeClan;
    }

    void removeMercenaryRecruitment(Clan clan) {
         final var siegeClanData = getSiegeParticipant(clan);
         siegeClanData.setMercenaryReward(0);
         siegeClanData.setRecruitingMercenary(false);
         getDAO(SiegeDAO.class).save(siegeClanData);
    }

    void joinMercenary(Player player, Clan clan) {
        final var participant = getSiegeParticipant(clan);
        if(nonNull(participant)) {
            participant.addMercenary(Mercenary.of(player));
        }
    }

    public void leaveMercenary(Player player, Clan clan) {
        final var participant = getSiegeParticipant(clan);
        if(nonNull(participant)) {
            participant.removeMercenary(player.getId());
        }
    }

    boolean isAttacker(Clan clan) {
        return attackers.containsKey(clan.getId());
    }

    boolean isDefender(Clan clan) {
        return defenders.containsKey(clan.getId());
    }

    boolean hasStarted() {
        return state == SiegeState.STARTED;
    }

    boolean isMercenary(int playerId) {
        for (SiegeParticipant clan : attackers.values()) {
            if(clan.hasMercenary(playerId)) {
                return true;
            }
        }

        for (SiegeParticipant clan : defenders.values()) {
            if(clan.hasMercenary(playerId)) {
                return true;
            }
        }
        return false;
    }

    Collection<Mercenary> getMercenaries(Clan clan) {
        final var siegeClan = getSiegeParticipant(clan);
        return siegeClan.getMercenaries();
    }

    public void removeMercenary(int playerId) {
        for (SiegeParticipant participant : attackers.values()) {
            if(participant.hasMercenary(playerId)) {
                participant.removeMercenary(playerId);
                return;
            }
        }
        for (SiegeParticipant participant : defenders.values()) {
            if(participant.hasMercenary(playerId)) {
                participant.removeMercenary(playerId);
                return;
            }
        }
    }

    void start() {
        filterNotAcceptedDefenders();
        state = SiegeState.STARTED;

        updateParticipantState();
        expelPossibleAttackers();

        castle.spawnDoor();
        spawnControlTowers();
        spawnFlameTowers();
        var zone = castle.getZone();
        zone.setIsActive(true);
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_S1_SIEGE_HAS_STARTED).addCastleId(castle.getId()), PlaySound.sound("systemmsg_eu.17"));
    }

    private void spawnFlameTowers() {
        SiegeEngine.getInstance().flameTowersOf(castle).forEach(this::spawnFlameTower);
    }

    private void spawnFlameTower(TowerSpawn towerSpawn) {
        try {
            final var spawn = new Spawn(towerSpawn.getId());
            spawn.setLocation(towerSpawn.getLocation());
            final var tower = (FlameTower) spawn.doSpawn();
            tower.setUpgradeLevel(towerSpawn.getUpgradeLevel());
            tower.setZoneList(towerSpawn.getZoneList());
            flameTowers.add(tower);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.error("Could not spawn flame tower in {}'s Castle Siege {}", castle, e);
        }
    }

    private void spawnControlTowers() {
        SiegeEngine.getInstance().controlTowersOf(castle).forEach(this::spawnControlTower);
    }

    private void spawnControlTower(TowerSpawn towerSpawn) {
        try {
            final var spawn = new Spawn(towerSpawn.getId());
            spawn.setLocation(towerSpawn.getLocation());
            controlTowers.add((ControlTower) spawn.doSpawn());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.error("Could not spawn control tower in {}'s Castle Siege {}", castle, e);
        }
    }

    private void expelPossibleAttackers() {
        castle.getZone().forEachPlayer(p -> p.teleToLocation(TeleportWhereType.TOWN), this::isPossibleAttacker);
    }

    private boolean isPossibleAttacker(Player player) {
        var clan = player.getClan();
        return (isNull(clan) || !defenders.containsKey(clan.getId())) && !player.isInObserverMode();
    }

    private void updateParticipantState() {
        for (SiegeParticipant participant : attackers.values()) {
            var clan = ClanTable.getInstance().getClan(participant.getClanId());
            clan.forEachOnlineMember(this::setAttackerState);
        }

        for (SiegeParticipant participant : defenders.values()) {
            var clan = ClanTable.getInstance().getClan(participant.getClanId());
            clan.forEachOnlineMember(this::setDefenderState);
        }
    }

    private void setDefenderState(Player player) {
        setSiegeState(player, (byte) 2);
    }

    private void setAttackerState(Player player) {
        setSiegeState(player, (byte) 1);
    }

    // TODO change magic to enum
    private void setSiegeState(Player player, byte state) {
        player.setSiegeState(state);
        player.setSiegeSide(castle.getId());
        if(isInsideZone(player)) {
            player.setIsInSiege(true);
        }
        player.broadcastUserInfo(UserInfoType.RELATION);

    }

    private boolean isInsideZone(ILocational loc) {
        return castle.checkIfInZone(loc);
    }

    private void filterNotAcceptedDefenders() {
        final var it = defenders.values().iterator();
        while(it.hasNext()) {
            var defender = it.next();
            switch (defender.getStatus()) {
                case DECLINED, WAITING -> it.remove();
            }
        }
    }

    void cancelDueNoInterest() {
        SystemMessage message;
        if(nonNull(owner)) {
            message = getSystemMessage(S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED);
        } else {
            message = getSystemMessage(THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
        }
        Broadcast.toAllOnlinePlayers(message.addCastleId(castle.getId()));
        state = SiegeState.NONE;
    }

    boolean hasAttackers() {
        return !attackers.isEmpty();
    }
}
