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
package ai.others.OlyManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.data.xml.impl.MultisellData;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.handler.BypassHandler;
import com.l2jmobius.gameserver.handler.IBypassHandler;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.entity.Hero;
import com.l2jmobius.gameserver.model.olympiad.CompetitionType;
import com.l2jmobius.gameserver.model.olympiad.Olympiad;
import com.l2jmobius.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jmobius.gameserver.model.olympiad.OlympiadGameTask;
import com.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExOlympiadMatchList;

import ai.AbstractNpcAI;

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
	private static final Logger LOGGER = Logger.getLogger(OlyManager.class.getName());
	
	private OlyManager()
	{
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
		BypassHandler.getInstance().registerHandler(this);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
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
					htmltext = htmltext.replace("%olympiad_round%", String.valueOf(Olympiad.getInstance().getPeriod()));
					htmltext = htmltext.replace("%olympiad_week%", String.valueOf(Olympiad.getInstance().getCurrentCycle()));
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
				else if (Olympiad.getInstance().getNoblePoints(player) <= 0)
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
				final int points = Olympiad.getInstance().getOlympiadTradePoint(player, false);
				if (points == 0)
				{
					htmltext = "OlyManager-calculateNoEnough.html";
				}
				else if (points < 20)
				{
					if (Hero.getInstance().isUnclaimedHero(player.getObjectId()) || Hero.getInstance().isHero(player.getObjectId()))
					{
						htmltext = "OlyManager-calculateEnough.html";
					}
					else
					{
						htmltext = "OlyManager-calculateNoEnough.html";
					}
				}
				else
				{
					htmltext = "OlyManager-calculateEnough.html";
				}
				break;
			}
			case "calculatePointsDone":
			{
				if (player.isInventoryUnder80(false))
				{
					final int tradePoints = Olympiad.getInstance().getOlympiadTradePoint(player, true);
					if (tradePoints > 0)
					{
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
			case "rank_2": // Gladiator
			case "rank_3": // Warlord
			case "rank_5": // Paladin
			case "rank_6": // Dark Avenger
			case "rank_8": // Treasure Hunter
			case "rank_9": // Hawkeye
			case "rank_12": // Sorcerer
			case "rank_13": // Necromancer
			case "rank_14": // Warlock
			case "rank_16": // Bishop
			case "rank_17": // Prophet
			case "rank_20": // Temple Knight
			case "rank_21": // Sword Singer
			case "rank_23": // Plains Walker
			case "rank_24": // Silver Ranger
			case "rank_27": // Spellsinger
			case "rank_28": // Elemental Summoner
			case "rank_30": // Elven Elder
			case "rank_33": // Shillien Knight
			case "rank_34": // Bladedancer
			case "rank_36": // Abyss Walker
			case "rank_37": // Phantom Ranger
			case "rank_40": // Spellhowler
			case "rank_41": // Phantom Summoner
			case "rank_43": // Shillien Elder
			case "rank_46": // Destroyer
			case "rank_48": // Tyrant
			case "rank_51": // Overlord
			case "rank_52": // Warcryer
			case "rank_55": // Bounty Hunter
			case "rank_88": // Duelist
			case "rank_89": // Dreadnought
			case "rank_90": // Phoenix Knight
			case "rank_91": // Hell Knight
			case "rank_92": // Sagittarius
			case "rank_93": // Adventurer
			case "rank_94": // Archmage
			case "rank_95": // Soultaker
			case "rank_96": // Arcana Lord
			case "rank_97": // Cardinal
			case "rank_98": // Hierophant
			case "rank_99": // Eva's Templar
			case "rank_100": // Sword Muse
			case "rank_101": // Wind Rider
			case "rank_102": // Moonlight Sentinel
			case "rank_103": // Mystic Muse
			case "rank_104": // Elemental Master
			case "rank_105": // Eva's Saint
			case "rank_106": // Shillien Templar
			case "rank_107": // Spectral Dancer
			case "rank_108": // Ghost Hunter
			case "rank_109": // Ghost Sentinel
			case "rank_110": // Storm Screamer
			case "rank_111": // Spectral Master
			case "rank_112": // Shillien Saint
			case "rank_113": // Titan
			case "rank_114": // Grand Khavatari
			case "rank_115": // Dominator
			case "rank_116": // Doom Cryer
			case "rank_117": // Fortune Seeker
			case "rank_118": // Maestro
			{
				final int classId = Integer.parseInt(event.replace("rank_", ""));
				final List<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
				htmltext = getHtm(player, "OlyManager-rankDetail.html");
				
				int index = 1;
				for (String name : names)
				{
					htmltext = htmltext.replace("%Rank" + index + "%", String.valueOf(index));
					htmltext = htmltext.replace("%Name" + index + "%", name);
					index++;
					if (index > 15)
					{
						break;
					}
				}
				for (; index <= 15; index++)
				{
					htmltext = htmltext.replace("%Rank" + index + "%", "");
					htmltext = htmltext.replace("%Name" + index + "%", "");
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		if (!player.isCursedWeaponEquipped())
		{
			htmltext = (!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (player.getLevel() < 55) ? "OlyManager-noNoble.html" : "OlyManager-noble.html";
		}
		else
		{
			htmltext = "OlyManager-noCursed.html";
		}
		return htmltext;
	}
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin)
	{
		try
		{
			final L2Npc olymanager = activeChar.getLastFolkNPC();
			
			if (command.startsWith(BYPASSES[0])) // list
			{
				if (!Olympiad.getInstance().inCompPeriod())
				{
					activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				activeChar.sendPacket(new ExOlympiadMatchList());
			}
			else if ((olymanager == null) || (olymanager.getId() != MANAGER) || (!activeChar.inObserverMode() && !activeChar.isInsideRadius2D(olymanager, 300)))
			{
				return false;
			}
			else if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
			{
				activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
				return false;
			}
			else if (!Olympiad.getInstance().inCompPeriod())
			{
				activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
				return false;
			}
			else if (activeChar.isOnEvent())
			{
				activeChar.sendMessage("You can not observe games while registered on an event");
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
						LOGGER.warning(getClass().getSimpleName() + ": Zone: " + nextArena.getStadium().getZone() + " doesn't have specatator spawns defined!");
						return false;
					}
					final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
					activeChar.enterOlympiadObserverMode(loc, arenaId);
				}
			}
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return BYPASSES;
	}
	
	public static void main(String[] args)
	{
		new OlyManager();
	}
}