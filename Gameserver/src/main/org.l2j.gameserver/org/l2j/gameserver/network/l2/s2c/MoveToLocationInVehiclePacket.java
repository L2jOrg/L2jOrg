package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

public class MoveToLocationInVehiclePacket extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _origin, _destination;

	public MoveToLocationInVehiclePacket(Player cha, Boat boat, Location origin, Location destination)
	{
		_playerObjectId = cha.getObjectId();
		_boatObjectId = boat.getBoatId();
		_origin = origin;
		_destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_playerObjectId);
		writeInt(_boatObjectId);
		writeInt(_destination.x);
		writeInt(_destination.y);
		writeInt(_destination.z);
		writeInt(_origin.x);
		writeInt(_origin.y);
		writeInt(_origin.z);
	}
}