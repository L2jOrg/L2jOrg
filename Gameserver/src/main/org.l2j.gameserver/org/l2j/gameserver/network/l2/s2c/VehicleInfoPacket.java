package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_boatObjectId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_loc.h);
	}
}