package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class VehicleInfoPacket extends L2GameServerPacket
{
	private int _boatObjectId;
	private Location _loc;

	public VehicleInfoPacket(Boat boat)
	{
		_boatObjectId = boat.getBoatId();
		_loc = boat.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_boatObjectId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}