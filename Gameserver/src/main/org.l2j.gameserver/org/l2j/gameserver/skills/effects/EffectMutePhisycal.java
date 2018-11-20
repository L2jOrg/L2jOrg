package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectMutePhisycal extends Effect
{
	public EffectMutePhisycal(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().getFlags().getPMuted().start(this))
		{
			Skill castingSkill = getEffected().getCastingSkill();

			if(castingSkill != null && !castingSkill.isMagic())
				getEffected().abortCast(true, true);
		}
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getPMuted().stop(this);
	}
}