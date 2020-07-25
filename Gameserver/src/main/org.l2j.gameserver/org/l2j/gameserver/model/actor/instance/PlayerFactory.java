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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElseGet;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 *
 * TODO clean up
 */
public class PlayerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFactory.class);

    public static Player loadPlayer(GameClient client, int playerId) {
        var playerDAO = getDAO(PlayerDAO.class);
        var playerData = playerDAO.findById(playerId);

        if(isNull(playerData)) {
            return null;
        }

        var template = PlayerTemplateData.getInstance().getTemplate(playerData.getClassId());
        Player player = new Player(playerData, template);
        player.setClient(client);
        client.setPlayer(player);

        player.setVariables(requireNonNullElseGet(getDAO(PlayerVariablesDAO.class).findById(playerId), () -> PlayerVariableData.init(playerId)));
        player.setStatsData(playerDAO.findPlayerStatsData(playerId));

        if(isNull(player.getStatsData())) { // TODO remove late, just temp fix to already created players
            player.setStatsData(PlayerStatsData.init(playerId));
            player.updateCharacteristicPoints();
        }

        player.setTeleportFavorites(playerDAO.findTeleportFavorites(playerId));

        player.setHeading(playerData.getHeading());
        player.getStats().setExp(playerData.getExp());
        player.getStats().setLevel(playerData.getLevel());
        player.getStats().setSp(playerData.getSp());
        player.setReputation(playerData.getReputation());
        player.setFame(playerData.getFame());
        player.setPvpKills(playerData.getPvP());
        player.setPkKills(playerData.getPk());
        player.setOnlineTime(playerData.getOnlineTime());
        player.setNoble(playerData.isNobless());
        player.getStats().setVitalityPoints(playerData.getVitalityPoints());

        player.setHero(Hero.getInstance().isHero(playerId));

        if(player.getLevel() >= 40) {
            player.initElementalSpirits();
        }

        if (playerData.getClanId() > 0) {
            player.setClan(ClanTable.getInstance().getClan(playerData.getClanId()));
        }

        if (player.getClan() != null) {
            if (player.getClan().getLeaderId() != player.getObjectId()) {
                if (player.getPowerGrade() == 0) {
                    player.setPowerGrade(5);
                }
                player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
            } else {
                player.getClanPrivileges().setAll();
                player.setPowerGrade(1);
            }
            player.setPledgeClass(ClanMember.calculatePledgeClass(player));
        } else {
            if (player.isNoble()) {
                player.setPledgeClass(5);
            }

            if (player.isHero()) {
                player.setPledgeClass(8);
            }

            player.getClanPrivileges().clear();
        }

        player.setTitle(playerData.getTitle());

        if (playerData.getTitleColor() != PlayerAppearance.DEFAULT_TITLE_COLOR) {
            player.getAppearance().setTitleColor(playerData.getTitleColor());
        }

        player.setFistsWeaponItem(player.findFistsWeaponItem());
        player.setUptime(System.currentTimeMillis());
        player.setClassIndex(0);
        player._activeClass = playerData.getClassId();

        player.setXYZInvisible(playerData.getX(), playerData.getY(), playerData.getZ());
        player.setLastServerPosition(playerData.getX(), playerData.getY(), playerData.getZ());

        player.setBookMarkSlot(playerData.getBookMarkSlot());
        player.setLang(playerData.getLanguage());

        if (player.isGM()) {
            final long masks = Long.parseLong(player.getCondOverrideKey());
            player.setOverrideCond(masks);
        }

        // Retrieve from the database all items of this Player and add them to _inventory
        player.getInventory().restore();
        // Retrieve from the database all secondary data of this Player
        // Note that Clan, Noblesse and Hero skills are given separately and not here.
        // Retrieve from the database all skills of this Player and add them to _skills
        player.restoreCharData();

        // Reward auto-get skills and all available skills if auto-learn skills is true.
        player.rewardSkills();


        player.getFreight().restore();
        if (!Config.WAREHOUSE_CACHE) {
            player.getWarehouse();
        }

        player.restoreItemReuse();

        // Restore player shortcuts
        player.restoreShortCuts();
        EventDispatcher.getInstance().notifyEvent(new OnPlayerLoad(player), Listeners.players());

        // Initialize status update cache
        player.initStatusUpdateCache();

        // Restore current Cp, HP and MP values
        player.setCurrentCp(playerData.getCurrentCp());
        player.setCurrentHp(playerData.getCurrentHp());
        player.setCurrentMp(playerData.getCurrentMp());

        player.setOriginalCpHpMp(playerData.getCurrentCp(), playerData.getCurrentHp(), playerData.getCurrentMp());

        if (playerData.getCurrentHp() < 0.5) {
            player.setIsDead(true);
            player.stopHpMpRegeneration();
        }

        // Restore pet if exists in the world
        player.setPet(World.getInstance().findPet(player.getObjectId()));
        final Summon pet = player.getPet();
        if (pet != null) {
            pet.setOwner(player);
        }

        if (player.hasServitors()) {
            for (Summon summon : player.getServitors().values()) {
                summon.setOwner(player);
            }
        }

        // Recalculate all stats
        player.getStats().recalculateStats(false);

        // Update the overloaded status of the Player
        player.refreshOverloaded(false);
        player.restoreFriendList();

        player.loadRecommendations();
        player.startRecoGiveTask();
        player.startOnlineTimeUpdateTask();

        player.setOnlineStatus(true, false);
        SaveTaskManager.getInstance().registerPlayer(player);

        // TODO this info should stay on GameClient, since it was already loaded
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=? AND charId<>?")) {
            // Retrieve the Player from the characters table of the database
            stmt.setString(1, playerData.getAccountName());
            stmt.setInt(2, playerId);

            ResultSet chars = stmt.executeQuery();
            while (chars.next()) {
                player.getAccountChars().put(chars.getInt("charId"), chars.getString("char_name"));
            }


        } catch (Exception e) {
            LOGGER.error("Failed loading character.", e);
        }
        return player;
    }

}
