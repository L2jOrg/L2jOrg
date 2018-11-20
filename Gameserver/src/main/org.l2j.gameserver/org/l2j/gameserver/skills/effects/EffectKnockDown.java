package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;
import org.l2j.gameserver.utils.Location;

public class EffectKnockDown extends EffectFlyAbstract
{
	private int _x, _y, _z;

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isThrowAndKnockImmune())
		{
			getEffected().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}
		return super.checkCondition();
	}

	public EffectKnockDown(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		int curX = getEffected().getX();
		int curY = getEffected().getY();
		int curZ = getEffected().getZ();

		double dx = getEffector().getX() - curX;
		double dy = getEffector().getY() - curY;
		double dz = getEffector().getZ() - curZ;
		double distance = Math.sqrt(dx * dx + dy * dy);

		if(distance > 2000.0D)
			return;

		int offset = Math.min((int) distance + getFlyRadius(), 1400);
		offset = (int) (offset + Math.abs(dz));

		if(offset < 5)
			offset = 5;

		if(distance < 1.0D)
			return;

		double sin = dy / distance;
		double cos = dx / distance;

		_x = (getEffector().getX() - (int) (offset * cos));
		_y = (getEffector().getY() - (int) (offset * sin));
		_z = getEffected().getZ();

		if(Config.ALLOW_GEODATA)
		{
			Location destiny = GeoEngine.moveCheck(getEffected().getX(), getEffected().getY(), getEffected().getZ(), _x, _y, getEffected().getGeoIndex());
			_x = destiny.getX();
			_y = destiny.getY();
		}

		if(getEffected().getFlags().getKnockDowned().start(this))
		{
			getEffected().abortAttack(true, true);
			getEffected().abortCast(true, true);
			getEffected().stopMove();

			getEffected().getAI().notifyEvent(CtrlEvent.EVT_KNOCK_DOWN, getEffected());

			getEffected().broadcastPacket(new FlyToLocationPacket(getEffected(), new Location(_x, _y, _z), FlyToLocationPacket.FlyType.PUSH_DOWN_HORIZONTAL, getFlySpeed(), getFlyDelay(), getFlyAnimationSpeed()));
			getEffected().setXYZ(_x, _y, _z);
			getEffected().validateLocation(1);
		}
	}

	@Override
	public void onExit()
	{
		if(getEffected().getFlags().getKnockDowned().stop(this))
		{
			if(!getEffected().isPlayer())
				getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
}