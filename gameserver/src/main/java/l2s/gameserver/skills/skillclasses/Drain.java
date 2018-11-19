package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class Drain extends Skill
{
	private double _absorbAbs;

	public Drain(StatsSet set)
	{
		super(set);
		_absorbAbs = set.getDouble("absorbAbs", 0.f);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!canAbsorb(target, activeChar))
			return;

		if(getPower() <= 0 && _absorbAbs <= 0) // Если == 0 значит скилл "отключен"
			return;

		final Creature realTarget = reflected ? activeChar : target;
		final boolean corpseSkill = getTargetType() == SkillTargetType.TARGET_CORPSE || getTargetType() == Skill.SkillTargetType.TARGET_AREA_AIM_CORPSE;

		if(realTarget.isDead() && !corpseSkill)
			return;

		final double targetHp = realTarget.getCurrentHp();

		double hp = 0.;
		if(!corpseSkill)
		{
			double damage = 0.;
			if(isMagic())
			{
				AttackInfo info = Formulas.calcMagicDam(activeChar, realTarget, this, isSSPossible());
				realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit, info.miss, info.shld);
				if(info.damage >= 1)
				{
					double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
					if(lethalDmg > 0)
						realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
				}

				damage = info.damage;
			}
			else
			{
				AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, isSSPossible(), false);
				if (info != null)
				{
					realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit || info.blow, info.miss, info.shld);
					if(!info.miss || info.damage >= 1)
					{
						double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
						if(lethalDmg > 0)
							realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
						else if(!reflected)
							realTarget.doCounterAttack(this, activeChar, false);
					}

					damage = info.damage;
				}
			}

			double targetCP = realTarget.getCurrentCp();

			// Нельзя восстанавливать HP из CP
			if(damage > targetCP || !realTarget.isPlayer())
				hp = (damage - targetCP) * getAbsorbPart();
		}

		if(_absorbAbs == 0 && getAbsorbPart() == 0)
			return;

		hp += _absorbAbs;

		// Нельзя восстановить больше hp, чем есть у цели.
		if(hp > targetHp && !corpseSkill)
			hp = targetHp;

		double addToHp = Math.max(0, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, null, null) * activeChar.getMaxHp() / 100. - activeChar.getCurrentHp()));

		if(addToHp > 0 && !activeChar.isHealBlocked())
			activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);
	}

	private boolean canAbsorb(Creature attacked, Creature attacker)
	{
		if(attacked.isPlayable() || !Config.DISABLE_VAMPIRIC_VS_MOB_ON_PVP)
			return true;
		return attacker.getPvpFlag() == 0;		
	}	
}