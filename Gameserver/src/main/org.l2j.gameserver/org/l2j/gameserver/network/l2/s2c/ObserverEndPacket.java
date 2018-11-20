package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

public class ObserverEndPacket extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverEndPacket(Location loc)
	{
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
	}
}