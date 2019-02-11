package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExValidateLocationInShuttlePacket extends L2GameServerPacket
{
	private int _playerObjectId, _shuttleId;
	private Location _loc;

	public ExValidateLocationInShuttlePacket(Player cha)
	{
		_playerObjectId = cha.getObjectId();
		_shuttleId = cha.getBoat().getBoatId();
		_loc = cha.getInBoatPosition();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_playerObjectId); // Player ObjID
		buffer.putInt(_shuttleId); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		buffer.putInt(_loc.x); // X in shuttle
		buffer.putInt(_loc.y); // Y in shuttle
		buffer.putInt(_loc.z); // Z in shuttle
		buffer.putInt(_loc.h); // H in shuttle
	}
}