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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

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
        resetDailyMissionRewards();
        resetDailySkills();
        resetRankers();
        resetPlayersData();
    }

    private void resetPlayersData() {
        // TODO block enter world until this method finish
        World.getInstance().forEachPlayer(player -> {
            player.setExtendDrop("");

            player.setRecomLeft(0);
            player.setRecomHave(player.getRecomHave() - 20);
            player.sendPacket(new ExVoteSystemInfo(player));

            if (getSettings(ChatSettings.class).worldChatEnabled()) {
                player.setWorldChatUsed(0);
                player.sendPacket(new ExWorldChatCnt(player));
            }

            if (Config.TRAINING_CAMP_ENABLE) {
                player.resetTraingCampDuration();
                player.getAccountVariables().storeMe();
            }

            if(player.getVipTier() > 0) {
                VipEngine.getInstance().checkVipTierExpiration(player);
            }

            player.storeVariables();
            player.resetRevengeData();
            player.broadcastUserInfo();
        });

        getDAO(PlayerVariablesDAO.class).resetExtendDrop();
        LOGGER.info("Daily Extend Drop has been reset.");

        getDAO(PlayerDAO.class).resetRecommends();

        if (getSettings(ChatSettings.class).worldChatEnabled()) {
            getDAO(PlayerVariablesDAO.class).resetWorldChatPoint();
            LOGGER.info("Daily world chat points has been reset.");
        }

        if (Config.TRAINING_CAMP_ENABLE) {
            getDAO(AccountDAO.class).deleteAccountVariable("TRAINING_CAMP_DURATION");
            LOGGER.info("Training Camp daily time has been resetted.");
        }

        getDAO(PlayerVariablesDAO.class).resetRevengeData();
    }

    @ScheduleTarget
    private void onSave() {
        GlobalVariablesManager.getInstance().storeMe();
    }

    @ScheduleTarget
    private void onClansTask(){
        for (Clan clan : ClanTable.getInstance().getClans()) {
            checkNewLeader(clan);
            ClanRewardManager.getInstance().checkArenaProgress(clan);
        }
        GlobalVariablesManager.getInstance().resetRaidBonus();
        LOGGER.info("Clans has been updated");
    }

    private void checkNewLeader(Clan clan) {
        if (clan.getNewLeaderId() != 0) {
            final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
            if(nonNull(member)) {
                clan.setNewLeader(member);
            }
        }
    }

    @ScheduleTarget
    private void onVitalityReset() {
        if (!Config.ENABLE_VITALITY) {
            return;
        }

        World.getInstance().forEachPlayer(player -> player.setVitalityPoints(PlayerStats.MAX_VITALITY_POINTS, false));

        getDAO(PlayerDAO.class).resetVitality(PlayerStats.MAX_VITALITY_POINTS);
        LOGGER.info("Vitality has been reset");
    }

    private void resetClanBonus() {
        ClanTable.getInstance().getClans().forEach(Clan::resetClanBonus);
        LOGGER.info("Daily clan bonus has been reset.");
    }

    private void resetDailySkills() {
        for (SkillHolder skill : getVariables().getList("reset_skills", SkillHolder.class, Collections.emptyList())) {
            getDAO(PlayerDAO.class).deleteSkillSave(skill.getSkillId());
        }
        LOGGER.info("Daily skill reuse cleaned.");
    }

    private void resetRankers() {
        RankEngine.getInstance().updateRankers();
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
