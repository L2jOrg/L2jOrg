package l2s.gameserver.listener.actor;

import l2s.gameserver.model.Creature;
import l2s.gameserver.listener.CharListener;

public interface OnDeathFromUndyingListener extends CharListener
{
	public void onDeathFromUndying(Creature actor, Creature killer);
}