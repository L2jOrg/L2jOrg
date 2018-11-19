package l2s.gameserver.listener.actor;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Creature;

public interface OnDeathListener extends CharListener
{
	public void onDeath(Creature actor, Creature killer);
}