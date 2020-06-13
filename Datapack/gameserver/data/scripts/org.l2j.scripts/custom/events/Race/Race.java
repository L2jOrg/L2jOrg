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
package custom.events.Race;

import events.ScriptEvent;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.Broadcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * @author Gnacik
 */
public final class Race extends Event implements ScriptEvent
{
	// Event NPCs list
	private List<Npc> _npclist;
	// Npc
	private Npc _npc;
	// Player list
	private Collection<Player> _players;
	// Event Task
	ScheduledFuture<?> _eventTask = null;
	// Event state
	private static boolean _isactive = false;
	// Race state
	private static boolean _isRaceStarted = false;
	// 5 min for register
	private static final int _time_register = 5;
	// 5 min for race
	private static final int _time_race = 10;
	// NPCs
	private static final int _start_npc = 900103;
	private static final int _stop_npc = 900104;
	// Skills (Frog by default)
	private static int _skill = 6201;
	// We must keep second NPC spawn for radar
	private static int[] _randspawn = null;
	// Locations
	private static final String[] _locations =
	{
		"Heretic catacomb enterance",
		"Dion castle bridge",
		"Floran village enterance",
		"Floran fort gate"
	};
	
	// @formatter:off
	private static final int[][] _coords =
	{
		// x, y, z, heading
		{ 39177, 144345, -3650, 0 },
		{ 22294, 155892, -2950, 0 },
		{ 16537, 169937, -3500, 0 },
		{  7644, 150898, -2890, 0 }
	};
	private static final int[][] _rewards =
	{
		{ 6622, 2 }, // Giant's Codex
		{ 9625, 2 }, // Giant's Codex -
		{ 9626, 2 }, // Giant's Codex -
		{ 9627, 2 }, // Giant's Codex -
		{ 9546, 5 }, // Attr stones
		{ 9547, 5 },
		{ 9548, 5 },
		{ 9549, 5 },
		{ 9550, 5 },
		{ 9551, 5 },
		{ 9574, 3 }, // Mid-Grade Life Stone: level 80
		{ 9575, 2 }, // High-Grade Life Stone: level 80
		{ 9576, 1 }, // Top-Grade Life Stone: level 80
		{ 20034,1 }  // Revita pop
	};
	// @formatter:on
	
	private Race()
	{
		addStartNpc(_start_npc);
		addFirstTalkId(_start_npc);
		addTalkId(_start_npc);
		addStartNpc(_stop_npc);
		addFirstTalkId(_stop_npc);
		addTalkId(_stop_npc);
	}
	
	@Override
	public boolean eventStart(Player eventMaker)
	{
		// Don't start event if its active
		if (_isactive)
		{
			return false;
		}
		// Check Custom Table - we use custom NPCs
		if (!Config.CUSTOM_NPC_DATA)
		{
			LOGGER.info(getName() + ": Event can't be started, because custom npc table is disabled!");
			eventMaker.sendMessage("Event " + getName() + " can't be started because custom NPC table is disabled!");
			return false;
		}
		// Initialize list
		_npclist = new ArrayList<>();
		_players = ConcurrentHashMap.newKeySet();
		// Set Event active
		_isactive = true;
		// Spawn Manager
		_npc = recordSpawn(_start_npc, 18429, 145861, -3090, 0, false, 0);
		
		// Announce event start
		Broadcast.toAllOnlinePlayers("* Race Event started! *");
		Broadcast.toAllOnlinePlayers("Visit Event Manager in Dion village and signup, you have " + _time_register + " min before Race Start...");
		
		// Schedule Event end
		_eventTask = ThreadPool.schedule(this::StartRace, _time_register * 60 * 1000);
		
		return true;
		
	}
	
	protected void StartRace()
	{
		// Abort race if no players signup
		if (_players.isEmpty())
		{
			Broadcast.toAllOnlinePlayers("Race aborted, nobody signup.");
			eventStop();
			return;
		}
		// Set state
		_isRaceStarted = true;
		// Announce
		Broadcast.toAllOnlinePlayers("Race started!");
		// Get random Finish
		final int location = getRandom(0, _locations.length - 1);
		_randspawn = _coords[location];
		// And spawn NPC
		recordSpawn(_stop_npc, _randspawn[0], _randspawn[1], _randspawn[2], _randspawn[3], false, 0);
		// Transform players and send message
		for (Player player : _players)
		{
			if ((player != null) && player.isOnline())
			{
				if (isInsideRadius2D(player, _npc, 500))
				{
					sendMessage(player, "Race started! Go find Finish NPC as fast as you can... He is located near " + _locations[location]);
					transformPlayer(player);
					player.getRadar().addMarker(_randspawn[0], _randspawn[1], _randspawn[2]);
				}
				else
				{
					sendMessage(player, "I told you stay near me right? Distance was too high, you are excluded from race");
					_players.remove(player);
				}
			}
		}
		// Schedule timeup for Race
		_eventTask = ThreadPool.schedule(() -> timeUp(), _time_race * 60 * 1000);
	}
	
	@Override
	public boolean eventStop()
	{
		// Don't stop inactive event
		if (!_isactive)
		{
			return false;
		}
		
		// Set inactive
		_isactive = false;
		_isRaceStarted = false;
		
		// Cancel task if any
		if (_eventTask != null)
		{
			_eventTask.cancel(true);
			_eventTask = null;
		}
		// Untransform players
		// Teleport to event start point
		for (Player player : _players)
		{
			if ((player != null) && player.isOnline())
			{
				player.untransform();
				player.teleToLocation(_npc, true);
			}
		}
		// Despawn NPCs
		for (Npc _npc : _npclist)
		{
			if (_npc != null)
			{
				_npc.deleteMe();
			}
		}
		_npclist.clear();
		_players.clear();
		// Announce event end
		Broadcast.toAllOnlinePlayers("* Race Event finished *");
		
		return true;
	}
	
	@Override
	public boolean eventBypass(Player activeChar, String bypass)
	{
		if (bypass.startsWith("skill"))
		{
			if (_isRaceStarted)
			{
				activeChar.sendMessage("Race already started, you cannot change transform skill now");
			}
			else
			{
				final int _number = Integer.parseInt(bypass.substring(5));
				final Skill _sk = SkillEngine.getInstance().getSkill(_number, 1);
				if (_sk != null)
				{
					_skill = _number;
					activeChar.sendMessage("Transform skill set to:");
					activeChar.sendMessage(_sk.getName());
				}
				else
				{
					activeChar.sendMessage("Error while changing transform skill");
				}
			}
			
		}
		else if (bypass.startsWith("tele"))
		{
			if ((Integer.parseInt(bypass.substring(4)) > 0) && (_randspawn != null))
			{
				activeChar.teleToLocation(_randspawn[0], _randspawn[1], _randspawn[2]);
			}
			else
			{
				activeChar.teleToLocation(18429, 145861, -3090);
			}
		}
		showMenu(activeChar);
		return true;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		if (event.equalsIgnoreCase("transform"))
		{
			transformPlayer(player);
			return null;
		}
		else if (event.equalsIgnoreCase("untransform"))
		{
			player.untransform();
			return null;
		}
		else if (event.equalsIgnoreCase("showfinish"))
		{
			player.getRadar().addMarker(_randspawn[0], _randspawn[1], _randspawn[2]);
			return null;
		}
		else if (event.equalsIgnoreCase("signup"))
		{
			if (_players.contains(player))
			{
				return "900103-onlist.htm";
			}
			_players.add(player);
			return "900103-signup.htm";
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			player.untransform();
			if (_players.contains(player))
			{
				_players.remove(player);
			}
			return "900103-quit.htm";
		}
		else if (event.equalsIgnoreCase("finish"))
		{
			if (player.isAffectedBySkill(_skill))
			{
				winRace(player);
				return "900104-winner.htm";
			}
			return "900104-notrans.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		getQuestState(player, true);
		
		if (npc.getId() == _start_npc)
		{
			if (_isRaceStarted)
			{
				return _start_npc + "-started-" + isRacing(player) + ".htm";
			}
			return _start_npc + "-" + isRacing(player) + ".htm";
		}
		else if ((npc.getId() == _stop_npc) && _isRaceStarted)
		{
			return _stop_npc + "-" + isRacing(player) + ".htm";
		}
		return npc.getId() + ".htm";
	}
	
	private int isRacing(Player player)
	{
		return _players.contains(player) ? 1 : 0;
	}
	
	private Npc recordSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffSet, long despawnDelay)
	{
		final Npc npc = addSpawn(npcId, x, y, z, heading, randomOffSet, despawnDelay);
		if (npc != null)
		{
			_npclist.add(npc);
		}
		return npc;
	}
	
	private void transformPlayer(Player player)
	{
		if (player.isTransformed())
		{
			player.untransform();
		}
		if (player.isSitting())
		{
			player.standUp();
		}
		
		player.getEffectList().stopEffects(AbnormalType.SPEED_UP);
		player.stopSkillEffects(true, 268);
		player.stopSkillEffects(true, 298); // Rabbit Spirit Totem
		SkillEngine.getInstance().getSkill(_skill, 1).applyEffects(player, player);
	}
	
	private void sendMessage(Player player, String text)
	{
		player.sendPacket(new CreatureSay(_npc.getObjectId(), ChatType.MPCC_ROOM, _npc.getName(), text));
	}
	
	private void showMenu(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		final String content = getHtm(activeChar, "admin_menu.htm");
		html.setHtml(content);
		activeChar.sendPacket(html);
	}
	
	protected void timeUp()
	{
		Broadcast.toAllOnlinePlayers("Time up, nobody wins!");
		eventStop();
	}
	
	private void winRace(Player player)
	{
		final int[] _reward = _rewards[getRandom(_rewards.length - 1)];
		player.addItem("eventModRace", _reward[0], _reward[1], _npc, true);
		Broadcast.toAllOnlinePlayers(player.getName() + " is a winner!");
		eventStop();
	}
	
	public static ScriptEvent provider()
	{
		return new Race();
	}
}