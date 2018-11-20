package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;

public interface OnChangeCurrentHpListener extends CharListener
{
	public void onChangeCurrentHp(Creature actor, double oldHp, double newHp);
}