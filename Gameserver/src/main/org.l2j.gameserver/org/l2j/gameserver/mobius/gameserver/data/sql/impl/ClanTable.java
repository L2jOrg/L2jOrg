package org.l2j.gameserver.mobius.gameserver.data.sql.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.communitybbs.Manager.ForumsBBSManager;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.mobius.gameserver.enums.UserInfoType;
import org.l2j.gameserver.mobius.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.ClanWar;
import org.l2j.gameserver.mobius.gameserver.model.ClanWar.ClanWarState;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.mobius.gameserver.model.entity.Fort;
import org.l2j.gameserver.mobius.gameserver.model.entity.FortSiege;
import org.l2j.gameserver.mobius.gameserver.model.entity.Siege;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerClanCreate;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerClanDestroy;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.clan.OnClanWarFinish;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PledgeShowMemberListAll;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class loads the clan related data.
 */
public class ClanTable {
    private static final Logger LOGGER = Logger.getLogger(ClanTable.class.getName());
    private final Map<Integer, L2Clan> _clans = new ConcurrentHashMap<>();

    protected ClanTable() {
        // forums has to be loaded before clan data, because of last forum id used should have also memo included
        if (Config.ENABLE_COMMUNITY_BOARD) {
            ForumsBBSManager.getInstance().initRoot();
        }

        // Get all clan ids.
        final List<Integer> cids = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT clan_id FROM clan_data")) {
            while (rs.next()) {
                cids.add(rs.getInt("clan_id"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error restoring ClanTable.", e);
        }

        // Create clans.
        for (int cid : cids) {
            final L2Clan clan = new L2Clan(cid);
            _clans.put(cid, clan);
            if (clan.getDissolvingExpiryTime() != 0) {
                scheduleRemoveClan(clan.getId());
            }
        }

        LOGGER.info(getClass().getSimpleName() + ": Restored " + cids.size() + " clans from the database.");
        allianceCheck();
        restorewars();
    }

    public static ClanTable getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Gets the clans.
     *
     * @return the clans
     */
    public Collection<L2Clan> getClans() {
        return _clans.values();
    }

    /**
     * Gets the clan count.
     *
     * @return the clan count
     */
    public int getClanCount() {
        return _clans.size();
    }

    /**
     * @param clanId
     * @return
     */
    public L2Clan getClan(int clanId) {
        return _clans.get(clanId);
    }

    public L2Clan getClanByName(String clanName) {
        return _clans.values().stream().filter(c -> c.getName().equalsIgnoreCase(clanName)).findFirst().orElse(null);
    }

    /**
     * Creates a new clan and store clan info to database
     *
     * @param player
     * @param clanName
     * @return NULL if clan with same name already exists
     */
    public L2Clan createClan(L2PcInstance player, String clanName) {
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
        if (!Util.isAlphaNumeric(clanName) || (2 > clanName.length())) {
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

        final L2Clan clan = new L2Clan(IdFactory.getInstance().getNextId(), clanName);
        final L2ClanMember leader = new L2ClanMember(clan, player);
        clan.setLeader(leader);
        leader.setPlayerInstance(player);
        clan.store();
        player.setClan(clan);
        player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
        player.setClanPrivileges(new EnumIntBitmask<>(ClanPrivilege.class, true));

        _clans.put(Integer.valueOf(clan.getId()), clan);

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

    public synchronized void destroyClan(int clanId) {
        final L2Clan clan = getClan(clanId);
        if (clan == null) {
            return;
        }

        clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));

        ClanEntryManager.getInstance().removeFromClanList(clan.getId());

        final int castleId = clan.getCastleId();
        if (castleId == 0) {
            for (Siege siege : SiegeManager.getInstance().getSieges()) {
                siege.removeSiegeClan(clan);
            }
        }

        final int fortId = clan.getFortId();
        if (fortId == 0) {
            for (FortSiege siege : FortSiegeManager.getInstance().getSieges()) {
                siege.removeAttacker(clan);
            }
        }

        final ClanHall hall = ClanHallData.getInstance().getClanHallByClan(clan);
        if (hall != null) {
            hall.setOwner(null);
        }

        final L2ClanMember leaderMember = clan.getLeader();
        if (leaderMember == null) {
            clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
        } else {
            clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);
        }

        for (L2ClanMember member : clan.getMembers()) {
            clan.removeClanMember(member.getObjectId(), 0);
        }

        _clans.remove(clanId);
        IdFactory.getInstance().releaseId(clanId);

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?")) {
                ps.setInt(1, clanId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?")) {
                ps.setInt(1, clanId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?")) {
                ps.setInt(1, clanId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?")) {
                ps.setInt(1, clanId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? OR clan2=?")) {
                ps.setInt(1, clanId);
                ps.setInt(2, clanId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM clan_notices WHERE clan_id=?")) {
                ps.setInt(1, clanId);
                ps.execute();
            }

            if (castleId != 0) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE castle SET taxPercent = 0 WHERE id = ?")) {
                    ps.setInt(1, castleId);
                    ps.execute();
                }
            }

            if (fortId != 0) {
                final Fort fort = FortManager.getInstance().getFortById(fortId);
                if (fort != null) {
                    final L2Clan owner = fort.getOwnerClan();
                    if (clan == owner) {
                        fort.removeOwner(true);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error removing clan from DB.", e);
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanDestroy(leaderMember, clan));
    }

    public void scheduleRemoveClan(int clanId) {
        ThreadPoolManager.getInstance().schedule(() ->
        {
            if (getClan(clanId) == null) {
                return;
            }
            if (getClan(clanId).getDissolvingExpiryTime() != 0) {
                destroyClan(clanId);
            }
        }, Math.max(getClan(clanId).getDissolvingExpiryTime() - System.currentTimeMillis(), 300000));
    }

    public boolean isAllyExists(String allyName) {
        for (L2Clan clan : _clans.values()) {
            if ((clan.getAllyName() != null) && clan.getAllyName().equalsIgnoreCase(allyName)) {
                return true;
            }
        }
        return false;
    }

    public void storeclanswars(ClanWar war) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("REPLACE INTO clan_wars (clan1, clan2, clan1Kill, clan2Kill, winnerClan, startTime, endTime, state) VALUES(?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, war.getAttackerClanId());
            ps.setInt(2, war.getAttackedClanId());
            ps.setInt(3, war.getAttackerKillCount());
            ps.setInt(4, war.getAttackedKillCount());
            ps.setInt(5, war.getWinnerClanId());
            ps.setLong(6, war.getStartTime());
            ps.setLong(7, war.getEndTime());
            ps.setInt(8, war.getState().ordinal());
            ps.execute();
        } catch (Exception e) {
            LOGGER.severe("Error storing clan wars data: " + e);
        }
    }

    public void deleteclanswars(int clanId1, int clanId2) {
        final L2Clan clan1 = getInstance().getClan(clanId1);
        final L2Clan clan2 = getInstance().getClan(clanId2);

        EventDispatcher.getInstance().notifyEventAsync(new OnClanWarFinish(clan1, clan2));

        clan1.deleteWar(clan2.getId());
        clan2.deleteWar(clan1.getId());
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? AND clan2=?")) {
            ps.setInt(1, clanId1);
            ps.setInt(2, clanId2);
            ps.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error removing clan wars data.", e);
        }
    }

    private void restorewars() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement();
             ResultSet rset = statement.executeQuery("SELECT clan1, clan2, clan1Kill, clan2Kill, winnerClan, startTime, endTime, state FROM clan_wars")) {
            while (rset.next()) {
                final L2Clan attacker = getClan(rset.getInt("clan1"));
                final L2Clan attacked = getClan(rset.getInt("clan2"));
                if ((attacker != null) && (attacked != null)) {
                    final ClanWarState state = ClanWarState.values()[rset.getInt("state")];

                    final ClanWar clanWar = new ClanWar(attacker, attacked, rset.getInt("clan1Kill"), rset.getInt("clan2Kill"), rset.getInt("winnerClan"), rset.getLong("startTime"), rset.getLong("endTime"), state);
                    attacker.addWar(attacked.getId(), clanWar);
                    attacked.addWar(attacker.getId(), clanWar);
                } else {
                    LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Restorewars one of clans is null attacker:" + attacker + " attacked:" + attacked);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error restoring clan wars data.", e);
        }
    }

    /**
     * Check for nonexistent alliances
     */
    private void allianceCheck() {
        for (L2Clan clan : _clans.values()) {
            final int allyId = clan.getAllyId();
            if ((allyId != 0) && (clan.getId() != allyId) && !_clans.containsKey(allyId)) {
                clan.setAllyId(0);
                clan.setAllyName(null);
                clan.changeAllyCrest(0, true);
                clan.updateClanInDB();
                LOGGER.info(getClass().getSimpleName() + ": Removed alliance from clan: " + clan);
            }
        }
    }

    public List<L2Clan> getClanAllies(int allianceId) {
        final List<L2Clan> clanAllies = new ArrayList<>();
        if (allianceId != 0) {
            for (L2Clan clan : _clans.values()) {
                if ((clan != null) && (clan.getAllyId() == allianceId)) {
                    clanAllies.add(clan);
                }
            }
        }
        return clanAllies;
    }

    public void shutdown() {
        for (L2Clan clan : _clans.values()) {
            clan.updateInDB();
        }
    }

    private static class SingletonHolder {
        protected static final ClanTable _instance = new ClanTable();
    }
}
