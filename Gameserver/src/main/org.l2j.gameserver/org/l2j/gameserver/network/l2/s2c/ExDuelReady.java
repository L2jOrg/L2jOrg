package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.events.impl.DuelEvent;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExDuelReady extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelReady(DuelEvent event)
	{
		_duelType = event.getDuelType();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_duelType);
	}
}