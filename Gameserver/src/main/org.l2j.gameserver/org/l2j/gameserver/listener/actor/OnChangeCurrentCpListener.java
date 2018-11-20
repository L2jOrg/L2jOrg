package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.listener.CharListener;

public interface OnChangeCurrentCpListener extends CharListener
{
	public void onChangeCurrentCp(Creature actor, double oldCp, double newCp);
}