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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.InitialEquipmentData;
import org.l2j.gameserver.data.xml.impl.InitialShortcutData;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.appearance.Appearance;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.EquipableItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.PcItemTemplate;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 *
 * TODO clean up
 */
public class PlayerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFactory.class);

    private PlayerFactory() {
    }

    public static Player loadPlayer(GameClient client, int playerId) {
        var playerDAO = getDAO(PlayerDAO.class);
        var playerData = playerDAO.findById(playerId);

        if(isNull(playerData)) {
            return null;
        }

        var template = PlayerTemplateData.getInstance().getTemplate(playerData.getClassId());
        Player player = new Player(client, playerData, template);
        client.setPlayer(player);

        player.setVariables(getDAO(PlayerVariablesDAO.class).findById(playerId));
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
        player.setNoble(playerData.isNobless());
        player.getStats().setVitalityPoints(playerData.getVitalityPoints());

        if(Olympiad.getInstance().isHero(playerId)) {
            player.setHero(true);
        }

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

        player.setFistsWeaponItem(player.findFistsWeaponItem());
        player.setUptime(System.currentTimeMillis());
        player._activeClass = playerData.getClassId();

        player.setXYZInvisible(playerData.getX(), playerData.getY(), playerData.getZ());
        player.setLastServerPosition(playerData.getX(), playerData.getY(), playerData.getZ());

        player.setBookMarkSlot(playerData.getBookMarkSlot());
        player.setLang(playerData.getLanguage());

        if (player.isGM()) {
            final long masks = Long.parseLong(player.getCondOverrideKey());
            player.setOverrideCond(masks);
        }

        // Retrieve from the database all secondary data of this Player
        // Note that Clan, Noblesse and Hero skills are given separately and not here.
        // Retrieve from the database all skills of this Player and add them to _skills
        player.restoreCharData();

        // Reward auto-get skills and all available skills if auto-learn skills is true.
        player.rewardSkills();

        if(playerData.getLastAccess() == 0){
            InitialShortcutData.getInstance().registerAllShortcuts(player);
        }

        player.getFreight().restore();
        if (!Config.WAREHOUSE_CACHE) {
            player.getWarehouse();
        }

        EventDispatcher.getInstance().notifyEvent(new OnPlayerLoad(player), Listeners.players());

        // Initialize status update cache
        player.initStatusUpdateCache();

        // Restore current Cp, HP and MP values
        player.setCurrentCp(playerData.getCurrentCp());
        player.setCurrentHp(playerData.getHp());
        player.setCurrentMp(playerData.getMp());

        player.setOriginalCpHpMp(playerData.getCurrentCp(), playerData.getHp(), playerData.getMp());

        if (playerData.getHp() < 0.5) {
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

        player.restoreRandomCraft();

        player.loadRecommendations();
        player.startRecoGiveTask();
        player.startOnlineTimeUpdateTask();

        player.setOnlineStatus(true, false);
        SaveTaskManager.getInstance().registerPlayer(player);

        for (PlayerSelectInfo info : client.getPlayersInfo()) {
            if(info.getObjectId() != player.getObjectId()) {
                player.getAccountChars().put(info.getObjectId(), info.getName());
            }
        }
        return player;
    }

    public static void savePlayerData(PlayerTemplate template, PlayerData data) {
        data.setId(IdFactory.getInstance().getNextId());

        if (Config.STARTING_LEVEL > 1) {
            data.setLevel(Config.STARTING_LEVEL);
            data.setExperience(LevelData.getInstance().getExpForLevel(Config.STARTING_LEVEL));
        }

        if (Config.STARTING_SP > 0) {
            data.setSp(Config.STARTING_SP);
        }

        var hp = template.getBaseHpMax(data.getLevel()) * BaseStats.CON.getValue(template.getBaseCON());
        data.setMaxHp(hp);
        data.setHp(hp);

        var mp = template.getBaseMpMax(data.getLevel()) * BaseStats.MEN.getValue(template.getBaseMEN());
        data.setMaxMp(mp);
        data.setMp(mp);

        if (Config.CUSTOM_STARTING_LOC) {
            data.setX(Config.CUSTOM_STARTING_LOC_X);
            data.setY(Config.CUSTOM_STARTING_LOC_Y);
            data.setZ(Config.CUSTOM_STARTING_LOC_Z);
        } else {
            final Location createLoc = template.getCreationPoint();
            data.setX(createLoc.getX());
            data.setY(createLoc.getY());
            data.setZ(createLoc.getZ());
        }


        data.setRace(template.getRace().ordinal());
        data.setTitleColor(Appearance.DEFAULT_TITLE_COLOR);
        data.setCreateDate(LocalDate.now());

        getDAO(PlayerDAO.class).save(data);
    }

    public static void init(GameClient client, PlayerData data) {

        getDAO(PlayerDAO.class).save(PlayerStatsData.init(data.getCharId()));
        getDAO(PlayerVariablesDAO.class).save(PlayerVariableData.init(data.getCharId(), data.getFace(), data.getHairStyle(), data.getHairColor()));
        getDAO(PlayerDAO.class).resetRecommends();

        addItems(data);

        client.addPlayerInfo(restorePlayerInfo(data));

    }

    private static void addItems(PlayerData data) {
        int nextLocData = 0;
        if(Config.STARTING_ADENA > 0) {
            getDAO(ItemDAO.class).saveItem(data.getCharId(), IdFactory.getInstance().getNextId(), CommonItem.ADENA, Config.STARTING_ADENA, ItemLocation.INVENTORY, nextLocData++);
        }

        final var initialItems = InitialEquipmentData.getInstance().getEquipmentList(data.getClassId());
        for (PcItemTemplate ie : initialItems) {
            ItemTemplate template = ItemEngine.getInstance().getTemplate(ie.getId());

            if(isNull(template)) {
                LOGGER.warn("Could not create item during player creation: itemId {}, amount {}", ie.getId(), ie.getCount());
                continue;
            }

            ItemLocation loc;
            int locData;
            if(ie.isEquipped() && template instanceof EquipableItem equipable) {
                loc = ItemLocation.PAPERDOLL;
                locData = equipable.getBodyPart().slot().getId();
            } else {
                loc = ItemLocation.INVENTORY;
                locData = nextLocData++;
            }

            getDAO(ItemDAO.class).saveItem(data.getCharId(), IdFactory.getInstance().getNextId(), ie.getId(), ie.getCount(), loc, locData);
        }
    }

    public static void deletePlayer(PlayerData data) {
        if(data.getClanId() > 0) {
            final Clan clan = ClanTable.getInstance().getClan(data.getClanId());
            if (clan != null) {
                clan.removeClanMember(data.getCharId(), 0);
            }
        }

        deleteCharByObjId(data.getCharId());
    }

    public static void deleteCharByObjId(int objId) {
        if (objId < 0) {
            return;
        }

        getDAO(PetDAO.class).deleteByOwner(objId);

        var itemDAO = getDAO(ItemDAO.class);
        itemDAO.deleteVariationsByOwner(objId);
        itemDAO.deleteByOwner(objId);
        getDAO(PlayerDAO.class).deleteById(objId);
        PlayerNameTable.getInstance().removeName(objId);
    }

    public static List<PlayerSelectInfo> loadPlayersInfo(GameClient client) {
        PlayerSelectInfo playerInfo;
        List<PlayerSelectInfo> playersInfo = new ArrayList<>(7);
        try {
            for (PlayerData playerData : getDAO(PlayerDAO.class).findPlayersByAccount(client.getAccountName())) {
                playerInfo = restorePlayerInfo(playerData);
                if (nonNull(playerInfo)) {
                    playersInfo.add(playerInfo);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore player info", e);
        }
        return playersInfo;
    }

    private static PlayerSelectInfo restorePlayerInfo(PlayerData data)  {
        final long deleteTime = data.getDeleteTime();
        if (deleteTime > 0 && System.currentTimeMillis() > deleteTime) {
            PlayerFactory.deletePlayer(data);
            return null;
        }
        return new PlayerSelectInfo(data);
    }
}
