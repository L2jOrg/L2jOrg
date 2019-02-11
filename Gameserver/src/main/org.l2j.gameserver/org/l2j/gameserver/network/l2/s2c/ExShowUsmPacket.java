package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExShowUsmPacket extends L2GameServerPacket
{
	private int _usmVideoId;

	public ExShowUsmPacket(int usmVideoId)
	{
		_usmVideoId = usmVideoId;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_usmVideoId);
	}
}