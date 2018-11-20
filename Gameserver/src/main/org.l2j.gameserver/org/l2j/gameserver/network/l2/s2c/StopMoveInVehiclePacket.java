package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeInt(_playerObjectId);
		writeInt(_boatObjectId);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeInt(_heading);
	}
}