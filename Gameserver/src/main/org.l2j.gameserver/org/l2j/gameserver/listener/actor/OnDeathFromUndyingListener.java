package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.listener.CharListener;

public interface OnDeathFromUndyingListener extends CharListener
{
	public void onDeathFromUndying(Creature actor, Creature killer);
}