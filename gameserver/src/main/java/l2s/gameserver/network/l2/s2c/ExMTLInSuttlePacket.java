package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.boat.Shuttle;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class ExMTLInSuttlePacket extends L2GameServerPacket
{
	private int _playableObjectId, _shuttleId;
	private Location _origin, _destination;

	public ExMTLInSuttlePacket(Player player, Shuttle shuttle, Location origin, Location destination)
	{
		_playableObjectId = player.getObjectId();
		_shuttleId = shuttle.getBoatId();
		_origin = origin;
		_destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playableObjectId); // Player ObjID
		writeD(_shuttleId); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		writeD(_destination.x); // Destination X in shuttle
		writeD(_destination.y); // Destination Y in shuttle
		writeD(_destination.z); // Destination Z in shuttle
		writeD(_origin.x); // X in shuttle
		writeD(_origin.y); // Y in shuttle
		writeD(_origin.z); // Z in shuttle
	}
}