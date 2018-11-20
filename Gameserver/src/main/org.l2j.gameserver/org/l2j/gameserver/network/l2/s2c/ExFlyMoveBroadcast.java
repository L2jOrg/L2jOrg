package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

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
	protected void writeImpl()
	{
		writeInt(_objId);

		writeInt(0x01); //TODO: [Bonux] Maybe TYPE
		writeInt(0x00); //TODO: [Bonux]

		writeInt(_loc.getX());
		writeInt(_loc.getY());
		writeInt(_loc.getZ());

		writeInt(0x00); //TODO: [Bonux]

		writeInt(_destLoc.getX());
		writeInt(_destLoc.getY());
		writeInt(_destLoc.getZ());
	}
}
