package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId);
		buffer.putInt(_boatObjectId);
		buffer.putInt(_destination.x);
		buffer.putInt(_destination.y);
		buffer.putInt(_destination.z);
		buffer.putInt(_origin.x);
		buffer.putInt(_origin.y);
		buffer.putInt(_origin.z);
	}
}