package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class ExStopMoveInShuttlePacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId, _playerHeading;
	private Location _loc;

	public ExStopMoveInShuttlePacket(Player cha)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = cha.getBoat().getBoatId();
		_loc = cha.getInBoatPosition();
		_playerHeading = cha.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_playerObjectId);//Player ObjID
		writeInt(_shuttleId);//Shuttle ID (Arkan: 1,2)
		writeInt(_loc.x); // X in shuttle
		writeInt(_loc.y); // Y in shuttle
		writeInt(_loc.z); // Z in shuttle
		writeInt(_playerHeading); // H in shuttle
	}
}