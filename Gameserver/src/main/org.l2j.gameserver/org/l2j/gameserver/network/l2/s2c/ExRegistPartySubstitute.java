package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExRegistPartySubstitute extends L2GameServerPacket
{
	private final int _object;

	public ExRegistPartySubstitute(int obj)
	{
		_object = obj;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_object);
		buffer.putInt(0x01);
	}
}
