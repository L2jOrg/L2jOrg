package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public abstract class EffectFlyAbstract extends Effect
{
	private final FlyType _flyType;
	private final double _flyCourse;
	private final int _flySpeed;
	private final int _flyDelay;
	private final int _flyAnimationSpeed;
	private final int _flyRadius;

	public EffectFlyAbstract(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_flyType = template.getParam().getEnum("fly_type", FlyType.class, getSkill().getFlyType());
		_flyCourse = template.getParam().getDouble("fly_course", 0D);
		_flySpeed = template.getParam().getInteger("fly_speed", getSkill().getFlySpeed());
		_flyDelay = template.getParam().getInteger("fly_delay", getSkill().getFlyDelay());
		_flyAnimationSpeed = template.getParam().getInteger("fly_animation_speed", getSkill().getFlyAnimationSpeed());
		_flyRadius = template.getParam().getInteger("fly_radius", getSkill().getFlyRadius());
	}

	public FlyType getFlyType()
	{
		return _flyType;
	}

	public double getFlyCourse()
	{
		return _flyCourse;
	}

	public int getFlySpeed()
	{
		return _flySpeed;
	}

	public int getFlyDelay()
	{
		return _flyDelay;
	}

	public int getFlyAnimationSpeed()
	{
		return _flyAnimationSpeed;
	}

	public int getFlyRadius()
	{
		return _flyRadius;
	}
}