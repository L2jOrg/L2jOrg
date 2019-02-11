package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.boat.Shuttle;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_shuttle.getBoatId()); // Shuttle ID (Arkan: 1,2; Cruma: 3)
		buffer.putInt(_shuttle.getMoveSpeed()); // Speed
		buffer.putInt(0x00); //unk: 0 (0x00000000)
		buffer.putInt(_shuttle.getDestination().x); // Destination X
		buffer.putInt(_shuttle.getDestination().y); // Destination Y
		buffer.putInt(_shuttle.getDestination().z); // Destination Z
	}
}