package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.StatsSet;

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