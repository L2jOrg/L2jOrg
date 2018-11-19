package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectHPDrain extends Effect
{
	private final boolean _percent;

	public EffectHPDrain(final Abnormal abnormal, final Env env, final EffectTemplate template)
	{
		super(abnormal, env, template);
		_percent = template.getParam().getBool("percent", false);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isDead())
			return;

		if(getEffected() == getEffector())
			return;

		double drained = getValue();
		if(_percent)
			drained = getEffected().getMaxHp() / 100. * drained;

		drained = Math.min(drained, getEffected().getCurrentHp());
		if(drained <= 0)
			return;

		getEffected().setCurrentHp(Math.max(0., getEffected().getCurrentHp() - drained), false);
		getEffected().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DRAINED_YOU_OF_S2_HP).addName(getEffector()).addInteger(Math.round(drained)));

		double newHp = getEffector().getCurrentHp() + drained;
		newHp = Math.max(0, Math.min(newHp, getEffector().getMaxHp() / 100. * getEffector().calcStat(Stats.HP_LIMIT, null, null)));

		double addToHp = newHp - getEffected().getCurrentHp();
		if(addToHp > 0)
		{
			getEffector().setCurrentHp(newHp, false);
			//TODO: Нужно ли какое-то сообщение для эффектора?
		}
	}
}