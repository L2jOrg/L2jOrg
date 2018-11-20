package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.s2c.ExRotation;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.PositionUtils;

public class EffectShadowStep extends EffectFlyAbstract
{
	private int _x, _y, _z;

	public EffectShadowStep(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		int px = getEffected().getX();
		int py = getEffected().getY();

		double ph = PositionUtils.convertHeadingToDegree(getEffected().getHeading()) + 180;
		if(ph > 360)
			ph -= 360;

		ph = (Math.PI * ph) / 180;
		
		_x = (int) (px + (30 * Math.cos(ph)));
		_y = (int) (py + (30 * Math.sin(ph)));
		_z = getEffected().getZ();

		if(Config.ALLOW_GEODATA)
		{
			Location destiny = GeoEngine.moveCheck(getEffector().getX(), getEffector().getY(), getEffector().getZ(), _x, _y, getEffector().getGeoIndex());
			_x = destiny.getX();
			_y = destiny.getY();
		}

		getEffector().abortAttack(true, true);
		getEffector().abortCast(true, true);
		getEffector().stopMove();

		getEffector().broadcastPacket(new FlyToLocationPacket(getEffector(), new Location(_x, _y, _z), FlyToLocationPacket.FlyType.DUMMY, getFlySpeed(), getFlyDelay(), getFlyAnimationSpeed()));
		getEffector().setXYZ(_x, _y, _z);
		getEffector().validateLocation(1);
		getEffector().setHeading(PositionUtils.calculateHeadingFrom(getEffector(), getEffected()));
		getEffector().broadcastPacket(new ExRotation(getEffector().getObjectId(), getEffector().getHeading()));
	}
}