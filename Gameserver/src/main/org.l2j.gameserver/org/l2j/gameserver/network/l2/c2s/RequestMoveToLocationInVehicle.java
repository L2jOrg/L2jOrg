package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private Location _pos = new Location();
	private Location _originPos = new Location();
	private int _boatObjectId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_boatObjectId = buffer.getInt();
		_pos.x = buffer.getInt();
		_pos.y = buffer.getInt();
		_pos.z = buffer.getInt();
		_originPos.x = buffer.getInt();
		_originPos.y = buffer.getInt();
		_originPos.z = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;
		
		Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
		if(boat == null)
		{
			player.sendActionFailed();
			return;
		}

		boat.moveInBoat(player, _originPos, _pos);
	}
}