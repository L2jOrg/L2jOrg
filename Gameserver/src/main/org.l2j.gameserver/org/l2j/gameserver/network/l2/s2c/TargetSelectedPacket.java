package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * format   dddddd
 */
public class TargetSelectedPacket extends L2GameServerPacket
{
	private int _objectId;
	private int _targetId;
	private Location _loc;

	public TargetSelectedPacket(int objectId, int targetId, Location loc)
	{
		_objectId = objectId;
		_targetId = targetId;
		_loc = loc;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_targetId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(0x00);
	}
}