package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class RequestGetOffVehicle extends L2GameClientPacket
{
	// Format: cdddd
	private int _objectId;
	private Location _location = new Location();

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_objectId = buffer.getInt();
		_location.x = buffer.getInt();
		_location.y = buffer.getInt();
		_location.z = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Boat boat = BoatHolder.getInstance().getBoat(_objectId);
		if(boat == null || boat.isMoving)
		{
			player.sendActionFailed();
			return;
		}

		boat.oustPlayer(player, _location, false);
	}
}