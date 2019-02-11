package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);

		buffer.putInt(_destination.x);
		buffer.putInt(_destination.y);
		buffer.putInt(_destination.z);

		buffer.putInt(_current.x);
		buffer.putInt(_current.y);
		buffer.putInt(_current.z);
	}
}