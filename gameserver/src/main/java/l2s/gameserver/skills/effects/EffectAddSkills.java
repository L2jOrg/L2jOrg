package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill.AddedSkill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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