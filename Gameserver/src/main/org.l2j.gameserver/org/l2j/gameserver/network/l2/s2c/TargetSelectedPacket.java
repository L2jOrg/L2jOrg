package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeInt(_objectId);
		writeInt(_targetId);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(0x00);
	}
}