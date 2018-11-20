package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;

public interface OnMagicHitListener extends CharListener
{
	public void onMagicHit(Creature actor, Skill skill, Creature caster);
}