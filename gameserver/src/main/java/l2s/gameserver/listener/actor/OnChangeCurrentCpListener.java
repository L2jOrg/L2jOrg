package l2s.gameserver.listener.actor;

import l2s.gameserver.model.Creature;
import l2s.gameserver.listener.CharListener;

public interface OnChangeCurrentCpListener extends CharListener
{
	public void onChangeCurrentCp(Creature actor, double oldCp, double newCp);
}