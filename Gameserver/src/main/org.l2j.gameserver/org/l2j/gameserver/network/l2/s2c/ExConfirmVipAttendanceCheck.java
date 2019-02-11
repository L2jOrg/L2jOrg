package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExConfirmVipAttendanceCheck extends L2GameServerPacket
{
	private final boolean _success;
	private final int _receivedIndex;

	public ExConfirmVipAttendanceCheck(boolean success, int receivedIndex)
	{
		_success = success;
		_receivedIndex = receivedIndex;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_success ? 0x01 : 0x00));
		buffer.put((byte)_receivedIndex);
		buffer.putInt(0x00);
		buffer.putInt(0x00);
	}
}