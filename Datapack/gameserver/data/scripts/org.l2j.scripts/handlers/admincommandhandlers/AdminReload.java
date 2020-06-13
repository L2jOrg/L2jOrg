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
package handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.zone.ZoneManager;

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
            if (!st.hasMoreTokens())
            {
                AdminHtml.showAdminHtml(activeChar, "reload.htm");
                activeChar.sendMessage(RELOAD_USAGE);
                return true;
            }

            final String type = st.nextToken();
            switch (type.toLowerCase()) {
                case "config" -> {
                    Config.load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
                }
                case "access" -> {
                    AdminData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Access.");
                }
                case "npc" -> {
                    NpcData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Npcs.");
                }
                case "quest" -> {
                    if (st.hasMoreElements()) {
                        final String value = st.nextToken();
                        if (!isDigit(value)) {
                            QuestManager.getInstance().reload(value);
                            AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
                        } else {
                            final int questId = Integer.parseInt(value);
                            QuestManager.getInstance().reload(questId);
                            AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
                        }
                    } else {
                        QuestManager.getInstance().reloadAllScripts();
                        BuilderUtil.sendSysMessage(activeChar, "All scripts have been reloaded.");
                        AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
                    }
                }
                case "walker" -> {
                    WalkingManager.getInstance().load();
                    BuilderUtil.sendSysMessage(activeChar, "All walkers have been reloaded");
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Walkers.");
                }
                case "htm", "html" -> {
                    if (st.hasMoreElements()) {
                        var path = "data/html/" + st.nextToken();
                        if (HtmCache.getInstance().purge(path)) {
                            AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htm File:" + path + ".");
                        } else {
                            BuilderUtil.sendSysMessage(activeChar, "Html Cache doesn't contains File or Directory.");
                        }
                    } else {
                        HtmCache.getInstance().reload();
                        AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htms.");
                    }
                }
                case "multisell" -> {
                    MultisellData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Multisells.");
                }
                case "buylist" -> {
                    BuyListData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Buylists.");
                }
                case "teleport" -> {
                    TeleportersData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Teleports.");
                }
                case "skill" -> {
                    SkillEngine.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Skills.");
                }
                case "item" -> {
                    ItemEngine.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Items.");
                }
                case "door" -> {
                    DoorDataManager.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Doors.");
                }
                case "zone" -> {
                    ZoneManager.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Zones.");
                }
                case "crest" -> {
                    CrestTable.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
                }
                case "enchant" -> {
                    EnchantItemEngine.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item enchanting data.");
                }
                case "transform" -> {
                    TransformData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded transform data.");
                }
                case "crystalizable" -> {
                    ItemCrystallizationData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item crystalization data.");
                }
                case "primeshop" -> {
                    PrimeShopData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Prime Shop data.");
                }
                case "sets" -> {
                    ArmorSetsData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Armor sets data.");
                }
                case "options" -> {
                    AugmentationEngine.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Options data.");
                }
                case "fishing" -> {
                    FishingData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Fishing data.");
                }
                case "attendance" -> {
                    AttendanceRewardData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Attendance Reward data.");
                }
                case "instance" -> {
                    InstanceManager.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Instances data.");
                }
                default -> {
                    activeChar.sendMessage(RELOAD_USAGE);
                    return true;
                }
            }
            BuilderUtil.sendSysMessage(activeChar, "WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
        }
        return true;
    }

    @Override
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}
