package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_boatObjId);
		buffer.putInt(_moveSpeed);
		buffer.putInt(_rotationSpeed);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
	}
}