package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_targetId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(0x00); // иногда бывает 1
	}
}