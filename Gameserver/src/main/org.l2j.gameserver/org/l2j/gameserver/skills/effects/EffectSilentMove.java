package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public final class EffectSilentMove extends Effect
{
	public EffectSilentMove(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		if(getEffected().isPlayable())
			((Playable) getEffected()).getFlags().getSilentMoving().start(this);
	}

	@Override
	public void onExit()
	{
		if(getEffected().isPlayable())
			((Playable) getEffected()).getFlags().getSilentMoving().stop(this);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		double manaDam = getValue();
		if(manaDam > getEffected().getCurrentMp())
		{
			if(getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMsg.NOT_ENOUGH_MP);
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
			}
			return false;
		}

		getEffected().reduceCurrentMp(manaDam, null);
		return true;
	}
}