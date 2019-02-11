package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * sample: d
 */
public class ShowCalcPacket extends L2GameServerPacket
{
	private int _calculatorId;

	public ShowCalcPacket(int calculatorId)
	{
		_calculatorId = calculatorId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_calculatorId);
	}
}