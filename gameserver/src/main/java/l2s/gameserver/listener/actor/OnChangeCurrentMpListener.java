package l2s.gameserver.listener.actor;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Creature;

public interface OnChangeCurrentMpListener extends CharListener
{
	public void onChangeCurrentMp(Creature actor, double oldMp, double newMp);
}