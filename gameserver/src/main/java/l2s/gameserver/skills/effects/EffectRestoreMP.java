package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectRestoreMP extends EffectRestore
{
	public EffectRestoreMP(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	private int calcAddToMp()
	{
		double power = getValue();
		if(power <= 0)
			return 0;

		if(!_staticPower)
		{
			if(!_percent)
			{
				//TODO: Check formulas.
				if(getSkill().isSSPossible() && Config.MANAHEAL_SPS_BONUS)
					power *= 1 + ((200 + getEffector().getChargedSpiritshotPower()) * 0.001);
			}
		}

		if(_percent)
			power = getEffected().getMaxMp() / 100. * power;

		if(!_staticPower)
		{
			if(!_ignoreBonuses)
			{
				if(_percent || getEffector() != getEffected()) // TODO: Check this:
					power *= getEffected().calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) / 100.;
			}
			else if(!_percent)
				// TODO: Check this:
				power *= 1.7;
		}

		double newMp = getEffected().getCurrentMp() + power;

		if(!_staticPower)
		{
			if(!_percent && getSkill().getTargetType() != Skill.SkillTargetType.TARGET_SELF)
			{
				// Обработка разницы в левелах при речардже. Учитывыется разница уровня скилла и уровня цели.
				// 1013 = id скилла recharge. Для сервиторов не проверено убавление маны, пока оставлено так как есть.
				if(getSkill().getMagicLevel() > 0)
				{
					if(getEffected().getLevel() > getSkill().getMagicLevel())
					{
						int lvlDiff = getEffected().getLevel() - getSkill().getMagicLevel();

						if(lvlDiff == 6)
							newMp *= 0.9;
						else if(lvlDiff == 7)
							newMp *= 0.8;
						else if(lvlDiff == 8)
							newMp *= 0.7;
						else if(lvlDiff == 9)
							newMp *= 0.6;
						else if(lvlDiff == 10)
							newMp *= 0.5;
						else if(lvlDiff == 11)
							newMp *= 0.4;
						else if(lvlDiff == 12)
							newMp *= 0.3;
						else if(lvlDiff == 13)
							newMp *= 0.2;
						else if(lvlDiff == 14)
							newMp *= 0.1;
						else if(lvlDiff >= 15)
							newMp = 0;
					}
				}
			}
		}

		newMp = Math.max(0, Math.min(newMp, getEffected().getMaxMp() / 100. * getEffected().calcStat(Stats.MP_LIMIT, null, null)));
		return (int) Math.max(0, newMp - getEffected().getCurrentMp());
	}

	@Override
	public void onStart()
	{
		if(getEffected().isHealBlocked())
			return;

		if(!getTemplate().isInstant())
			return;

		int addToMp = calcAddToMp();
		if(addToMp > 0)
		{
			addToMp = (int) Math.min(getEffected().getMaxMp() - getEffected().getCurrentMp(), addToMp);
			if(getEffector() != getEffected())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(addToMp));
			else
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(addToMp));

			getEffected().setCurrentMp(getEffected().getCurrentMp() + addToMp);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_MP));
		}
	}

	@Override
	public boolean onActionTime()
	{
		if(getTemplate().isInstant())
			return false;

		if(getEffected().isHealBlocked())
			return true;

		int addToMp = calcAddToMp();
		if(addToMp > 0)
		{
			getEffected().setCurrentMp(getEffected().getCurrentMp() + addToMp);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_MP));
		}

		return true;
	}
}