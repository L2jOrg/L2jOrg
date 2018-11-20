package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.StatsSet;

public class EffectsFromSkills extends Skill
{
	public EffectsFromSkills(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		for(AddedSkill as : getAddedSkills())
		{
			SkillEntry skillEntry = as.getSkill();
			if(skillEntry != null)
				skillEntry.getEffects(activeChar, target);
		}
	}
}