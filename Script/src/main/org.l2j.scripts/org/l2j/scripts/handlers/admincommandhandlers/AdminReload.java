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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.engine.fishing.FishingEngine;
import org.l2j.gameserver.engine.item.AttendanceEngine;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.transform.TransformEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.DailyTaskManager;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.zone.ZoneEngine;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isDigit;

/**
 * @author NosBit
 * @author JoeAlisson
 */
public class AdminReload implements IAdminCommandHandler {

    private static final String[] ADMIN_COMMANDS = { "admin_reload" };

    private static final String RELOAD_USAGE = "Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|enchant|options|fishing>";

    @Override
    public boolean useAdminCommand(String command, Player activeChar)
    {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equalsIgnoreCase("admin_reload"))
        {
            if (!st.hasMoreTokens()) {
                AdminHtml.showAdminHtml(activeChar, "reload.htm");
                activeChar.sendMessage(RELOAD_USAGE);
            }  else {
                doReload(activeChar, st);
                BuilderUtil.sendSysMessage(activeChar, "WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
            }
            return true;
        }
        return false;
    }

    private void doReload(Player player, StringTokenizer st) {
        final String type = st.nextToken();
        switch (type.toLowerCase()) {
            case "config" -> reloadConfig(player);
            case "access" -> reloadAccess(player);
            case "npc" -> reloadNpc(player);
            case "quest" -> reloadquest(player, st);
            case "walker" -> reloadWalker(player);
            case "htm", "html" -> reloadHtml(player, st);
            case "multisell" -> reloadMultisell(player);
            case "buylist" -> reloadBuyList(player);
            case "teleport" -> reloadTeleport(player);
            case "skill" -> reloadSkills(player);
            case "item" -> reloadItem(player);
            case "door" -> reloadDoor(player);
            case "zone" -> reloadZone(player);
            case "crest" -> reloadCrest(player);
            case "enchant" -> reloadEnchant(player);
            case "transform" -> reloadTransform(player);
            case "crystalizable" -> reloadCrystalizable(player);
            case "primeshop" -> reloadPrimeShop(player);
            case "sets" -> reloadSets(player);
            case "options" -> reloadOptions(player);
            case "fishing" -> reloadFishing(player);
            case "attendance" -> reloadAttendance(player);
            case "instance" -> reloadInstnaces(player);
            case "dailytasks" -> reloadDailyTasks(player);
            case "rankings" -> reloadRanking(player);
            default -> player.sendMessage(RELOAD_USAGE);
        }
    }

    private void reloadRanking(Player player) {
        RankEngine.getInstance().updateRankers();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded ranks.");
    }

    private void reloadDailyTasks(Player player) {
        DailyTaskManager.getInstance().onReset();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded daily tasks.");
    }

    private void reloadInstnaces(Player player) {
        InstanceManager.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Instances data.");
    }

    private void reloadAttendance(Player player) {
        AttendanceEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Attendance Reward data.");
    }

    private void reloadFishing(Player player) {
        FishingEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Fishing data.");
    }

    private void reloadOptions(Player player) {
        AugmentationEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Options data.");
    }

    private void reloadSets(Player player) {
        ArmorSetsData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Armor sets data.");
    }

    private void reloadPrimeShop(Player player) {
        L2Store.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Prime Shop data.");
    }

    private void reloadCrystalizable(Player player) {
        ItemCrystallizationData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded item crystalization data.");
    }

    private void reloadTransform(Player player) {
        TransformEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded transform data.");
    }

    private void reloadEnchant(Player player) {
        EnchantItemEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded item enchanting data.");
    }

    private void reloadCrest(Player player) {
        CrestTable.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Crests.");
    }

    private void reloadZone(Player player) {
        ZoneEngine.getInstance().reload();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Zones.");
    }

    private void reloadDoor(Player player) {
        DoorDataManager.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Doors.");
    }

    private void reloadItem(Player player) {
        ItemEngine.getInstance().reload();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Items.");
    }

    private void reloadSkills(Player player) {
        SkillEngine.getInstance().reload();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Skills.");
    }

    private void reloadTeleport(Player player) {
        TeleportersData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Teleports.");
    }

    private void reloadBuyList(Player player) {
        BuyListData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Buylists.");
    }

    private void reloadMultisell(Player player) {
        MultisellEngine.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Multisells.");
    }

    private void reloadHtml(Player player, StringTokenizer st) {
        if (st.hasMoreElements()) {
            var path = "data/html/" + st.nextToken();
            if (HtmCache.getInstance().purge(path)) {
                AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Htm File:" + path + ".");
            } else {
                BuilderUtil.sendSysMessage(player, "Html Cache doesn't contains File or Directory.");
            }
        } else {
            HtmCache.getInstance().reload();
            AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Htms.");
        }
    }

    private void reloadWalker(Player player) {
        WalkingManager.getInstance().load();
        BuilderUtil.sendSysMessage(player, "All walkers have been reloaded");
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Walkers.");
    }

    private void reloadquest(Player player, StringTokenizer st) {
        if (st.hasMoreElements()) {
            final String value = st.nextToken();
            if (!isDigit(value)) {
                QuestManager.getInstance().reload(value);
                AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Quest Name:" + value + ".");
            } else {
                final int questId = Integer.parseInt(value);
                QuestManager.getInstance().reload(questId);
                AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Quest ID:" + questId + ".");
            }
        } else {
            QuestManager.getInstance().reloadAllScripts();
            BuilderUtil.sendSysMessage(player, "All scripts have been reloaded.");
            AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Quests.");
        }
    }

    private void reloadNpc(Player player) {
        NpcData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Npcs.");
    }

    private void reloadAccess(Player player) {
        AdminData.getInstance().load();
        AdminData.getInstance().broadcastMessageToGMs(player.getName() + ": Reloaded Access.");
    }

    private void reloadConfig(Player activeChar) {
        Config.load();
        AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
    }

    @Override
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}
