package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ShowXMasSeal extends L2GameServerPacket
{
	private int _item;

	public ShowXMasSeal(int item)
	{
		_item = item;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_item);
	}
}