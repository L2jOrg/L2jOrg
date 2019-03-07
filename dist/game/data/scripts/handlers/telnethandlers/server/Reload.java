/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.telnethandlers.server;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.cache.HtmCache;
import com.l2jmobius.gameserver.data.sql.impl.CrestTable;
import com.l2jmobius.gameserver.data.xml.impl.AdminData;
import com.l2jmobius.gameserver.data.xml.impl.AppearanceItemData;
import com.l2jmobius.gameserver.data.xml.impl.ArmorSetsData;
import com.l2jmobius.gameserver.data.xml.impl.BuyListData;
import com.l2jmobius.gameserver.data.xml.impl.DoorData;
import com.l2jmobius.gameserver.data.xml.impl.EnchantItemData;
import com.l2jmobius.gameserver.data.xml.impl.EnchantItemGroupsData;
import com.l2jmobius.gameserver.data.xml.impl.ItemCrystallizationData;
import com.l2jmobius.gameserver.data.xml.impl.MultisellData;
import com.l2jmobius.gameserver.data.xml.impl.NpcData;
import com.l2jmobius.gameserver.data.xml.impl.SayuneData;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.data.xml.impl.TeleportersData;
import com.l2jmobius.gameserver.data.xml.impl.TransformData;
import com.l2jmobius.gameserver.datatables.ItemTable;
import com.l2jmobius.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jmobius.gameserver.instancemanager.QuestManager;
import com.l2jmobius.gameserver.instancemanager.WalkingManager;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.scripting.ScriptEngineManager;
import com.l2jmobius.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Reload implements ITelnetCommand
{
	private static final Logger LOGGER = Logger.getLogger(Reload.class.getName());
	
	@Override
	public String getCommand()
	{
		return "reload";
	}
	
	@Override
	public String getUsage()
	{
		return "Reload <zone/multisell/teleport/skill/npc/htm/item/config/npcwalkers/access/quests/door/primeshop/html/script>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		switch (args[0])
		{
			case "config":
			{
				Config.load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Configs.");
			}
			case "access":
			{
				AdminData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Access.");
			}
			case "npc":
			{
				NpcData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Npcs.");
			}
			case "quest":
			{
				if (args.length > 1)
				{
					final String value = args[1];
					if (!Util.isDigit(value))
					{
						QuestManager.getInstance().reload(value);
						return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quest Name:" + value + ".");
					}
					final int questId = Integer.parseInt(value);
					QuestManager.getInstance().reload(questId);
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quest ID:" + questId + ".");
				}
				QuestManager.getInstance().reloadAllScripts();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Quests.");
			}
			case "walker":
			{
				WalkingManager.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Walkers.");
			}
			case "htm":
			case "html":
			{
				if (args.length > 1)
				{
					final String path = args[1];
					final File file = new File(Config.DATAPACK_ROOT, "data/html/" + path);
					if (file.exists())
					{
						HtmCache.getInstance().reload(file);
						return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Htm File:" + file.getName() + ".");
					}
					return "File or Directory does not exist.";
				}
				HtmCache.getInstance().reload();
				AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Htms.");
				return "Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage() + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " files loaded";
			}
			case "multisell":
			{
				MultisellData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Multisells.");
			}
			case "buylist":
			{
				BuyListData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Buylists.");
			}
			case "teleport":
			{
				TeleportersData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Teleports.");
			}
			case "skill":
			{
				SkillData.getInstance().reload();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Skills.");
			}
			case "item":
			{
				ItemTable.getInstance().reload();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Items.");
			}
			case "door":
			{
				DoorData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Doors.");
			}
			case "zone":
			{
				ZoneManager.getInstance().reload();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Zones.");
			}
			case "cw":
			{
				CursedWeaponsManager.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Cursed Weapons.");
			}
			case "crest":
			{
				CrestTable.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Crests.");
			}
			case "effect":
			{
				try
				{
					ScriptEngineManager.getInstance().executeEffectMasterHandler();
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded effect master handler.");
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed executing effect master handler!", e);
					return "Error reloading effect master handler: " + e.getMessage();
				}
			}
			case "handler":
			{
				try
				{
					ScriptEngineManager.getInstance().executeMasterHandler();
					return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded master handler.");
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed executing master handler!", e);
					return "Error reloading master handler: " + e.getMessage();
				}
			}
			case "enchant":
			{
				EnchantItemGroupsData.getInstance().load();
				EnchantItemData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded item enchanting data.");
			}
			case "transform":
			{
				TransformData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded transform data.");
			}
			case "crystalizable":
			{
				ItemCrystallizationData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded item crystalization data.");
			}
			case "appearance":
			{
				AppearanceItemData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded appearance item data.");
			}
			case "sayune":
			{
				SayuneData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Sayune data.");
			}
			case "sets":
			{
				ArmorSetsData.getInstance().load();
				return AdminData.getInstance().broadcastMessageToGMs("Telnet Admin: Reloaded Armor sets data.");
			}
			case "script":
			{
				if (args.length < 2)
				{
					return "Syntax: reload script <path>";
				}
				try
				{
					ScriptEngineManager.getInstance().executeScript(Paths.get(args[1]));
					return "Script " + args[1] + " has been reloaded successfuly.";
				}
				catch (Exception e)
				{
					return "Couldn't reload script: " + args[1] + " err: " + e.getMessage();
				}
			}
		}
		return null;
	}
}
