package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

public class ObserverStartPacket extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverStartPacket(Location loc)
	{
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(0x00);
		writeInt(0x00);
	}
}