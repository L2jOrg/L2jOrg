package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId);//Player ObjID
		buffer.putInt(_shuttleId);//Shuttle ID (Arkan: 1,2)
		buffer.putInt(_loc.x); // X in shuttle
		buffer.putInt(_loc.y); // Y in shuttle
		buffer.putInt(_loc.z); // Z in shuttle
		buffer.putInt(_playerHeading); // H in shuttle
	}
}