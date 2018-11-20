package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill);
}