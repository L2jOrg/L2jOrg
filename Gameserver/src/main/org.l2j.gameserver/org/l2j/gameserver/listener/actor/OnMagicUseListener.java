package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt);
}