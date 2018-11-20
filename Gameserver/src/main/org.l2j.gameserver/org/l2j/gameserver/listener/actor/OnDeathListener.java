package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;

public interface OnDeathListener extends CharListener
{
	public void onDeath(Creature actor, Creature killer);
}