package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class t_hp extends Effect
{
	private final boolean _percent;

	public t_hp(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_percent = getTemplate().getParam().getBool("percent", false);
	}

	@Override
	public void onStart()
	{
		giveDamage(true);
	}

	@Override
	public boolean onActionTime()
	{
		return giveDamage(false);
	}

	private boolean giveDamage(boolean first)
	{
		if(getEffected().isDead())
			return false;

		double hp = getValue() * getInterval(); // В PTS скриптах сила эффекта указывается без учета интервала.
		if(_percent)
			hp = getEffected().getMaxHp() / 100 * hp;

		if(hp > 0)
		{
			//TODO: Реализовать Хилку.
		}
		else if(hp < 0)
		{
			double damage = Math.abs(hp);

			damage = getEffector().calcStat(getSkill().isMagic() ? Stats.INFLICTS_M_DAMAGE_POWER : Stats.INFLICTS_P_DAMAGE_POWER, damage, getEffected(), getSkill());

			if(first)
			{
				if(!getSkill().isOffensive() && getSkill().isMagic() && Formulas.calcMCrit(getEffector(), getEffected(), getSkill()))
					damage *= 10.; // TODO: Сверить с оффом.
				else
					return false;
			}
			if(damage > getEffected().getCurrentHp() - 1 && !getEffected().isNpc())
			{
				// TODO: Проверить на оффе, что происходит, когда остается 1 HP.
				if(!getSkill().isOffensive())
					getEffected().sendPacket(SystemMsg.NOT_ENOUGH_HP);
				return false;
			}

			if(getSkill().getAbsorbPart() > 0)
				getEffector().setCurrentHp(getSkill().getAbsorbPart() * Math.min(getEffected().getCurrentHp(), damage) + getEffector().getCurrentHp(), false);

			boolean awake = !getEffected().isNpc() && getEffected() != getEffector(); // TODO: Check this.
			boolean standUp = getEffected() != getEffector(); // TODO: Check this.
			boolean directHp = getEffector().isNpc() || getEffected() == getEffector(); // TODO: Check this.
			getEffected().reduceCurrentHp(damage, getEffector(), getSkill(), awake, standUp, directHp, false, false, true, false);
			getEffected().broadcastPacket(getEffected().makeStatusUpdate(getEffector(), StatusUpdatePacket.CUR_HP));
		}
		return true;
	}
}