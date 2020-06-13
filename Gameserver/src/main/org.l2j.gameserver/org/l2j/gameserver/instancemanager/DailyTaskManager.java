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
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author UnAfraid
 */
public class DailyTaskManager extends AbstractEventManager<AbstractEvent<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyTaskManager.class);

    private DailyTaskManager() {
    }

    @Override
    public void onInitialized() {
    }

    @ScheduleTarget
    private void onReset() {
        resetClanBonus();
        resetExtendDrop();
        resetDailyMissionRewards();
        resetDailySkills();
        resetRecommends();
        resetWorldChatPoints();
        resetTrainingCamp();
        resetVipTierExpired();
        resetRankers();
        resetRevengeData();
    }

    @ScheduleTarget
    private void onSave() {
        GlobalVariablesManager.getInstance().storeMe();

        if (Olympiad.getInstance().inCompPeriod()) {
            Olympiad.getInstance().saveOlympiadStatus();
            LOGGER.info("Olympiad System: Data updated.");
        }
    }



    @ScheduleTarget
    private void onClansTask(){
        onClanLeaderApply();
        GlobalVariablesManager.getInstance().resetRaidBonus();
        onClanResetRaids();
    }

    private void onClanResetRaids() {
        ClanTable.getInstance().forEachClan(clan ->{
            ClanRewardManager.getInstance().checkArenaProgress(clan);
        });
    }

    private void onClanLeaderApply() {
        for (Clan clan : ClanTable.getInstance().getClans()) {
            if (clan.getNewLeaderId() != 0) {
                final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
                if (member == null) {
                    continue;
                }

                clan.setNewLeader(member);
            }
        }
        LOGGER.info("Clan leaders has been updated");
    }

    @ScheduleTarget
    private void onVitalityReset() {
        if (!Config.ENABLE_VITALITY) {
            return;
        }

        for (Player player : World.getInstance().getPlayers()) {
            player.setVitalityPoints(PlayerStats.MAX_VITALITY_POINTS, false);

            for (SubClass subclass : player.getSubClasses().values()) {
                subclass.setVitalityPoints(PlayerStats.MAX_VITALITY_POINTS);
            }
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement st = con.prepareStatement("UPDATE character_subclasses SET vitality_points = ?")) {
                st.setInt(1, PlayerStats.MAX_VITALITY_POINTS);
                st.execute();
            }

            try (PreparedStatement st = con.prepareStatement("UPDATE characters SET vitality_points = ?")) {
                st.setInt(1, PlayerStats.MAX_VITALITY_POINTS);
                st.execute();
            }
        } catch (Exception e) {
            LOGGER.warn("Error while updating vitality", e);
        }
        LOGGER.info("Vitality resetted");
    }

    private void resetClanBonus() {
        ClanTable.getInstance().getClans().forEach(Clan::resetClanBonus);
        LOGGER.info("Daily clan bonus has been resetted.");
    }

    private void resetExtendDrop() {
        // Update data for offline players.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ?")) {
            ps.setString(1, PlayerVariables.EXTEND_DROP);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Could not reset extend drop : ", e);
        }

        // Update data for online players.
        World.getInstance().getPlayers().forEach(player ->
        {
            player.getVariables().remove(PlayerVariables.EXTEND_DROP);
            player.getVariables().storeMe();
        });

        LOGGER.info("Daily Extend Drop has been resetted.");
    }

    private void resetDailySkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            final List<SkillHolder> dailySkills = getVariables().getList("reset_skills", SkillHolder.class, Collections.emptyList());
            for (SkillHolder skill : dailySkills) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE skill_id=?;")) {
                    ps.setInt(1, skill.getSkillId());
                    ps.execute();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not reset daily skill reuse: ", e);
        }
        LOGGER.info("Daily skill reuse cleaned.");
    }

    private void resetRevengeData() {
        getDAO(PlayerVariablesDAO.class).resetRevengeData();
        World.getInstance().forEachPlayer(player -> player.resetRevengeData());
    }

    private void resetWorldChatPoints() {
        if (!getSettings(ChatSettings.class).worldChatEnabled()) {
            return;
        }

        // Update data for offline players.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET val = ? WHERE var = ?")) {
            ps.setInt(1, 0);
            ps.setString(2, PlayerVariables.WORLD_CHAT_VARIABLE_NAME);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Could not reset daily world chat points: ", e);
        }

        // Update data for online players.
        World.getInstance().getPlayers().forEach(player ->
        {
            player.setWorldChatUsed(0);
            player.sendPacket(new ExWorldChatCnt(player));
            player.getVariables().storeMe();
        });

        LOGGER.info("Daily world chat points has been resetted.");
    }

    private void resetRecommends() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = 0 WHERE rec_have <= 20")) {
                ps.setInt(1, 0); // Rec left = 0
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = GREATEST(rec_have - 20,0) WHERE rec_have > 20")) {
                ps.setInt(1, 0); // Rec left = 0
                ps.execute();
            }
        } catch (Exception e) {
            LOGGER.error("Could not reset Recommendations System: ", e);
        }

        World.getInstance().getPlayers().forEach(player ->
        {
            player.setRecomLeft(0);
            player.setRecomHave(player.getRecomHave() - 20);
            player.sendPacket(new ExVoteSystemInfo(player));
            player.broadcastUserInfo();
        });
    }

    private void resetTrainingCamp() {
        if (Config.TRAINING_CAMP_ENABLE) {
            // Update data for offline players.
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ?")) {
                ps.setString(1, "TRAINING_CAMP_DURATION");
                ps.executeUpdate();
            } catch (Exception e) {
                LOGGER.error("Could not reset Training Camp: ", e);
            }

            // Update data for online players.
            World.getInstance().getPlayers().forEach(player ->
            {
                player.resetTraingCampDuration();
                player.getAccountVariables().storeMe();
            });

            LOGGER.info("Training Camp daily time has been resetted.");
        }
    }

    private void resetVipTierExpired() {
        World.getInstance().getPlayers().forEach(player -> {
            if(player.getVipTier() < 1) {
                return;
            }

            VipEngine.getInstance().checkVipTierExpiration(player);
        });
        LOGGER.info("VIP expiration time has been checked.");
    }

    private void resetRankers() {
        RankManager.getInstance().updateRankers();
    }

    private void resetDailyMissionRewards() {
        var scheduler = getScheduler("reset");
        long lastReset = nonNull(scheduler) ? scheduler.getLastRun() : 0;
        MissionData.getInstance().getMissions().forEach(mission -> mission.reset(lastReset));
    }

    public static DailyTaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DailyTaskManager INSTANCE = new DailyTaskManager();
    }
}
