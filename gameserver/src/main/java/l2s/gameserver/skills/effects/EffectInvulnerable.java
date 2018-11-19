package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

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