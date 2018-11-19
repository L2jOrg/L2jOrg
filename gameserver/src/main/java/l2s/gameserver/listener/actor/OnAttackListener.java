package l2s.gameserver.listener.actor;

import l2s.gameserver.listener.CharListener;
import l2s.gameserver.model.Creature;

public interface OnAttackListener extends CharListener
{
	public void onAttack(Creature actor, Creature target);
}