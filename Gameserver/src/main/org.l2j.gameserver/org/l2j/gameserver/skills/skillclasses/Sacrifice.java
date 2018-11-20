package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.residences.SiegeFlagInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.StatsSet;

/**
 * Created by Archer on 8/5/2014.
 */
public class Sacrifice extends Skill
{
	/**
	 * Внимание!!! У наследников вручную надо поменять тип на public
	 *
	 * @param set парамерты скилла
	 */
	public Sacrifice(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(target == null || target.isDoor() || target instanceof SiegeFlagInstance)
			return false;

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isHealBlocked())
			return;

		final double addToHp = Math.max(0, Math.min(getPower(), target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));
		if(addToHp > 0)
		{
			target.setCurrentHp(addToHp + target.getCurrentHp(), false);

			if(getId() == 4051)
				target.sendPacket(SystemMsg.REJUVENATING_HP);
			else if(target.isPlayer())
			{
				if(activeChar == target)
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
				else
					target.sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(activeChar).addInteger(Math.round(addToHp)));
			}
		}
	}
}