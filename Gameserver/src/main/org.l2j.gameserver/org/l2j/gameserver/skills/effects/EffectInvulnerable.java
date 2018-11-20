package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.Skill.SkillType;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectInvulnerable extends Effect
{
	public EffectInvulnerable(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		Skill skill = getEffected().getCastingSkill();
		if(skill != null && skill.getSkillType() == SkillType.TAKECASTLE)
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getInvulnerable().start(this);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getInvulnerable().stop(this);
	}
}