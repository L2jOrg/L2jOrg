package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExFieldEventEffect extends L2GameServerPacket
{
	private final int _unk;

	public ExFieldEventEffect(int unk)
	{
		_unk = unk;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_unk);
	}
}