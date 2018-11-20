package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Shuttle;
import org.l2j.gameserver.utils.Location;

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
		writeInt(_playableObjectId); // Player ObjID
		writeInt(_shuttleId); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		writeInt(_destination.x); // Destination X in shuttle
		writeInt(_destination.y); // Destination Y in shuttle
		writeInt(_destination.z); // Destination Z in shuttle
		writeInt(_origin.x); // X in shuttle
		writeInt(_origin.y); // Y in shuttle
		writeInt(_origin.z); // Z in shuttle
	}
}