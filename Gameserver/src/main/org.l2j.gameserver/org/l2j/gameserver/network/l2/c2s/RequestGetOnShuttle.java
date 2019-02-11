package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class RequestGetOnShuttle extends L2GameClientPacket
{
	private int _shuttleId;
	private Location _loc = new Location();

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_shuttleId = buffer.getInt();
		_loc.x = buffer.getInt();
		_loc.y = buffer.getInt();
		_loc.z = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Boat boat = BoatHolder.getInstance().getBoat(_shuttleId);
		if(boat == null)
			return;

		boat.addPlayer(player, _loc);
	}
}