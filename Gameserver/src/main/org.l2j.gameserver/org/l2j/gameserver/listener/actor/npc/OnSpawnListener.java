package org.l2j.gameserver.listener.actor.npc;

import org.l2j.gameserver.listener.NpcListener;
import org.l2j.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}