package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

/**
 * format   dddddd
 */
public class EarthQuakePacket extends L2GameServerPacket
{
	private Location _loc;
	private int _intensity;
	private int _duration;

	public EarthQuakePacket(Location loc, int intensity, int duration)
	{
		_loc = loc;
		_intensity = intensity;
		_duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(_intensity);
		writeInt(_duration);
		writeInt(0x00); // Unknown
	}
}