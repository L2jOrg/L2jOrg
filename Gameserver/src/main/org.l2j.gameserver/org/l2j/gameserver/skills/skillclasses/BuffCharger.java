package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.templates.StatsSet;

public class BuffCharger extends Skill
{
	private int _target;

	public BuffCharger(StatsSet set)
	{
		super(set);
		_target = set.getInteger("targetBuff", 0);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		int level = 0;
		for(Abnormal effect : target.getAbnormalList())
		{
			if(effect.getSkill().getId() == _target)
			{
				level = effect.getSkill().getLevel();
				break;
			}
		}

		Skill next = SkillHolder.getInstance().getSkill(_target, level + 1);
		if(next != null)
			next.getEffects(activeChar, target);
		else	// @Rivelia. Buff Chargers are able to maintain their level.
		{
			next = SkillHolder.getInstance().getSkill(_target, level);
			if(next != null)
				next.getEffects(activeChar, target);
		}
	}
}