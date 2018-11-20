package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.utils.Location;

/**
 * format  ddddd
 */
public class TargetUnselectedPacket extends L2GameServerPacket
{
	private int _targetId;
	private Location _loc;

	public TargetUnselectedPacket(GameObject obj)
	{
		_targetId = obj.getObjectId();
		_loc = obj.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_targetId);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(0x00); // иногда бывает 1
	}
}