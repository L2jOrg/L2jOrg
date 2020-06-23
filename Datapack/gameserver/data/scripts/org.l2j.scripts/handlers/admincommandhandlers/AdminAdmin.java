/*
 * Copyright © 2019 L2J Mobius
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
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isDigit;
import static org.l2j.gameserver.util.GameUtils.isPlayer;


/**
 * This class handles following admin commands: - admin|admin1/admin2/admin3/admin4/admin5 = slots for the 5 starting admin menus - gmliston/gmlistoff = includes/excludes active character from /gmlist results - silence = toggles private messages acceptance mode - diet = toggles weight penalty mode -
 * tradeoff = toggles trade acceptance mode - reload = reloads specified component from multisell|skill|npc|htm|item - set/set_menu/set_mod = alters specified server setting - saveolymp = saves olympiad state manually - manualhero = cycles olympiad and calculate new heroes.
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2007/07/28 10:06:06 $
 */
public class AdminAdmin implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminAdmin.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_admin",
		"admin_admin1",
		"admin_admin2",
		"admin_admin3",
		"admin_admin4",
		"admin_admin5",
		"admin_admin6",
		"admin_admin7",
		"admin_gmliston",
		"admin_gmlistoff",
		"admin_silence",
		"admin_diet",
		"admin_tradeoff",
		"admin_set",
		"admin_set_mod",
		"admin_saveolymp",
		"admin_sethero",
		"admin_givehero",
		"admin_setconfig",
		"admin_config_server",
		"admin_gmon",
		"admin_worldchat",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_admin"))
		{
			showMainPage(activeChar, command);
		}
		else if (command.equals("admin_config_server"))
		{
			showConfigPage(activeChar);
		}
		else if (command.startsWith("admin_gmliston"))
		{
			AdminData.getInstance().showGm(activeChar);
			BuilderUtil.sendSysMessage(activeChar, "Registered into gm list.");
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_gmlistoff"))
		{
			AdminData.getInstance().hideGm(activeChar);
			BuilderUtil.sendSysMessage(activeChar, "Removed from gm list.");
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_silence"))
		{
			if (activeChar.isSilenceMode()) // already in message refusal mode
			{
				activeChar.setSilenceMode(false);
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
			}
			else
			{
				activeChar.setSilenceMode(true);
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_saveolymp"))
		{
			OlympiadEngine.getInstance().saveOlympiadStatus();
			BuilderUtil.sendSysMessage(activeChar, "olympiad system saved.");
		}
		else if (command.startsWith("admin_sethero"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			
			final Player target = isPlayer(activeChar.getTarget()) ? activeChar.getTarget().getActingPlayer() : activeChar;
			target.setHero(!target.isHero());
			target.broadcastUserInfo();
		}
		else if (command.startsWith("admin_givehero"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			
			final Player target = isPlayer(activeChar.getTarget()) ? activeChar.getTarget().getActingPlayer() : activeChar;
			if (Hero.getInstance().isHero(target.getObjectId()))
			{
				BuilderUtil.sendSysMessage(activeChar, "This player has already claimed the hero status.");
				return false;
			}
			
			if (!Hero.getInstance().isUnclaimedHero(target.getObjectId()))
			{
				BuilderUtil.sendSysMessage(activeChar, "This player cannot claim the hero status.");
				return false;
			}
			Hero.getInstance().claimHero(target);
		}
		else if (command.startsWith("admin_diet"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if (st.nextToken().equalsIgnoreCase("on"))
				{
					activeChar.setDietMode(true);
					BuilderUtil.sendSysMessage(activeChar, "Diet mode on.");
				}
				else if (st.nextToken().equalsIgnoreCase("off"))
				{
					activeChar.setDietMode(false);
					BuilderUtil.sendSysMessage(activeChar, "Diet mode off.");
				}
			}
			catch (Exception ex)
			{
				if (activeChar.getDietMode())
				{
					activeChar.setDietMode(false);
					BuilderUtil.sendSysMessage(activeChar, "Diet mode off.");
				}
				else
				{
					activeChar.setDietMode(true);
					BuilderUtil.sendSysMessage(activeChar, "Diet mode on.");
				}
			}
			finally
			{
				activeChar.refreshOverloaded(true);
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_tradeoff"))
		{
			try
			{
				final String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					activeChar.setTradeRefusing(true);
					BuilderUtil.sendSysMessage(activeChar, "Trade refusal enabled.");
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					activeChar.setTradeRefusing(false);
					BuilderUtil.sendSysMessage(activeChar, "Trade refusal disabled.");
				}
			}
			catch (Exception ex)
			{
				if (activeChar.isTradeRefusing())
				{
					activeChar.setTradeRefusing(false);
					BuilderUtil.sendSysMessage(activeChar, "Trade refusal disabled.");
				}
				else
				{
					activeChar.setTradeRefusing(true);
					BuilderUtil.sendSysMessage(activeChar, "Trade refusal enabled.");
				}
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.startsWith("admin_setconfig"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				final String pName = st.nextToken();
				final String pValue = st.nextToken();
				if (Float.valueOf(pValue) == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Invalid parameter!");
					return false;
				}
				switch (pName)
				{
					case "RateXp":
					{
						getSettings(RateSettings.class).setXp(Float.parseFloat(pValue));
						break;
					}
					case "RateSp":
					{
						Config.RATE_SP = Float.valueOf(pValue);
						break;
					}
					case "RateDropSpoil":
					{
						Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER = Float.valueOf(pValue);
						break;
					}
					case "EnchantChanceElementStone":
					{
						Config.ENCHANT_CHANCE_ELEMENT_STONE = Float.valueOf(pValue);
						break;
					}
					case "EnchantChanceElementCrystal":
					{
						Config.ENCHANT_CHANCE_ELEMENT_CRYSTAL = Float.valueOf(pValue);
						break;
					}
					case "EnchantChanceElementJewel":
					{
						Config.ENCHANT_CHANCE_ELEMENT_JEWEL = Float.valueOf(pValue);
						break;
					}
					case "EnchantChanceElementEnergy":
					{
						Config.ENCHANT_CHANCE_ELEMENT_ENERGY = Float.valueOf(pValue);
						break;
					}
				}
				BuilderUtil.sendSysMessage(activeChar, "Config parameter " + pName + " set to " + pValue);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //setconfig <parameter> <value>");
			}
			finally
			{
				showConfigPage(activeChar);
			}
		}
		else if (command.startsWith("admin_worldchat"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // admin_worldchat
			switch (st.hasMoreTokens() ? st.nextToken() : "")
			{
				case "shout":
				{
					final StringBuilder sb = new StringBuilder();
					while (st.hasMoreTokens())
					{
						sb.append(st.nextToken());
						sb.append(" ");
					}
					
					final CreatureSay cs = new CreatureSay(activeChar, ChatType.WORLD, sb.toString());
					World.getInstance().getPlayers().stream().filter(activeChar::isNotBlocked).forEach(cs::sendTo);
					break;
				}
				case "see":
				{
					final WorldObject target = activeChar.getTarget();
					if (!isPlayer(target))
					{
						activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
						break;
					}
					final Player targetPlayer = target.getActingPlayer();
					var worldChatMinLevel = getSettings(ChatSettings.class).worldChatMinLevel();
					if (targetPlayer.getLevel() < worldChatMinLevel)
					{
						BuilderUtil.sendSysMessage(activeChar, "Your target's level is below the minimum: " + worldChatMinLevel);
						break;
					}
					BuilderUtil.sendSysMessage(activeChar, targetPlayer.getName() + ": has used world chat " + targetPlayer.getWorldChatUsed() + " times out of maximum " + targetPlayer.getWorldChatPoints() + " times.");
					break;
				}
				case "set":
				{
					final WorldObject target = activeChar.getTarget();
					if (!isPlayer(target)) {
						activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
						break;
					}
					
					final Player targetPlayer = target.getActingPlayer();
					var worldChatMinLevel = getSettings(ChatSettings.class).worldChatMinLevel();
					if (targetPlayer.getLevel() < worldChatMinLevel)
					{
						BuilderUtil.sendSysMessage(activeChar, "Your target's level is below the minimum: " + worldChatMinLevel);
						break;
					}
					
					if (!st.hasMoreTokens())
					{
						BuilderUtil.sendSysMessage(activeChar, "Incorrect syntax, use: //worldchat set <times used>");
						break;
					}
					
					final String valueToken = st.nextToken();
					if (!isDigit(valueToken))
					{
						BuilderUtil.sendSysMessage(activeChar, "Incorrect syntax, use: //worldchat set <times used>");
						break;
					}
					
					BuilderUtil.sendSysMessage(activeChar, targetPlayer.getName() + ": times used changed from " + targetPlayer.getWorldChatPoints() + " to " + valueToken);
					targetPlayer.setWorldChatUsed(Integer.parseInt(valueToken));
					if (getSettings(ChatSettings.class).worldChatEnabled()) {
						targetPlayer.sendPacket(new ExWorldChatCnt(targetPlayer));
					}
					break;
				}
				default:
				{
					BuilderUtil.sendSysMessage(activeChar, "Possible commands:");
					BuilderUtil.sendSysMessage(activeChar, " - Send message: //worldchat shout <text>");
					BuilderUtil.sendSysMessage(activeChar, " - See your target's points: //worldchat see");
					BuilderUtil.sendSysMessage(activeChar, " - Change your target's points: //worldchat set <points>");
					break;
				}
			}
		}
		else if (command.startsWith("admin_gmon"))
		{
			// TODO why is this empty?
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showMainPage(Player activeChar, String command)
	{
		int mode = 0;
		String filename = null;
		try
		{
			mode = Integer.parseInt(command.substring(11));
		}
		catch (Exception e)
		{
		}
		switch (mode)
		{
			case 1:
			{
				filename = "main";
				break;
			}
			case 2:
			{
				filename = "game";
				break;
			}
			case 3:
			{
				filename = "effects";
				break;
			}
			case 4:
			{
				filename = "server";
				break;
			}
			case 5:
			{
				filename = "mods";
				break;
			}
			case 6:
			{
				filename = "char";
				break;
			}
			case 7:
			{
				filename = "gm";
				break;
			}
			default:
			{
				filename = "main";
				break;
			}
		}
		AdminHtml.showAdminHtml(activeChar, filename + "_menu.htm");
	}
	
	private void showConfigPage(Player activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		final StringBuilder replyMSG = new StringBuilder("<html><title>L2J :: Config</title><body>");
		replyMSG.append("<center><table width=270><tr><td width=60><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=150>Config Server Panel</td><td width=60><button value=\"Back\" action=\"bypass -h admin_admin4\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></center><br>");
		replyMSG.append("<center><table width=260><tr><td width=140></td><td width=40></td><td width=40></td></tr>");
		replyMSG.append("<tr><td><font color=\"00AA00\">Drop:</font></td><td></td><td></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate EXP</font> = ").append(getSettings(RateSettings.class).xp()).append("</td><td><edit var=\"param1\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig RateXp $param1\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate SP</font> = " + Config.RATE_SP + "</td><td><edit var=\"param2\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig RateSp $param2\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Rate Drop Spoil</font> = " + Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER + "</td><td><edit var=\"param4\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig RateDropSpoil $param4\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td width=140></td><td width=40></td><td width=40></td></tr>");
		replyMSG.append("<tr><td><font color=\"00AA00\">Enchant:</font></td><td></td><td></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Stone</font> = " + Config.ENCHANT_CHANCE_ELEMENT_STONE + "</td><td><edit var=\"param8\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementStone $param8\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Crystal</font> = " + Config.ENCHANT_CHANCE_ELEMENT_CRYSTAL + "</td><td><edit var=\"param9\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementCrystal $param9\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Jewel</font> = " + Config.ENCHANT_CHANCE_ELEMENT_JEWEL + "</td><td><edit var=\"param10\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementJewel $param10\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><font color=\"LEVEL\">Enchant Element Energy</font> = " + Config.ENCHANT_CHANCE_ELEMENT_ENERGY + "</td><td><edit var=\"param11\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementEnergy $param11\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		
		replyMSG.append("</table></body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}
