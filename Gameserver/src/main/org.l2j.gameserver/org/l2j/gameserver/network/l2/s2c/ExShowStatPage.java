package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public final class ExShowStatPage extends L2GameServerPacket
{
	private final int _page;

	public ExShowStatPage(int page)
	{
		_page = page;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_page);
	}
}