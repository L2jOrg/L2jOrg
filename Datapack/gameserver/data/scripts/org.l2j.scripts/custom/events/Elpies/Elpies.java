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
package custom.events.Elpies;

import events.ScriptEvent;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.EventMonster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.util.Broadcast;

import java.util.concurrent.ScheduledFuture;

public final class Elpies extends Event implements ScriptEvent
{
	// NPC
	private static final int ELPY = 900100;
	// Amount of Elpies to spawn when the event starts
	private static final int ELPY_AMOUNT = 100;
	// Event duration in minutes
	private static final int EVENT_DURATION_MINUTES = 2;
	// @formatter:off
	private static final int[][] DROPLIST_CONSUMABLES =
	{
		// itemId, chance, min amount, max amount
		{  1540, 80, 10, 15 },	// Quick Healing Potion
		{  1538, 60,  5, 10 },	// Blessed Scroll of Escape
		{  3936, 40,  5, 10 },	// Blessed Scroll of Ressurection
		{  6387, 25,  5, 10 },	// Blessed Scroll of Ressurection Pets
		{ 22025, 15,  5, 10 },	// Powerful Healing Potion
		{  6622, 10,  1, 1 },	// Giant's Codex
		{ 20034,  5,  1, 1 },	// Revita Pop
		{ 20004,  1,  1, 1 },	// Energy Ginseng
		{ 20004,  0,  1, 1 }	// Energy Ginseng
	};
	
	private static final int[][] DROPLIST_CRYSTALS =
	{
		{ 1458, 80, 50, 100 },	// Crystal D-Grade
		{ 1459, 60, 40,  80 },	// Crystal C-Grade
		{ 1460, 40, 30,  60 },	// Crystal B-Grade
		{ 1461, 20, 20,  30 },	// Crystal A-Grade
		{ 1462,  0, 10,  20 }	// Crystal S-Grade
	};
	// @formatter:on
	// Non-final variables
	private static boolean EVENT_ACTIVE = false;
	private static int CURRENT_ELPY_COUNT = 0;
	private ScheduledFuture<?> _eventTask = null;
	
	private Elpies()
	{
		addSpawnId(ELPY);
		addKillId(ELPY);
	}
	
	@Override
	public boolean eventBypass(Player activeChar, String bypass)
	{
		return false;
	}
	
	@Override
	public boolean eventStart(Player eventMaker)
	{
		if (EVENT_ACTIVE)
		{
			return false;
		}
		
		// Check Custom Table - we use custom NPCs
		if (!Config.CUSTOM_NPC_DATA)
		{
			LOGGER.info(getName() + ": Event can't be started because custom NPC table is disabled!");
			eventMaker.sendMessage("Event " + getName() + " can't be started because custom NPC table is disabled!");
			return false;
		}
		
		EVENT_ACTIVE = true;

		final EventLocation randomLoc = getRandomEntry(EventLocation.values());
		
		CURRENT_ELPY_COUNT = 0;
		final long despawnDelay = EVENT_DURATION_MINUTES * 60000;
		
		for (int i = 0; i < ELPY_AMOUNT; i++)
		{
			addSpawn(ELPY, randomLoc.getRandomX(), randomLoc.getRandomY(), randomLoc.getZ(), 0, true, despawnDelay);
			CURRENT_ELPY_COUNT++;
		}
		
		Broadcast.toAllOnlinePlayers("*Squeak Squeak*");
		Broadcast.toAllOnlinePlayers("Elpy invasion in " + randomLoc.getName());
		Broadcast.toAllOnlinePlayers("Help us exterminate them!");
		Broadcast.toAllOnlinePlayers("You have " + EVENT_DURATION_MINUTES + " minutes!");
		
		_eventTask = ThreadPool.schedule(() ->
		{
			Broadcast.toAllOnlinePlayers("Time is up!");
			eventStop();
		}, despawnDelay);
		return true;
	}
	
	@Override
	public boolean eventStop()
	{
		if (!EVENT_ACTIVE)
		{
			return false;
		}
		
		EVENT_ACTIVE = false;
		
		if (_eventTask != null)
		{
			_eventTask.cancel(true);
			_eventTask = null;
		}
		
		for (Spawn spawn : SpawnTable.getInstance().getSpawns(ELPY))
		{
			final Npc npc = spawn.getLastSpawn();
			if (npc != null)
			{
				npc.deleteMe();
			}
		}
		
		Broadcast.toAllOnlinePlayers("*Squeak Squeak*");
		Broadcast.toAllOnlinePlayers("Elpy Event finished!");
		return true;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (EVENT_ACTIVE)
		{
			dropItem(npc, killer, DROPLIST_CONSUMABLES);
			dropItem(npc, killer, DROPLIST_CRYSTALS);
			CURRENT_ELPY_COUNT--;
			
			if (CURRENT_ELPY_COUNT <= 0)
			{
				Broadcast.toAllOnlinePlayers("All elpies have been killed!");
				eventStop();
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		((EventMonster) npc).eventSetDropOnGround(true);
		((EventMonster) npc).eventSetBlockOffensiveSkills(true);
		return super.onSpawn(npc);
	}
	
	private enum EventLocation
	{
		ADEN("Aden", 146558, 148341, 26622, 28560, -2200),
		DION("Dion", 18564, 19200, 144377, 145782, -3081),
		GLUDIN("Gludin", -84040, -81420, 150257, 151175, -3125),
		HV("Hunters Village", 116094, 117141, 75776, 77072, -2700),
		OREN("Oren", 82048, 82940, 53240, 54126, -1490);
		
		private final String _name;
		private final int _minX;
		private final int _maxX;
		private final int _minY;
		private final int _maxY;
		private final int _z;
		
		EventLocation(String name, int minX, int maxX, int minY, int maxY, int z)
		{
			_name = name;
			_minX = minX;
			_maxX = maxX;
			_minY = minY;
			_maxY = maxY;
			_z = z;
		}
		
		public String getName()
		{
			return _name;
		}
		
		public int getRandomX()
		{
			return getRandom(_minX, _maxX);
		}
		
		public int getRandomY()
		{
			return getRandom(_minY, _maxY);
		}
		
		public int getZ()
		{
			return _z;
		}
	}
	
	private static void dropItem(Npc mob, Player player, int[][] droplist)
	{
		final int chance = getRandom(100);
		
		for (int[] drop : droplist)
		{
			if (chance >= drop[1])
			{
				mob.dropItem(player, drop[0], getRandom(drop[2], drop[3]));
				break;
			}
		}
	}
	
	public static ScriptEvent provider()
	{
		return new Elpies();
	}
}
