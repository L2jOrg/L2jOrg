package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.entity.boat.Boat;
import l2s.gameserver.utils.Location;

public class VehicleDeparturePacket extends L2GameServerPacket
{
	private int _moveSpeed, _rotationSpeed;
	private int _boatObjId;
	private Location _loc;

	public VehicleDeparturePacket(Boat boat)
	{
		_boatObjId = boat.getBoatId();
		_moveSpeed = boat.getMoveSpeed();
		_rotationSpeed = boat.getRotationSpeed();
		_loc = boat.getDestination();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_boatObjId);
		writeD(_moveSpeed);
		writeD(_rotationSpeed);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}