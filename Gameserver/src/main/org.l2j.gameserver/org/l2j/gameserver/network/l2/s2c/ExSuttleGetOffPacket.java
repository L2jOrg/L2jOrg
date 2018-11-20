package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.entity.boat.Shuttle;
import org.l2j.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class ExSuttleGetOffPacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId;
	private Location _loc;

	public ExSuttleGetOffPacket(Playable cha, Shuttle shuttle, Location loc)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = shuttle.getBoatId();
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_playerObjectId); // Player ObjID
		writeInt(_shuttleId); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		writeInt(_loc.x); // X in shuttle
		writeInt(_loc.y); // Y in shuttle
		writeInt(_loc.z); // Z in shuttle
	}
}