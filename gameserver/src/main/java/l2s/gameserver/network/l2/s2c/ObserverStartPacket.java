package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.utils.Location;

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
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(0x00);
		writeD(0x00);
	}
}