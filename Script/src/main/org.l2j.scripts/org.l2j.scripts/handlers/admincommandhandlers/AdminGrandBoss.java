/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.BossStatus;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.NoRestartZone;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import static java.util.Objects.nonNull;

/**
 * @author St3eT
 */
public class AdminGrandBoss implements IAdminCommandHandler
{
	private static final int ANTHARAS = 29068; // Antharas
	private static final int ANTHARAS_ZONE = 70050; // Antharas Nest
	private static final int VALAKAS = 29028; // Valakas
	private static final int BAIUM = 29020; // Baium
	private static final int BAIUM_ZONE = 70051; // Baium Nest
	private static final int QUEENANT = 29001; // Queen Ant
	private static final int ORFEN = 29014; // Orfen
	private static final int CORE = 29006; // Core
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_grandboss",
		"admin_grandboss_skip",
		"admin_grandboss_respawn",
		"admin_grandboss_minions",
		"admin_grandboss_abort",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_grandboss":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					manageHtml(activeChar, grandBossId);
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
					html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/grandboss.htm"));
					activeChar.sendPacket(html);
				}
				break;
			}
			
			case "admin_grandboss_skip":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					if (grandBossId == ANTHARAS)
					{
						antharasAi().notifyEvent("SKIP_WAITING", null, activeChar);
						manageHtml(activeChar, grandBossId);
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_skip Id");
				}
				break;
			}
			case "admin_grandboss_respawn":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("RESPAWN_ANTHARAS", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("RESPAWN_BAIUM", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						default:
						{
							BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
						}
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_respawn Id");
				}
				break;
			}
			case "admin_grandboss_minions":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("DESPAWN_MINIONS", null, activeChar);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("DESPAWN_MINIONS", null, activeChar);
							break;
						}
						default:
						{
							BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
						}
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_minions Id");
				}
				break;
			}
			case "admin_grandboss_abort":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("ABORT_FIGHT", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("ABORT_FIGHT", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						default:
						{
							BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
						}
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_abort Id");
				}
			}
				break;
		}
		return true;
	}
	
	private void manageHtml(Player activeChar, int grandBossId)
	{
		if (Arrays.asList(ANTHARAS, VALAKAS, BAIUM, QUEENANT, ORFEN, CORE).contains(grandBossId))
		{
			final var bossStatus = GrandBossManager.getInstance().getBossStatus(grandBossId);
			NoRestartZone bossZone = null;
			String textColor;
			String text;
			String htmlPatch = null;

			switch (grandBossId)
			{
				case ANTHARAS:
				{
					bossZone = ZoneEngine.getInstance().getZoneById(ANTHARAS_ZONE, NoRestartZone.class);
					htmlPatch = "data/html/admin/grandboss_antharas.htm";
					break;
				}
				case VALAKAS:
				{
					htmlPatch = "data/html/admin/grandboss_valakas.htm";
					break;
				}
				case BAIUM:
				{
					bossZone = ZoneEngine.getInstance().getZoneById(BAIUM_ZONE, NoRestartZone.class);
					htmlPatch = "data/html/admin/grandboss_baium.htm";
					break;
				}
				case QUEENANT:
				{
					htmlPatch = "data/html/admin/grandboss_queenant.htm";
					break;
				}
				case ORFEN:
				{
					htmlPatch = "data/html/admin/grandboss_orfen.htm";
					break;
				}
				case CORE:
				{
					htmlPatch = "data/html/admin/grandboss_core.htm";
					break;
				}
			}
			
			if (Arrays.asList(ANTHARAS, VALAKAS, BAIUM).contains(grandBossId))
			{
				switch (bossStatus)
				{
					case ALIVE:
					{
						textColor = "00FF00"; // Green
						text = "Alive";
						break;
					}
					case FIGHTING:
					{
						textColor = "FF9900"; // Orange
						text = "In Fight";
						break;
					}
					case DEAD:
					{
						textColor = "FF0000"; // Red
						text = "Dead";
						break;
					}
					default:
					{
						textColor = "FFFFFF"; // White
						text = "Unk " + bossStatus;
					}
				}
			}
			else
			{
				switch (bossStatus)
				{
					case ALIVE:
					{
						textColor = "00FF00"; // Green
						text = "Alive";
						break;
					}
					case DEAD:
					{
						textColor = "FF0000"; // Red
						text = "Dead";
						break;
					}
					default:
					{
						textColor = "FFFFFF"; // White
						text = "Unk " + bossStatus;
					}
				}
			}
			
			final var info = GrandBossManager.getInstance().getBossData(grandBossId);
			final String bossRespawn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getRespawnTime());
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setHtml(HtmCache.getInstance().getHtm(activeChar, htmlPatch));
			html.replace("%bossStatus%", text);
			html.replace("%bossColor%", textColor);
			html.replace("%respawnTime%", bossStatus == BossStatus.DEAD ? bossRespawn : "Already respawned!");
			html.replace("%playersInside%", nonNull(bossZone) ? String.valueOf(bossZone.getPlayersInsideCount()) : "Zone not found!");
			activeChar.sendPacket(html);
		}
		else
		{
			BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
		}
	}
	
	private Quest antharasAi()
	{
		return QuestManager.getInstance().getQuest("Antharas");
	}
	
	private Quest baiumAi()
	{
		return QuestManager.getInstance().getQuest("Baium");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
