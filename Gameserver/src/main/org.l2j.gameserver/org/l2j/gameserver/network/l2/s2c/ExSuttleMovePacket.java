package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.boat.Shuttle;

/**
 * @author Bonux
**/
public class ExSuttleMovePacket extends L2GameServerPacket
{
	private final Shuttle _shuttle;

	public ExSuttleMovePacket(Shuttle shuttle)
	{
		_shuttle = shuttle;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_shuttle.getBoatId()); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		writeInt(_shuttle.getMoveSpeed()); // Speed
		writeInt(0x00); //unk: 0 (0x00000000)
		writeInt(_shuttle.getDestination().x); // Destination X
		writeInt(_shuttle.getDestination().y); // Destination Y
		writeInt(_shuttle.getDestination().z); // Destination Z
	}
}