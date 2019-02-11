package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class TradeDonePacket extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new TradeDonePacket(1);
	public static final L2GameServerPacket FAIL = new TradeDonePacket(0);

	private int _response;

	private TradeDonePacket(int num)
	{
		_response = num;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_response);
	}
}