package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;

public class EffectThrowUp extends EffectFlyAbstract
{
	private int _x, _y, _z;

	public EffectThrowUp(Abnormal abnormal, Env env, EffectTemplate template)
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

		getEffected().getFlags().getStunned().start(this);
		getEffected().broadcastPacket(new FlyToLocationPacket(getEffected(), new Location(_x, _y, _z), FlyToLocationPacket.FlyType.THROW_UP, getFlySpeed(), getFlyDelay(), getFlyAnimationSpeed()));
		getEffected().setXYZ(_x, _y, _z);
		getEffected().validateLocation(1);
	}

	@Override
	public void onExit()
	{
		getEffected().getFlags().getStunned().stop(this);
	}
}