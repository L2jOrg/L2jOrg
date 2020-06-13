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
package ai.bosses.Core;

import ai.AbstractNpcAI;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.PlaySound;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core AI.
 * @author DrLecter, Emperorc, Mobius
 */
public final class Core extends AbstractNpcAI
{
	// NPCs
	private static final int CORE = 29006;
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	private static final int SUSCEPTOR = 29011;
	// Spawns
	private static final Map<Integer, Location> MINNION_SPAWNS = new HashMap<>();
	{
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17191, 109298, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17564, 109548, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17855, 109552, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(18280, 109202, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(18784, 109253, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(18059, 108314, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17300, 108444, -6488));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17148, 110071, -6648));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(18318, 110077, -6648));
		MINNION_SPAWNS.put(DEATH_KNIGHT, new Location(17726, 110391, -6648));
		MINNION_SPAWNS.put(DOOM_WRAITH, new Location(17113, 110970, -6648));
		MINNION_SPAWNS.put(DOOM_WRAITH, new Location(17496, 110880, -6648));
		MINNION_SPAWNS.put(DOOM_WRAITH, new Location(18061, 110990, -6648));
		MINNION_SPAWNS.put(DOOM_WRAITH, new Location(18384, 110698, -6648));
		MINNION_SPAWNS.put(DOOM_WRAITH, new Location(17993, 111458, -6584));
		MINNION_SPAWNS.put(SUSCEPTOR, new Location(17297, 111470, -6584));
		MINNION_SPAWNS.put(SUSCEPTOR, new Location(17893, 110198, -6648));
		MINNION_SPAWNS.put(SUSCEPTOR, new Location(17706, 109423, -6488));
		MINNION_SPAWNS.put(SUSCEPTOR, new Location(17849, 109388, -6480));
	}
	// Misc
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	
	private static boolean _firstAttacked;
	
	private static final Collection<Attackable> _minions = ConcurrentHashMap.newKeySet();
	
	private Core()
	{
		registerMobs(CORE, DEATH_KNIGHT, DOOM_WRAITH, SUSCEPTOR);
		
		_firstAttacked = false;
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
		if (GrandBossManager.getInstance().getBossStatus(CORE) == DEAD)
		{
			// Load the unlock date and time for Core from DB.
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			// If Core is locked until a certain time, mark it so and start the unlock timer the unlock time has not yet expired.
			if (temp > 0)
			{
				startQuestTimer("core_unlock", temp, null, null);
			}
			else
			{
				// The time has already expired while the server was offline. Immediately spawn Core.
				final GrandBoss core = (GrandBoss) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
				GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
				spawnBoss(core);
			}
		}
		else
		{
			final boolean test = GlobalVariablesManager.getInstance().getBoolean("Core_Attacked", false);
			if (test)
			{
				_firstAttacked = true;
			}
			final int loc_x = info.getInt("loc_x");
			final int loc_y = info.getInt("loc_y");
			final int loc_z = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final double hp = info.getDouble("currentHP");
			final double mp = info.getDouble("currentMP");
			final GrandBoss core = (GrandBoss) addSpawn(CORE, loc_x, loc_y, loc_z, heading, false, 0);
			core.setCurrentHpMp(hp, mp);
			spawnBoss(core);
		}
	}
	
	@Override
	public void onSave()
	{
		GlobalVariablesManager.getInstance().set("Core_Attacked", _firstAttacked);
	}
	
	public void spawnBoss(GrandBoss npc)
	{
		GrandBossManager.getInstance().addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		// Spawn minions
		Attackable mob;
		Location spawnLocation;
		for (Entry<Integer, Location> spawn : MINNION_SPAWNS.entrySet())
		{
			spawnLocation = spawn.getValue();
			mob = (Attackable) addSpawn(spawn.getKey(), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), getRandom(61794), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("core_unlock"))
		{
			final GrandBoss core = (GrandBoss) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
			GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
			spawnBoss(core);
		}
		else if (event.equalsIgnoreCase("spawn_minion"))
		{
			final Attackable mob = (Attackable) addSpawn(npc.getId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
		}
		else if (event.equalsIgnoreCase("despawn_minions"))
		{
			_minions.forEach(Attackable::decayMe);
			_minions.clear();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (npc.getId() == CORE)
		{
			if (_firstAttacked)
			{
				if (getRandom(100) == 0)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.REMOVING_INTRUDERS);
				}
			}
			else
			{
				_firstAttacked = true;
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_NON_PERMITTED_TARGET_HAS_BEEN_DISCOVERED);
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.INTRUDER_REMOVAL_SYSTEM_INITIATED);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == CORE)
		{
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_FATAL_ERROR_HAS_OCCURRED);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SYSTEM_IS_BEING_SHUT_DOWN);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.EMPTY);
			_firstAttacked = false;
			
			GrandBossManager.getInstance().setBossStatus(CORE, DEAD);
			// Calculate Min and Max respawn times randomly.
			final long respawnTime = (Config.CORE_SPAWN_INTERVAL + getRandom(-Config.CORE_SPAWN_RANDOM, Config.CORE_SPAWN_RANDOM)) * 3600000;
			startQuestTimer("core_unlock", respawnTime, null, null);
			// Also save the respawn time so that the info is maintained past reboots.
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(CORE, info);
			startQuestTimer("despawn_minions", 20000, null, null);
			cancelQuestTimers("spawn_minion");
		}
		else if ((GrandBossManager.getInstance().getBossStatus(CORE) == ALIVE) && _minions.contains(npc))
		{
			_minions.remove(npc);
			startQuestTimer("spawn_minion", 60000, npc, null);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == CORE)
		{
			npc.setIsImmobilized(true);
		}
		return super.onSpawn(npc);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Core();
	}
}
