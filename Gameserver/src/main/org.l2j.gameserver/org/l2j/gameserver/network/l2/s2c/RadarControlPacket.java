package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * Примеры пакетов:
 *
 * Ставит флажок на карте и показывает стрелку на компасе:
 * EB 00 00 00 00 01 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 * Убирает флажок и стрелку
 * EB 02 00 00 00 02 00 00 00 40 2B FF FF 8C 3C 02 00 A0 F6 FF FF
 */
public class RadarControlPacket extends L2GameServerPacket
{
	private int _x, _y, _z, _type, _showRadar;

	public RadarControlPacket(int showRadar, int type, Location loc)
	{
		this(showRadar, type, loc.x, loc.y, loc.z);
	}

	public RadarControlPacket(int showRadar, int type, int x, int y, int z)
	{
		_showRadar = showRadar; // showRadar?? 0 = showRadar; 1 = delete radar;
		_type = type; // 1 - только стрелка над головой, 2 - флажок на карте
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_showRadar);
		buffer.putInt(_type); //maybe type
		buffer.putInt(_x); //x
		buffer.putInt(_y); //y
		buffer.putInt(_z); //z
	}
}