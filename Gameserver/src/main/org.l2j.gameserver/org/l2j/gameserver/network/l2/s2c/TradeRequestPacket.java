package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class TradeRequestPacket extends L2GameServerPacket
{
	private int _senderId;

	public TradeRequestPacket(int senderId)
	{
		_senderId = senderId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_senderId);
	}
}