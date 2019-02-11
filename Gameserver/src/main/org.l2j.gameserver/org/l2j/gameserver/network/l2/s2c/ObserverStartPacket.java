package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class ObserverStartPacket extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverStartPacket(Location loc)
	{
		_loc = loc;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(0x00);
		buffer.putInt(0x00);
	}
}