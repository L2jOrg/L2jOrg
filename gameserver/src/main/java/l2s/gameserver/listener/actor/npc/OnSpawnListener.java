package l2s.gameserver.listener.actor.npc;

import l2s.gameserver.listener.NpcListener;
import l2s.gameserver.model.instances.NpcInstance;

public interface OnSpawnListener extends NpcListener
{
	public void onSpawn(NpcInstance actor);
}