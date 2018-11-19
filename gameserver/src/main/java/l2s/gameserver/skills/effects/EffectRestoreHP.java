package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectRestoreHP extends EffectRestore
{
	private final boolean _cpIncluding;
	private double _power = 0;

	public EffectRestoreHP(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_cpIncluding = template.getParam().getBool("cp_including", false);
	}

	private int[] calcAddToHpCp()
	{
		double power = _power;
		int addToHp = (int) power;
		int addToCp = 0;

		if(_cpIncluding && getEffected().isPlayer())
		{
			double newHp = getEffected().getCurrentHp() + power;
			newHp = Math.max(0, Math.min(newHp, getEffected().getMaxHp() / 100. * getEffected().calcStat(Stats.HP_LIMIT, null, null)));

			addToHp = (int) Math.max(0, newHp - getEffected().getCurrentHp());

			if(_percent) // Проверить эту часть.
			{
				if(addToHp > 0)
					power = 0;
				else
				{
					power = getEffected().getMaxCp() / 100. * getValue();

					if(!_ignoreBonuses)
						power *= getEffected().calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
				}
			}
			else
				power = power - addToHp;

			if(power > 0)
			{
				double newCp = getEffected().getCurrentCp() + power;
				newCp = Math.max(0, Math.min(newCp, getEffected().getMaxCp() / 100. * getEffected().calcStat(Stats.CP_LIMIT, null, null)));

				addToCp = (int) Math.max(0, newCp - getEffected().getCurrentCp());
			}
		}
		return new int[]{ addToHp, addToCp };
	}

	@Override
	public void onStart()
	{
		_power = getValue();
		if(_power <= 0)
			return;

		if(!_staticPower)
		{
			if(!_percent)
			{
				_power += 0.1 * _power * Math.sqrt(getEffector().getMAtk(null, getSkill()) / 333);

				if(getSkill().isSSPossible() && getSkill().getHpConsume() == 0)
					_power *= 1 + ((200 + getEffector().getChargedSpiritshotPower()) * 0.001);

				if(getSkill().isMagic())
				{
					if(Formulas.calcMCrit(getEffector(), getEffected(), getSkill()))
					{
						_power *= 2;
						getEffector().sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT));
						getEffector().sendPacket(new ExMagicAttackInfo(getEffector().getObjectId(), getEffected().getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
						if(getEffected().isPlayer() && getEffector() != getEffected())
							getEffected().sendPacket(new ExMagicAttackInfo(getEffector().getObjectId(), getEffected().getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
					}
				}
			}
		}
		if(_percent)
			_power = getEffected().getMaxHp() / 100. * _power;

		if(!_staticPower)
		{
			if(!_ignoreBonuses)
			{
				_power *= getEffected().calcStat(Stats.HEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
				_power = getEffector().calcStat(Stats.HEAL_POWER, _power, getEffected(), getSkill());
			}
		}

		if(getEffected().isHealBlocked())
			return;

		int[] addToHpCp = calcAddToHpCp();

		int addToHp = addToHpCp[0];
		if(!getTemplate().isInstant())
		{
			if(getEffected().isPlayer())
				getEffected().sendPacket(new ExRegenMaxPacket(addToHp, getDuration(), getInterval()));
			return;
		}

		if(addToHp > 0)
		{
			addToHp = (int) Math.min(getEffected().getMaxHp() - getEffected().getCurrentHp(), addToHp);
			if(getSkill().getId() == 4051)
				getEffected().sendPacket(SystemMsg.REJUVENATING_HP);
			else if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToHp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(addToHp));

			getEffected().setCurrentHp(getEffected().getCurrentHp() + addToHp, false);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_HP));
		}

		int addToCp = addToHpCp[1];
		if(addToCp > 0)
		{
			addToCp = (int) Math.min(getEffected().getMaxCp() - getEffected().getCurrentCp(), addToCp);
			if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_CP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToCp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger(addToCp));

			getEffected().setCurrentCp(getEffected().getCurrentCp() + addToCp);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_CP));
		}
	}

	@Override
	public boolean onActionTime()
	{
		if(getTemplate().isInstant())
			return false;

		if(getEffected().isHealBlocked())
			return true;

		int[] addToHpCp = calcAddToHpCp();

		int addToHp = addToHpCp[0];
		if(addToHp > 0)
		{
			getEffected().setCurrentHp(getEffected().getCurrentHp() + addToHp, false);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_HP));
		}

		int addToCp = addToHpCp[1];
		if(addToCp > 0)
		{
			getEffected().setCurrentCp(getEffected().getCurrentCp() + addToCp);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_CP));
		}

		return true;
	}
}