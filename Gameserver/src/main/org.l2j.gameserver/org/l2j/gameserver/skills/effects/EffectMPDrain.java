package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class EffectMPDrain extends Effect
{
	private final boolean _percent;

	public EffectMPDrain(final Abnormal abnormal, final Env env, final EffectTemplate template)
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
			drained = getEffected().getMaxMp() / 100. * drained;

		drained = Math.min(drained, getEffected().getCurrentMp());
		if(drained <= 0)
			return;

		getEffected().setCurrentMp(Math.max(0., getEffected().getCurrentMp() - drained));
		getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2S_MP_HAS_BEEN_DRAINED_BY_C1).addInteger(Math.round(drained)).addName(getEffector()));

		double newMp = getEffector().getCurrentMp() + drained;
		newMp = Math.max(0, Math.min(newMp, getEffector().getMaxMp() / 100. * getEffector().calcStat(Stats.MP_LIMIT, null, null)));

		double addToMp = newMp - getEffected().getCurrentMp();
		if(addToMp > 0)
		{
			getEffector().setCurrentMp(newMp);
			//TODO: Нужно ли какое-то сообщение для эффектора?
		}
	}
}