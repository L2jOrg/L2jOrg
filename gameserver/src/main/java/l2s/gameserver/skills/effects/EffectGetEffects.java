package l2s.gameserver.skills.effects;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectGetEffects extends Effect
{
	private final Skill _effectsSkill;

	public EffectGetEffects(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		final int skillId = template.getParam().getInteger("effects_skill_id");
		final int skillLvl = template.getParam().getInteger("effects_skill_level", 1);

		_effectsSkill = SkillHolder.getInstance().getSkill(skillId, skillLvl);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effectsSkill == null)
		{
			//getEffector().sendMessage("_effectsSkill == null");
			return false;
		}

		final int chance = _effectsSkill.getActivateRate();
		if(chance >= 0)
		{
			if(!Formulas.calcEffectsSuccess(getEffector(), getEffected(), _effectsSkill, chance))
			{
				if(getEffected() == getEffector().getCastingTarget() && getSkill() == getEffector().getCastingSkill())
					getEffector().sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(_effectsSkill));
				return false;
			}
		}
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		_effectsSkill.getEffects(getEffector(), getEffected());
	}

	@Override
	public void onStart()
	{
		if(!getTemplate().isInstant())
		{
			if(!_effectsSkill.getEffects(getEffector(), getEffected(), getTimeLeft() * 1000, 1.0))
				getAbnormal().exit();
		}
	}

	@Override
	public void onExit()
	{
		if(!getTemplate().isInstant())
			getEffected().getAbnormalList().stop(_effectsSkill);
	}
}