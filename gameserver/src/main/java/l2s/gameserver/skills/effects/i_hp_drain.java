package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

public class i_hp_drain extends i_abstract_effect
{
	private final double _absorbPercent;

	public i_hp_drain(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_absorbPercent = template.getParam().getDouble("absorb_percent", 0.);
	}

	@Override
	public boolean checkCondition()
	{
		if(getValue() <= 0)
			return false;
		if(!getEffected().isPlayable() && Config.DISABLE_VAMPIRIC_VS_MOB_ON_PVP)
			return false;
		if(getEffector().getPvpFlag() != 0)
			return false;
		return super.checkCondition();
	}

	@Override
	public void instantUse()
	{
		Creature realTarget = isReflected() ? getEffector() : getEffected();

		if(realTarget.isDead())
			return;

		double targetHp = realTarget.getCurrentHp();
		double targetCP = realTarget.getCurrentCp();

		double damage = 0;
		if(getSkill().isMagic())
		{
			AttackInfo info = Formulas.calcMagicDam(getEffector(), realTarget, getSkill(), getValue(), getSkill().isSSPossible());
			realTarget.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, false, true, false, false, true, true, info.crit, info.miss, info.shld);
			if(info.damage >= 1)
			{
				double lethalDmg = Formulas.calcLethalDamage(getEffector(), realTarget, getSkill());
				if(lethalDmg > 0)
					realTarget.reduceCurrentHp(lethalDmg, getEffector(), getSkill(), true, true, false, false, false, false, false);
			}
			damage = info.damage;
		}
		else
		{
			AttackInfo info = Formulas.calcPhysDam(getEffector(), realTarget, getSkill(), 1.0D, getValue(), false, false, getSkill().isSSPossible(), false, true);
			if(info != null)
			{
				realTarget.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, false, true, false, false, true, true, info.crit || info.blow, info.miss, info.shld);
				if(!info.miss || info.damage >= 1)
				{
					double lethalDmg = Formulas.calcLethalDamage(getEffector(), realTarget, getSkill());
					if(lethalDmg > 0)
						realTarget.reduceCurrentHp(lethalDmg, getEffector(), getSkill(), true, true, false, false, false, false, false);
					else if(!isReflected())
						realTarget.doCounterAttack(getSkill(), getEffector(), false);
				}
				damage = info.damage;
			}
		}

		if(_absorbPercent > 0 && !getEffector().isHealBlocked())
		{
			double hp = 0;

			if(damage > targetCP || !realTarget.isPlayer())
				hp = (damage - targetCP) * (_absorbPercent / 100.);

			if(hp > targetHp)
				hp = targetHp;

			double addToHp = Math.max(0, Math.min(hp, getEffector().calcStat(Stats.HP_LIMIT, null, null) * getEffector().getMaxHp() / 100. - getEffector().getCurrentHp()));
			if(addToHp > 0)
				getEffector().setCurrentHp(getEffector().getCurrentHp() + addToHp, false);
		}
	}
}