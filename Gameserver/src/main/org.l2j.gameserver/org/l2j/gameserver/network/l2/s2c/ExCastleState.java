package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.ResidenceSide;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class ExCastleState extends L2GameServerPacket
{
	private final int _id;
	private final ResidenceSide _side;

	public ExCastleState(Castle castle)
	{
		_id = castle.getId();
		_side = castle.getResidenceSide();
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_id);
		buffer.putInt(_side.ordinal());
	}
}