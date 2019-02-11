package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_intensity);
		buffer.putInt(_duration);
		buffer.putInt(0x00); // Unknown
	}
}