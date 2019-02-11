package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class Ex2NDPasswordAckPacket extends L2GameServerPacket
{
	public static final int SUCCESS = 0x00;
	public static final int WRONG_PATTERN = 0x01;

	private int _response;

	public Ex2NDPasswordAckPacket(int response)
	{
		_response = response;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)0x00);
		buffer.putInt(_response == WRONG_PATTERN ? 0x01 : 0x00);
		buffer.putInt(0x00);
	}
}