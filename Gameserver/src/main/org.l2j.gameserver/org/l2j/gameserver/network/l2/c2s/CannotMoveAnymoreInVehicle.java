package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

// format: cddddd
public class CannotMoveAnymoreInVehicle extends L2GameClientPacket
{
	private Location _loc = new Location();
	private int _boatid;

	@Override
	protected void readImpl(ByteBuffer buffer) {
		_boatid = buffer.getInt();
		_loc.x = buffer.getInt();
		_loc.y = buffer.getInt();
		_loc.z = buffer.getInt();
		_loc.h = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Boat boat = player.getBoat();
		if(boat != null && boat.getBoatId() == _boatid)
		{
			player.setInBoatPosition(_loc);
			player.setHeading(_loc.h);
			player.broadcastPacket(boat.inStopMovePacket(player));
		}
	}
}