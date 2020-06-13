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

import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isPlayer;


/**
 * @author UnAfraid
 */
public class AdminPunishment implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminPunishment.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_punishment",
		"admin_punishment_add",
		"admin_punishment_remove",
		"admin_ban_acc",
		"admin_unban_acc",
		"admin_ban_chat",
		"admin_unban_chat",
		"admin_ban_char",
		"admin_unban_char",
		"admin_jail",
		"admin_unjail"
	};
	
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		if (!st.hasMoreTokens())
		{
			return false;
		}
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_punishment":
			{
				if (!st.hasMoreTokens())
				{
					String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment.htm");
					if (content != null)
					{
						content = content.replaceAll("%punishments%", CommonUtil.implode(PunishmentType.values(), ";"));
						content = content.replaceAll("%affects%", CommonUtil.implode(PunishmentAffect.values(), ";"));
						activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
					}
					else
					{
						LOGGER.warn(": data/html/admin/punishment.htm is missing");
					}
				}
				else
				{
					final String subcmd = st.nextToken();
					switch (subcmd)
					{
						case "info":
						{
							String key = st.hasMoreTokens() ? st.nextToken() : null;
							final String af = st.hasMoreTokens() ? st.nextToken() : null;
							final String name = key;
							
							if ((key == null) || (af == null))
							{
								BuilderUtil.sendSysMessage(activeChar, "Not enough data specified!");
								break;
							}
							final PunishmentAffect affect = PunishmentAffect.getByName(af);
							if (affect == null)
							{
								BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect type!");
								break;
							}
							
							// Swap the name of the character with it's id.
							if (affect == PunishmentAffect.CHARACTER)
							{
								key = findCharId(key);
							}
							
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment-info.htm");
							if (content != null)
							{
								final StringBuilder sb = new StringBuilder();
								for (PunishmentType type : PunishmentType.values())
								{
									if (PunishmentManager.getInstance().hasPunishment(key, affect, type))
									{
										final long expiration = PunishmentManager.getInstance().getPunishmentExpiration(key, affect, type);
										String expire = "never";
										
										if (expiration > 0)
										{
											// Synchronize date formatter since its not thread safe.
											synchronized (DATE_FORMATTER)
											{
												expire = DATE_FORMATTER.format(new Date(expiration));
											}
										}
										sb.append("<tr><td><font color=\"LEVEL\">" + type + "</font></td><td>" + expire + "</td><td><a action=\"bypass -h admin_punishment_remove " + name + " " + affect + " " + type + "\">Remove</a></td></tr>");
									}
								}
								
								content = content.replaceAll("%player_name%", name);
								content = content.replaceAll("%punishments%", sb.toString());
								content = content.replaceAll("%affects%", CommonUtil.implode(PunishmentAffect.values(), ";"));
								content = content.replaceAll("%affect_type%", affect.name());
								activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
							}
							else
							{
								LOGGER.warn(": data/html/admin/punishment-info.htm is missing");
							}
							break;
						}
						case "player":
						{
							Player target = null;
							if (st.hasMoreTokens())
							{
								final String playerName = st.nextToken();
								if (playerName.isEmpty() && !isPlayer(activeChar.getTarget()))
								{
									return useAdminCommand("admin_punishment", activeChar);
								}
								target = World.getInstance().findPlayer(playerName);
							}
							if ((target == null) && (!isPlayer(activeChar.getTarget())))
							{
								BuilderUtil.sendSysMessage(activeChar, "You must target player!");
								break;
							}
							if (target == null)
							{
								target = activeChar.getTarget().getActingPlayer();
							}
							String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment-player.htm");
							if (content != null)
							{
								content = content.replaceAll("%player_name%", target.getName());
								content = content.replaceAll("%punishments%", CommonUtil.implode(PunishmentType.values(), ";"));
								content = content.replaceAll("%acc%", target.getAccountName());
								content = content.replaceAll("%char%", target.getName());
								content = content.replaceAll("%ip%", target.getIPAddress());
								activeChar.sendPacket(new NpcHtmlMessage(0, 1, content));
							}
							else
							{
								LOGGER.warn(": data/html/admin/punishment-player.htm is missing");
							}
							break;
						}
					}
				}
				break;
			}
			case "admin_punishment_add":
			{
				// Add new punishment
				String key = st.hasMoreTokens() ? st.nextToken() : null;
				final String af = st.hasMoreTokens() ? st.nextToken() : null;
				final String t = st.hasMoreTokens() ? st.nextToken() : null;
				final String exp = st.hasMoreTokens() ? st.nextToken() : null;
				String reason = st.hasMoreTokens() ? st.nextToken() : null;
				
				// Let's grab the other part of the reason if there is..
				if (reason != null)
				{
					while (st.hasMoreTokens())
					{
						reason += " " + st.nextToken();
					}
					if (!reason.isEmpty())
					{
						reason = reason.replaceAll("\\$", "\\\\\\$");
						reason = reason.replaceAll("\r\n", "<br1>");
						reason = reason.replace("<", "&lt;");
						reason = reason.replace(">", "&gt;");
					}
				}
				
				final String name = key;
				
				if ((key == null) || (af == null) || (t == null) || (exp == null) || (reason == null))
				{
					BuilderUtil.sendSysMessage(activeChar, "Please fill all the fields!");
					break;
				}
				if (!Util.isInteger(exp) && !exp.equals("-1"))
				{
					BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for expiration time!");
					break;
				}
				
				long expirationTime = Integer.parseInt(exp);
				if (expirationTime > 0)
				{
					expirationTime = System.currentTimeMillis() + (expirationTime * 60 * 1000);
				}
				
				final PunishmentAffect affect = PunishmentAffect.getByName(af);
				final PunishmentType type = PunishmentType.getByName(t);
				if ((affect == null) || (type == null))
				{
					BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect/punishment type!");
					break;
				}
				
				// Swap the name of the character with it's id.
				if (affect == PunishmentAffect.CHARACTER)
				{
					key = findCharId(key);
				}
				else if (affect == PunishmentAffect.IP)
				{
					try
					{
						final InetAddress addr = InetAddress.getByName(key);
						if (addr.isLoopbackAddress())
						{
							throw new UnknownHostException("You cannot ban any local address!");
						}
						else if (Config.GAME_SERVER_HOSTS.contains(addr.getHostAddress()))
						{
							throw new UnknownHostException("You cannot ban your gameserver's address!");
						}
					}
					catch (UnknownHostException e)
					{
						BuilderUtil.sendSysMessage(activeChar, "You've entered an incorrect IP address!");
						activeChar.sendMessage(e.getMessage());
						break;
					}
				}
				
				// Check if we already put the same punishment on that guy ^^
				if (PunishmentManager.getInstance().hasPunishment(key, affect, type))
				{
					BuilderUtil.sendSysMessage(activeChar, "Target is already affected by that punishment.");
					break;
				}
				
				// Punish him!
				PunishmentManager.getInstance().startPunishment(new PunishmentTask(key, affect, type, expirationTime, reason, activeChar.getName()));
				BuilderUtil.sendSysMessage(activeChar, "Punishment " + type.name() + " have been applied to: " + affect + " " + name + "!");
				GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", cmd, affect.name(), name);
				return useAdminCommand("admin_punishment info " + name + " " + affect.name(), activeChar);
			}
			case "admin_punishment_remove":
			{
				// Remove punishment.
				String key = st.hasMoreTokens() ? st.nextToken() : null;
				final String af = st.hasMoreTokens() ? st.nextToken() : null;
				final String t = st.hasMoreTokens() ? st.nextToken() : null;
				final String name = key;
				
				if ((key == null) || (af == null) || (t == null))
				{
					BuilderUtil.sendSysMessage(activeChar, "Not enough data specified!");
					break;
				}
				
				final PunishmentAffect affect = PunishmentAffect.getByName(af);
				final PunishmentType type = PunishmentType.getByName(t);
				if ((affect == null) || (type == null))
				{
					BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect/punishment type!");
					break;
				}
				
				// Swap the name of the character with it's id.
				if (affect == PunishmentAffect.CHARACTER)
				{
					key = findCharId(key);
				}
				
				if (!PunishmentManager.getInstance().hasPunishment(key, affect, type))
				{
					BuilderUtil.sendSysMessage(activeChar, "Target is not affected by that punishment!");
					break;
				}
				
				PunishmentManager.getInstance().stopPunishment(key, affect, type);
				BuilderUtil.sendSysMessage(activeChar, "Punishment " + type.name() + " have been stopped to: " + affect + " " + name + "!");
				GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", cmd, affect.name(), name);
				return useAdminCommand("admin_punishment info " + name + " " + affect.name(), activeChar);
			}
			case "admin_ban_char":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.BAN, 0, "Banned by admin"), activeChar);
				}
			}
			case "admin_unban_char":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.BAN), activeChar);
				}
			}
			case "admin_ban_acc":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.ACCOUNT, PunishmentType.BAN, 0, "Banned by admin"), activeChar);
				}
			}
			case "admin_unban_acc":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.ACCOUNT, PunishmentType.BAN), activeChar);
				}
			}
			case "admin_ban_chat":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0, "Chat banned by admin"), activeChar);
				}
			}
			case "admin_unban_chat":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN), activeChar);
				}
			}
			case "admin_jail":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, 0, "Jailed by admin"), activeChar);
				}
			}
			case "admin_unjail":
			{
				if (st.hasMoreTokens())
				{
					return useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.JAIL), activeChar);
				}
			}
		}
		return true;
	}
	
	private static String findCharId(String key)
	{
		final int charId = PlayerNameTable.getInstance().getIdByName(key);
		if (charId > 0) // Yeah its a char name!
		{
			return Integer.toString(charId);
		}
		return key;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}