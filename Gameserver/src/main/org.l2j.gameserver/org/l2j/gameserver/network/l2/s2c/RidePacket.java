package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class RidePacket extends L2GameServerPacket
{
	private int _mountType, _id, _rideClassID;
	private Location _loc;

	public RidePacket(Player cha)
	{
		_id = cha.getObjectId();
		_mountType = cha.getMountType().ordinal();
		_rideClassID = cha.getMountNpcId() + 1000000;
		_loc = cha.getLoc();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_id);
		buffer.putInt(_mountType);
		buffer.putInt(_mountType);
		buffer.putInt(_rideClassID);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
	}
}