package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class StopMoveInVehiclePacket extends L2GameServerPacket
{
	private int _boatObjectId, _playerObjectId, _heading;
	private Location _loc;

	public StopMoveInVehiclePacket(Player player)
	{
		_boatObjectId = player.getBoat().getBoatId();
		_playerObjectId = player.getObjectId();
		_loc = player.getInBoatPosition();
		_heading = player.getHeading();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId);
		buffer.putInt(_boatObjectId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		buffer.putInt(_heading);
	}
}