package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

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