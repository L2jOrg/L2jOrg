package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.events.impl.DuelEvent;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExDuelStart extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelStart(DuelEvent e)
	{
		_duelType = e.getDuelType();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_duelType);
	}
}