package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class GetOnVehiclePacket extends L2GameServerPacket
{
	private int _playerObjectId, _boatObjectId;
	private Location _loc;

	public GetOnVehiclePacket(Player activeChar, Boat boat, Location loc)
	{
		_loc = loc;
		_playerObjectId = activeChar.getObjectId();
		_boatObjectId = boat.getBoatId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId);
		buffer.putInt(_boatObjectId);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
	}
}