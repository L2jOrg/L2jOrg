package org.l2j.gameserver.model.instances;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.ai.NpcAI;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.s2c.DiePacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public DeadManInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setAI(new NpcAI(this));
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		setCurrentHp(0, false);
		broadcastPacket(new DiePacket(this));
		setWalking();
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{}

	@Override
	public boolean isBlocked()
	{
		return true;
	}
}