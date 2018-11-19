package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.templates.StatsSet;

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