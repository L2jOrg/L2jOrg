/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.data.database.dao.PledgeRecruitDAO;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.data.database.data.PledgeWaitingData;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ClanEntryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClanEntryManager.class);

    private IntMap<PledgeWaitingData> waitings = new CHashIntMap<>();
    private IntMap<PledgeRecruitData> clans = new CHashIntMap<>();
    private final IntMap<IntMap<PledgeApplicantData>> applicants = new CHashIntMap<>();

    private final IntMap<ScheduledFuture<?>> clanLocked = new CHashIntMap<>();
    private final IntMap<ScheduledFuture<?>> playerLocked = new CHashIntMap<>();

    //@formatter:off
    private static final List<Comparator<PledgeWaitingData>> PLAYER_COMPARATOR = Arrays.asList(
            null,
            Comparator.comparing(PledgeWaitingData::getPlayerName),
            Comparator.comparingInt(PledgeWaitingData::getKarma),
            Comparator.comparingInt(PledgeWaitingData::getPlayerLvl),
            Comparator.comparingInt(PledgeWaitingData::getPlayerClassId));
    //@formatter:on

    //@formatter:off
    private static final List<Comparator<PledgeRecruitData>> CLAN_COMPARATOR = Arrays.asList(
            null,
            Comparator.comparing(PledgeRecruitData::getClanName),
            Comparator.comparing(PledgeRecruitData::getClanLeaderName),
            Comparator.comparingInt(PledgeRecruitData::getClanLevel),
            Comparator.comparingInt(PledgeRecruitData::getKarma));
    //@formatter:on

    private static final long LOCK_TIME = TimeUnit.MINUTES.toMillis(5);

    private ClanEntryManager() {
    }

    private void load() {
        final var pledgeRecruitDAO= getDAO(PledgeRecruitDAO.class);

        clans = pledgeRecruitDAO.findAll(pledgeRecruit -> pledgeRecruit.setClan(ClanEngine.getInstance().getClan(pledgeRecruit.getClanId())));
        LOGGER.info("Loaded {} clan entry", clans.size());

        waitings = pledgeRecruitDAO.findAllWaiting();
        LOGGER.info("Loaded {} player in waiting list", waitings.size());

        var applicantList = pledgeRecruitDAO.findAllApplicant();
        for (PledgeApplicantData applicant : applicantList) {
            applicants.computeIfAbsent(applicant.getRequestClanId(), k -> new CHashIntMap<>()).put(applicant.getPlayerId(), applicant);
        }
        LOGGER.info("Loaded {} player applications", applicantList.size());
    }

    private void lockPlayer(int playerId) {
        playerLocked.put(playerId, ThreadPool.schedule(() -> playerLocked.remove(playerId), LOCK_TIME));
    }

    private void lockClan(int clanId) {
        clanLocked.put(clanId, ThreadPool.schedule(() -> clanLocked.remove(clanId), LOCK_TIME));
    }

    public IntMap<PledgeApplicantData> getApplicantListForClan(int clanId) {
        return applicants.getOrDefault(clanId, Containers.emptyIntMap());
    }

    public PledgeApplicantData getPlayerApplication(int clanId, int playerId) {
        return applicants.getOrDefault(clanId, Containers.emptyIntMap()).get(playerId);
    }

    public void removePlayerApplication(int clanId, int playerId) {
        final IntMap<PledgeApplicantData> clanApplicantList = applicants.get(clanId);
        if(nonNull(clanApplicantList) && nonNull(clanApplicantList.remove(playerId))) {
            getDAO(PledgeRecruitDAO.class).deleteApplicant(playerId, clanId);
        }
    }

    public boolean addPlayerApplicationToClan(int clanId, PledgeApplicantData info) {
        if (!playerLocked.containsKey(info.getPlayerId())) {
            applicants.computeIfAbsent(clanId, k -> new CHashIntMap<>()).put(info.getPlayerId(), info);
            getDAO(PledgeRecruitDAO.class).save(info);
            return true;
        }
        return false;
    }

    public OptionalInt getClanIdForPlayerApplication(int playerId) {
        return applicants.entrySet().stream().filter(e -> e.getValue().containsKey(playerId)).mapToInt(IntMap.Entry::getKey).findFirst();
    }

    public boolean addToWaitingList(int playerId, PledgeWaitingData info) {
        if (!playerLocked.containsKey(playerId)) {
            getDAO(PledgeRecruitDAO.class).save(info);
            waitings.put(playerId, info);
            return true;
        }
        return false;
    }

    public boolean removeFromWaitingList(int playerId) {
        if (waitings.containsKey(playerId)) {
            getDAO(PledgeRecruitDAO.class).deleteWaiting(playerId);
            waitings.remove(playerId);
            lockPlayer(playerId);
            return true;
        }
        return false;
    }

    public boolean addToClanList(int clanId, PledgeRecruitData info) {
        if (!clans.containsKey(clanId) && !clanLocked.containsKey(clanId)) {
            getDAO(PledgeRecruitDAO.class).save(info);
            clans.put(clanId, info);
            return true;
        }
        return false;
    }

    public boolean updateClanList(int clanId, PledgeRecruitData info) {
        if (clans.containsKey(clanId) && !clanLocked.containsKey(clanId)) {
            getDAO(PledgeRecruitDAO.class).save(info);
            return clans.replace(clanId, info) != null;
        }
        return false;
    }

    public boolean removeFromClanList(int clanId) {
        if (clans.containsKey(clanId)) {
            getDAO(PledgeRecruitDAO.class).deleteRecruit(clanId);
            clans.remove(clanId);
            lockClan(clanId);
            return true;
        }
        return false;
    }

    public List<PledgeWaitingData> getSortedWaitingList(int levelMin, int levelMax, int role, int sortBy, boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, PLAYER_COMPARATOR.size() - 1);

        // TODO: Handle Role
        //@formatter:off
        return waitings.values().stream()
                .filter(p -> ((p.getPlayerLvl() >= levelMin) && (p.getPlayerLvl() <= levelMax)))
                .sorted(descending ? PLAYER_COMPARATOR.get(sortBy).reversed() : PLAYER_COMPARATOR.get(sortBy))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public List<PledgeWaitingData> queryWaitingListByName(String name) {
        return waitings.values().stream().filter(p -> p.getPlayerName().toLowerCase().contains(name)).collect(Collectors.toList());
    }

    public List<PledgeRecruitData> getSortedClanListByName(String query, int type) {
        return type == 1 ? clans.values().stream().filter(p -> p.getClanName().toLowerCase().contains(query)).collect(Collectors.toList()) : clans.values().stream().filter(p -> p.getClanLeaderName().toLowerCase().contains(query)).collect(Collectors.toList());
    }

    public PledgeRecruitData getClanById(int clanId) {
        return clans.get(clanId);
    }

    public boolean isClanRegistred(int clanId) {
        return clans.get(clanId) != null;
    }

    public boolean isPlayerRegistred(int playerId) {
        return waitings.get(playerId) != null;
    }

    public List<PledgeRecruitData> getUnSortedClanList() {
        return new ArrayList<>(clans.values());
    }

    public List<PledgeRecruitData> getSortedClanList(int clanLevel, int karma, int sortBy, boolean descending) {
        sortBy = CommonUtil.constrain(sortBy, 1, CLAN_COMPARATOR.size() - 1);
        //@formatter:off
        return clans.values().stream()
                .filter((p -> (((clanLevel < 0) && (karma >= 0) && (karma != p.getKarma())) || ((clanLevel >= 0) && (karma < 0) && (clanLevel != (p.getClan() != null ? p.getClanLevel() : 0))) || ((clanLevel >= 0) && (karma >= 0) && ((clanLevel != (p.getClan() != null ? p.getClanLevel() : 0)) || (karma != p.getKarma()))))))
                .sorted(descending ? CLAN_COMPARATOR.get(sortBy).reversed() : CLAN_COMPARATOR.get(sortBy))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public long getPlayerLockTime(int playerId) {
        return playerLocked.get(playerId) == null ? 0 : playerLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }

    public long getClanLockTime(int playerId) {
        return clanLocked.get(playerId) == null ? 0 : clanLocked.get(playerId).getDelay(TimeUnit.MINUTES);
    }


    public static void init() {
        getInstance().load();
    }

    public static ClanEntryManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanEntryManager INSTANCE = new ClanEntryManager();
    }
}
