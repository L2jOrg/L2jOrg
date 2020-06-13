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
package ai.bosses.Zaken;

import ai.AbstractNpcAI;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PlaySound;

/**
 * Zaken AI<br>
 * TODO: Skill cast?<br>
 * TODO: Day/Night spawn? TODO: Boss message broadcast.
 * @author Mobius
 */
public class Zaken extends AbstractNpcAI
{
	// NPC
	private static final int ZAKEN = 29022;
	// Location
	private static final int ZAKEN_X = 52207;
	private static final int ZAKEN_Y = 217230;
	private static final int ZAKEN_Z = -3341;
	// Misc
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	
	private Zaken()
	{
		addKillId(ZAKEN);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ZAKEN);
		final int status = GrandBossManager.getInstance().getBossStatus(ZAKEN);
		if (status == DEAD)
		{
			// load the unlock date and time from DB
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			if (temp > 0)
			{
				startQuestTimer("zaken_unlock", temp, null, null);
			}
			else // the time has already expired while the server was offline
			{
				spawnBoss();
			}
		}
		else
		{
			spawnBoss();
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equals("zaken_unlock"))
		{
			spawnBoss();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	private void spawnBoss()
	{
		final GrandBoss zaken = (GrandBoss) addSpawn(ZAKEN, ZAKEN_X, ZAKEN_Y, ZAKEN_Z, 0, false, 0);
		GrandBossManager.getInstance().setBossStatus(ZAKEN, ALIVE);
		GrandBossManager.getInstance().addBoss(zaken);
		zaken.broadcastPacket(new PlaySound(1, "BS01_A", 1, zaken.getObjectId(), zaken.getX(), zaken.getY(), zaken.getZ()));
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		GrandBossManager.getInstance().setBossStatus(ZAKEN, DEAD);
		// Calculate Min and Max respawn times randomly.
		final long respawnTime = (Config.ZAKEN_SPAWN_INTERVAL + getRandom(-Config.ZAKEN_SPAWN_RANDOM, Config.ZAKEN_SPAWN_RANDOM)) * 3600000;
		startQuestTimer("zaken_unlock", respawnTime, null, null);
		// also save the respawn time so that the info is maintained past reboots
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ZAKEN);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatsSet(ZAKEN, info);
		return super.onKill(npc, killer, isSummon);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Zaken();
	}
}
