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
package ai.others.OlyManager;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.CompetitionType;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * Olympiad Manager AI.
 * @author St3eT
 */
public final class OlyManager extends AbstractNpcAI implements IBypassHandler
{
	// NPC
	private static final int MANAGER = 31688;
	// Misc
	private static final int EQUIPMENT_MULTISELL = 3168801;
	
	private static final String[] BYPASSES =
	{
		"watchmatch",
		"arenachange"
	};
	private static final Logger LOGGER = LoggerFactory.getLogger(OlyManager.class);
	private static final String UNCLAIMED_OLYMPIAD_POINTS_VAR = "UNCLAIMED_OLYMPIAD_POINTS";

	private OlyManager()
	{
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
		BypassHandler.getInstance().registerHandler(this);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "OlyManager-info.html":
			case "OlyManager-infoHistory.html":
			case "OlyManager-infoRules.html":
			case "OlyManager-infoPoints.html":
			case "OlyManager-infoPointsCalc.html":
			case "OlyManager-rank.html":
			case "OlyManager-rewards.html":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "joinMatch":
			{
				if (OlympiadManager.getInstance().isRegistered(player))
				{
					htmltext = "OlyManager-registred.html";
				}
				else
				{
					htmltext = getHtm(player, "OlyManager-joinMatch.html");
					htmltext = htmltext.replace("%olympiad_round%", String.valueOf(OlympiadEngine.getInstance().getPeriod()));
					htmltext = htmltext.replace("%olympiad_week%", String.valueOf(OlympiadEngine.getInstance().getCurrentSeason()));
					htmltext = htmltext.replace("%olympiad_participant%", String.valueOf(OlympiadManager.getInstance().getCountOpponents()));
				}
				break;
			}
			case "register1v1":
			{
				if (player.isSubClassActive())
				{
					htmltext = "OlyManager-subclass.html";
				}
				else if ((!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (player.getLevel() < 55)) // avoid exploits
				{
					htmltext = "OlyManager-noNoble.html";
				}
				else if (OlympiadEngine.getInstance().getOlympiadPoints(player) <= 0)
				{
					htmltext = "OlyManager-noPoints.html";
				}
				else if (!player.isInventoryUnder80(false))
				{
					player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				}
				else
				{
					OlympiadManager.getInstance().registerNoble(player, CompetitionType.NON_CLASSED);
				}
				break;
			}
			case "unregister":
			{
				OlympiadManager.getInstance().unRegisterNoble(player);
				break;
			}
			case "calculatePoints":
			{
				if (player.getVariables().getInt(UNCLAIMED_OLYMPIAD_POINTS_VAR, 0) > 0)
				{
					htmltext = "OlyManager-calculateEnough.html";
				}
				else
				{
					htmltext = "OlyManager-calculateNoEnough.html";
				}
				break;
			}
			case "calculatePointsDone":
			{
				if (player.isInventoryUnder80(false))
				{
					final int tradePoints = player.getVariables().getInt(UNCLAIMED_OLYMPIAD_POINTS_VAR, 0);
					if (tradePoints > 0)
					{
						player.getVariables().remove(UNCLAIMED_OLYMPIAD_POINTS_VAR);
						giveItems(player, Config.ALT_OLY_COMP_RITEM, tradePoints * Config.ALT_OLY_MARK_PER_POINT);
					}
				}
				else
				{
					player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				}
				break;
			}
			case "showEquipmentReward":
			{
				MultisellData.getInstance().separateAndSend(EQUIPMENT_MULTISELL, player, npc, false);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player) {
		return (!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (player.getLevel() < 55) ? "OlyManager-noNoble.html" : "OlyManager-noble.html";
	}
	
	@Override
	public boolean useBypass(String command, Player player, Creature bypassOrigin)
	{
		try
		{
			final Npc olymanager = player.getLastFolkNPC();
			
			if (command.startsWith(BYPASSES[0])) // list
			{
				if (!OlympiadEngine.getInstance().isMatchInProgress())
				{
					player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				player.sendPacket(new ExOlympiadMatchList());
			}
			else if ((olymanager == null) || (olymanager.getId() != MANAGER) || (!player.inObserverMode() && !isInsideRadius2D(player, olymanager, 300)))
			{
				return false;
			}
			else if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
				return false;
			}
			else if (!OlympiadEngine.getInstance().isMatchInProgress())
			{
				player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
				return false;
			}
			else if (player.isOnEvent())
			{
				player.sendMessage("You can not observe games while registered on an event");
				return false;
			}
			else
			{
				final int arenaId = Integer.parseInt(command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null)
				{
					final List<Location> spectatorSpawns = nextArena.getStadium().getZone().getSpectatorSpawns();
					if (spectatorSpawns.isEmpty())
					{
						LOGGER.warn(": Zone: " + nextArena.getStadium().getZone() + " doesn't have specatator spawns defined!");
						return false;
					}
					final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
					player.enterOlympiadObserverMode(loc, arenaId);
				}
			}
			return true;
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return BYPASSES;
	}
	
	public static AbstractNpcAI provider()
	{
		return new OlyManager();
	}
}