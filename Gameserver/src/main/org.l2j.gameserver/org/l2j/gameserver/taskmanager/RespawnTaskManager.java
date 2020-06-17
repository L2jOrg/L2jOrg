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
package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author Mobius
 */
public class RespawnTaskManager {

	private static final Map<Npc, Long> PENDING_RESPAWNS = new ConcurrentHashMap<>();

	public RespawnTaskManager() {
		ThreadPool.scheduleAtFixedRate(() -> {
			final long time = System.currentTimeMillis();

			for (Entry<Npc, Long> entry : PENDING_RESPAWNS.entrySet()) {
				if (time > entry.getValue()) {
				final Npc npc = entry.getKey();
				PENDING_RESPAWNS.remove(npc);
				final Spawn spawn = npc.getSpawn();
				if (spawn != null) {
					spawn.respawnNpc(npc);
					spawn._scheduledCount--;
				}
			}
		} }, 0, 1000);
	}

	public void add(Npc npc, long time)
	{
		PENDING_RESPAWNS.put(npc, time);
	}
	
	public static RespawnTaskManager getInstance()
	{
		return Singleton.INSTANCE;
	}
	
	private static class Singleton {
		private static final RespawnTaskManager INSTANCE = new RespawnTaskManager();
	}
}
