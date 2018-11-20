package org.l2j.gameserver.listener.actor.npc;

import org.l2j.gameserver.listener.NpcListener;
import org.l2j.gameserver.model.instances.NpcInstance;

public interface OnDecayListener extends NpcListener
{
	public void onDecay(NpcInstance actor);
}