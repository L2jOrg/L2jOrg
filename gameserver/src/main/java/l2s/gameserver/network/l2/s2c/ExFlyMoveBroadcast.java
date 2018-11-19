package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

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
		writeD(_objId);

		writeD(0x01); //TODO: [Bonux] Maybe TYPE
		writeD(0x00); //TODO: [Bonux]

		writeD(_loc.getX());
		writeD(_loc.getY());
		writeD(_loc.getZ());

		writeD(0x00); //TODO: [Bonux]

		writeD(_destLoc.getX());
		writeD(_destLoc.getY());
		writeD(_destLoc.getZ());
	}
}
