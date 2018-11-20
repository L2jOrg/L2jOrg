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
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}