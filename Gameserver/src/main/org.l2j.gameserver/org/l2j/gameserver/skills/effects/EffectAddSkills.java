package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Skill.AddedSkill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectAddSkills extends Effect
{
	public EffectAddSkills(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		for(AddedSkill as : getSkill().getAddedSkills())
		{
			SkillEntry skillEntry = as.getSkill();
			if(skillEntry != null)
				getEffected().addSkill(skillEntry);
		}
	}

	@Override
	public void onExit()
	{
		for(AddedSkill as : getSkill().getAddedSkills())
		{
			SkillEntry skillEntry = as.getSkill();
			if(skillEntry != null)
				getEffected().removeSkill(skillEntry);
		}
	}
}