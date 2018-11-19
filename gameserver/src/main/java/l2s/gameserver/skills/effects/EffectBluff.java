package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.s2c.FinishRotatingPacket;
import l2s.gameserver.network.l2.s2c.StartRotatingPacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectBluff extends Effect
{
	public EffectBluff(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isNpc() && !getEffected().isMonster())
			return false;
		return super.checkCondition();
	}

	@Override
	public boolean onActionTime()
	{
		getEffected().broadcastPacket(new StartRotatingPacket(getEffected(), getEffected().getHeading(), 1, 65535));
		getEffected().broadcastPacket(new FinishRotatingPacket(getEffected(), getEffector().getHeading(), 65535));
		getEffected().setHeading(getEffector().getHeading());
		return true;
	}
}