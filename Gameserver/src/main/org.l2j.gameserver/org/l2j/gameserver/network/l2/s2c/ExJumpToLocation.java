package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

public class ExJumpToLocation extends L2GameServerPacket
{
	private int _objectId;
	private Location _current;
	private Location _destination;

	public ExJumpToLocation(int objectId, Location from, Location to)
	{
		_objectId = objectId;
		_current = from;
		_destination = to;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_objectId);

		writeInt(_destination.x);
		writeInt(_destination.y);
		writeInt(_destination.z);

		writeInt(_current.x);
		writeInt(_current.y);
		writeInt(_current.z);
	}
}