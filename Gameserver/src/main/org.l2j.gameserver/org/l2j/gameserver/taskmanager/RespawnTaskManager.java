package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
