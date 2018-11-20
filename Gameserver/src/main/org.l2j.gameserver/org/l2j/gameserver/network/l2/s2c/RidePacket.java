package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeD(_id);
		writeD(_mountType);
		writeD(_mountType);
		writeD(_rideClassID);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}