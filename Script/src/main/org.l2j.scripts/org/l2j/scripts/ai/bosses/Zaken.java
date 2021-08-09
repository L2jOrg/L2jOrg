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
package org.l2j.scripts.ai.bosses;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.BossStatus;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.scripts.ai.AbstractNpcAI;

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
	
	private Zaken()
	{
		addKillId(ZAKEN);
		
		final var info = GrandBossManager.getInstance().getBossData(ZAKEN);
		final var status = GrandBossManager.getInstance().getBossStatus(ZAKEN);
		if (status == BossStatus.DEAD)
		{
			// load the unlock date and time from DB
			final long temp = info.getRespawnTime() - System.currentTimeMillis();
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
		GrandBossManager.getInstance().setBossStatus(ZAKEN, BossStatus.ALIVE);
		GrandBossManager.getInstance().addBoss(zaken);
		zaken.broadcastPacket(PlaySound.music("BS01_A",zaken));
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		npc.broadcastPacket(PlaySound.music("BS02_D", npc));
		GrandBossManager.getInstance().setBossStatus(ZAKEN, BossStatus.DEAD);
		// Calculate Min and Max respawn times randomly.
		final long respawnTime = (Config.ZAKEN_SPAWN_INTERVAL + Rnd.get(-Config.ZAKEN_SPAWN_RANDOM, Config.ZAKEN_SPAWN_RANDOM)) * 3600000L;
		startQuestTimer("zaken_unlock", respawnTime, null, null);
		// also save the respawn time so that the info is maintained past reboots
		final var info = GrandBossManager.getInstance().getBossData(ZAKEN);
		info.setRespawnTime(System.currentTimeMillis() + respawnTime);
		return super.onKill(npc, killer, isSummon);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Zaken();
	}
}
