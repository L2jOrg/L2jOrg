package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;

public interface OnChangeCurrentMpListener extends CharListener
{
	public void onChangeCurrentMp(Creature actor, double oldMp, double newMp);
}