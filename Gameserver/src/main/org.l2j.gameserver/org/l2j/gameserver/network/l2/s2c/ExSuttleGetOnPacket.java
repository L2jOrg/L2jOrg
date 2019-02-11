package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.entity.boat.Shuttle;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExSuttleGetOnPacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId;
	private Location _loc;

	public ExSuttleGetOnPacket(Playable cha, Shuttle shuttle, Location loc)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = shuttle.getBoatId();
		_loc = loc;
		if(_loc == null)
			_loc = cha.getLoc();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId); // Player ObjID
		buffer.putInt(_shuttleId); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		buffer.putInt(_loc.x); // X in shuttle
		buffer.putInt(_loc.y); // Y in shuttle
		buffer.putInt(_loc.z); // Z in shuttle
	}
}