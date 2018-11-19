package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.utils.Location;

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
		writeD(_objectId);
		writeD(_targetId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(0x00);
	}
}