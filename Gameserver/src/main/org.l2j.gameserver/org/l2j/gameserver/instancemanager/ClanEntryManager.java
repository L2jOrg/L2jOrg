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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.clan.entry.PledgeApplicantInfo;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.clan.entry.PledgeWaitingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Sdw
 */
public class ClanEntryManager {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ClanEntryManager.class);

    private static final Map<Integer, PledgeWaitingInfo> _waitingList = new ConcurrentHashMap<>();
    private static final Map<Integer, PledgeRecruitInfo> _clanList = new ConcurrentHashMap<>();
    private static final Map<Integer, Map<Integer, PledgeApplicantInfo>> _applicantList = new ConcurrentHashMap<>();

    private static final Map<Integer, ScheduledFuture<?>> _clanLocked = new ConcurrentHashMap<>();
    private static final Map<Integer, ScheduledFuture<?>> _playerLocked = new ConcurrentHashMap<>();

    private static final String INSERT_APPLICANT = "INSERT INTO pledge_applicant VALUES (?, ?, ?, ?)";
    private static final String DELETE_APPLICANT = "DELETE FROM pledge_applicant WHERE charId = ? AND clanId = ?";

    private static final String INSERT_WAITING_LIST = "INSERT INTO pledge_waiting_list VALUES (?, ?)";
    private static final String DELETE_WAITING_LIST = "DELETE FROM pledge_waiting_list WHERE char_id = ?";

    private static final String INSERT_CLAN_RECRUIT = "INSERT INTO pledge_recruit VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_CLAN_RECRUIT = "UPDATE pledge_recruit SET karma = ?, information = ?, detailed_information = ?, application_type = ?, recruit_type = ? WHERE clan_id = ?";
    private static final String DELETE_CLAN_RECRUIT = "DELETE FROM pledge_recruit WHERE clan_id = ?";

    //@formatter:off
    private static final List<Comparator<PledgeWaitingInfo>> PLAYER_COMPARATOR = Arrays.asList(
            null,
            Comparator.comparing(PledgeWaitingInfo::getPlayerName),
            Comparator.comparingInt(PledgeWaitingInfo::getKarma),
            Comparator.comparingInt(PledgeWaitingInfo::getPlayerLvl),
            Comparator.comparingInt(PledgeWaitingInfo::getPlayerClassId));
    //@formatter:on

    //@formatter:off
    private static final List<Comparator<PledgeRecruitInfo>> CLAN_COMPARATOR = Arrays.asList(
            null,
            Comparator.comparing(PledgeRecruitInfo::getClanName),
            Comparator.comparing(PledgeRecruitInfo::getClanLeaderName),
            Comparator.comparingInt(PledgeRecruitInfo::getClanLevel),
            Comparator.comparingInt(PledgeRecruitInfo::getKarma));
    //@formatter:on

    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(5);

    private ClanEntryManager() {
        load();
    }

    private static void lockPlayer(int playerId) {
        _playerLocked.put(playerId, ThreadPool.schedule(() ->
        {
            _playerLocked.remove(playerId);
        }, LOCK_TIME));
    }

    private static void lockClan(int clanId) {
        _clanLocked.put(clanId, ThreadPool.schedule(() ->
        {
            _clanLocked.remove(clanId);
        }, LOCK_TIME));
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM pledge_recruit")) {
            while (rs.next()) {
                final int clanId = rs.getInt("clan_id");
                _clanList.put(clanId, new PledgeRecruitInfo(clanId, rs.getInt("karma"), rs.getString("information"), rs.getString("detailed_information"), rs.getInt("application_type"), rs.getInt("recruit_type")));
                // Remove non existing clan data.
                if (ClanTable.getInstance().getClan(clanId) == null)
                {
                    removeFromClanList(clanId);
                }
            }
            LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _clanList.size() + " clan entry");
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Failed to load: ", e);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT a.char_id, a.karma, b.base_class, b.level, b.char_name FROM pledge_waiting_list as a LEFT JOIN characters as b ON a.char_id = b.charId")) {
            while (rs.next()) {
                _waitingList.put(rs.getInt("char_id"), new PledgeWaitingInfo(rs.getInt("char_id"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("base_class"), rs.getString("char_name")));
            }

            LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _waitingList.size() + " player in waiting list");
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Failed to load: ", e);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT a.charId, a.clanId, a.karma, a.message, b.base_class, b.level, b.char_name FROM pledge_applicant as a LEFT JOIN characters as b ON a.charId = b.charId")) {
            while (rs.next()) {
                _applicantList.computeIfAbsent(rs.getInt("clanId"), k -> new ConcurrentHashMap<>()).put(rs.getInt("charId"), new PledgeApplicantInfo(rs.getInt("charId"), rs.getString("char_name"), rs.getInt("level"), rs.getInt("karma"), rs.getInt("clanId"), rs.getString("message")));
            }

            LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _applicantList.size() + " player application");
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Failed to load: ", e);
        }
    }

    public Map<Integer, PledgeWaitingInfo> getWaitingList() {
        return _waitingList;
    }

    public Map<Integer, PledgeRecruitInfo> getClanList() {
        return _clanList;
    }

    public Map<Integer, Map<Integer, PledgeApplicantInfo>> getApplicantList() {
        return _applicantList;
    }

    public Map<Integer, PledgeApplicantInfo> getApplicantListForClan(int clanId) {
        return _applicantList.getOrDefault(clanId, Collections.emptyMap());
    }

    public PledgeApplicantInfo getPlayerApplication(int clanId, int playerId) {
        return _applicantList.getOrDefault(clanId, Collections.emptyMap()).get(playerId);
    }

    public boolean removePlayerApplication(int clanId, int playerId) {
        final Map<Integer, PledgeApplicantInfo> clanApplicantList = _applicantList.get(clanId);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_APPLICANT)) {
            statement.setInt(1, playerId);
            statement.setInt(2, clanId);
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return (clanApplicantList != null) && (clanApplicantList.remove(playerId) != null);
    }

    public boolean addPlayerApplicationToClan(int clanId, PledgeApplicantInfo info) {
        if (!_playerLocked.containsKey(info.getPlayerId())) {
            _applicantList.computeIfAbsent(clanId, k -> new ConcurrentHashMap<>()).put(info.getPlayerId(), info);

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(INSERT_APPLICANT)) {
                statement.setInt(1, info.getPlayerId());
                statement.setInt(2, info.getRequestClanId());
                statement.setInt(3, info.getKarma());
                statement.setString(4, info.getMessage());
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    public OptionalInt getClanIdForPlayerApplication(int playerId) {
        return _applicantList.entrySet().stream().filter(e -> e.getValue().containsKey(playerId)).mapToInt(e -> e.getKey()).findFirst();
    }

    public boolean addToWaitingList(int playerId, PledgeWaitingInfo info) {
        if (!_playerLocked.containsKey(playerId)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(INSERT_WAITING_LIST)) {
                statement.setInt(1, info.getPlayerId());
                statement.setInt(2, info.getKarma());
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            _waitingList.put(playerId, info);
            return true;
        }
        return false;
    }

    public boolean removeFromWaitingList(int playerId) {
        if (_waitingList.containsKey(playerId)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(DELETE_WAITING_LIST)) {
                statement.setInt(1, playerId);
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            _waitingList.remove(playerId);
            lockPlayer(playerId);
            return true;
        }
        return false;
    }

    public boolean addToClanList(int clanId, PledgeRecruitInfo info) {
        if (!_clanList.containsKey(clanId) && !_clanLocked.containsKey(clanId)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(INSERT_CLAN_RECRUIT)) {
                statement.setInt(1, info.getClanId());
                statement.setInt(2, info.getKarma());
                statement.setString(3, info.getInformation());
                statement.setString(4, info.getDetailedInformation());
                statement.setInt(5, info.getApplicationType());
                statement.setInt(6, info.getRecruitType());
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            _clanList.put(clanId, info);
            return true;
        }
        return false;
    }

    public boolean updateClanList(int clanId, PledgeRecruitInfo info) {
        if (_clanList.containsKey(clanId) && !_clanLocked.containsKey(clanId)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(UPDATE_CLAN_RECRUIT)) {
                statement.setInt(1, info.getKarma());
                statement.setString(2, info.getInformation());
                statement.setString(3, info.getDetailedInformation());
                statement.setInt(4, info.getApplicationType());
                statement.setInt(5, info.getRecruitType());
                statement.setInt(6, info.getClanId());
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            return _clanList.replace(clanId, info) != null;
        }
        return false;
    }

    public boolean removeFromClanList(int clanId) {
        if (_clanList.containsKey(clanId)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(DELETE_CLAN_RECRUIT)) {
                statement.setInt(1, clanId);
                statement.executeUpdate();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            _clanList.remove(clanId);
            lockClan(clanId);
            return true;
        }
        return false;
    }

    public List<PledgeWaitingInfo> getSortedWaitingList(int levelMin, int levelMax, int role, int sortBy, boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, PLAYER_COMPARATOR.size() - 1);

        // TODO: Handle Role
        //@formatter:off
        return _waitingList.values().stream()
                .filter(p -> ((p.getPlayerLvl() >= levelMin) && (p.getPlayerLvl() <= levelMax)))
                .sorted(descending ? PLAYER_COMPARATOR.get(sortBy).reversed() : PLAYER_COMPARATOR.get(sortBy))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public List<PledgeWaitingInfo> queryWaitingListByName(String name) {
        return _waitingList.values().stream().filter(p -> p.getPlayerName().toLowerCase().contains(name)).collect(Collectors.toList());
    }

    public List<PledgeRecruitInfo> getSortedClanListByName(String query, int type) {
        return type == 1 ? _clanList.values().stream().filter(p -> p.getClanName().toLowerCase().contains(query)).collect(Collectors.toList()) : _clanList.values().stream().filter(p -> p.getClanLeaderName().toLowerCase().contains(query)).collect(Collectors.toList());
    }

    public PledgeRecruitInfo getClanById(int clanId) {
        return _clanList.get(clanId);
    }

    public boolean isClanRegistred(int clanId) {
        return _clanList.get(clanId) != null;
    }

    public boolean isPlayerRegistred(int playerId) {
        return _waitingList.get(playerId) != null;
    }

    public List<PledgeRecruitInfo> getUnSortedClanList() {
        return new ArrayList<>(_clanList.values());
    }

    public List<PledgeRecruitInfo> getSortedClanList(int clanLevel, int karma, int sortBy, boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, CLAN_COMPARATOR.size() - 1);
        //@formatter:off
        return _clanList.values().stream()
                .filter((p -> (((clanLevel < 0) && (karma >= 0) && (karma != p.getKarma())) || ((clanLevel >= 0) && (karma < 0) && (clanLevel != (p.getClan() != null ? p.getClanLevel() : 0))) || ((clanLevel >= 0) && (karma >= 0) && ((clanLevel != (p.getClan() != null ? p.getClanLevel() : 0)) || (karma != p.getKarma()))))))
                .sorted(descending ? CLAN_COMPARATOR.get(sortBy).reversed() : CLAN_COMPARATOR.get(sortBy))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public long getPlayerLockTime(int playerId) {
        return _playerLocked.get(playerId) == null ? 0 : _playerLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }

    public long getClanLockTime(int playerId) {
        return _clanLocked.get(playerId) == null ? 0 : _clanLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }

    public static ClanEntryManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanEntryManager INSTANCE = new ClanEntryManager();
    }
}
