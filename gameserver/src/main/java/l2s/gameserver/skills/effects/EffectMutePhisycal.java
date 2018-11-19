package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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