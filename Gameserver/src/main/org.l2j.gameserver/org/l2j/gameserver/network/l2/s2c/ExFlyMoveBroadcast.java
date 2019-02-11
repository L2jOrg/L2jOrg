package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class ExFlyMoveBroadcast extends L2GameServerPacket
{
	private int _objId;
	private Location _loc;
	private Location _destLoc;

	public ExFlyMoveBroadcast(Player player, Location destLoc)
	{
		_objId = player.getObjectId();
		_loc = player.getLoc();
		_destLoc = destLoc;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);

		buffer.putInt(0x01); //TODO: [Bonux] Maybe TYPE
		buffer.putInt(0x00); //TODO: [Bonux]

		buffer.putInt(_loc.getX());
		buffer.putInt(_loc.getY());
		buffer.putInt(_loc.getZ());

		buffer.putInt(0x00); //TODO: [Bonux]

		buffer.putInt(_destLoc.getX());
		buffer.putInt(_destLoc.getY());
		buffer.putInt(_destLoc.getZ());
	}
}
