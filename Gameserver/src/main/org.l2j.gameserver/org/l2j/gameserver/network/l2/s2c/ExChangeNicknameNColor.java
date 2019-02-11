package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExChangeNicknameNColor extends L2GameServerPacket
{
	private int _itemObjId;

	public ExChangeNicknameNColor(int itemObjId)
	{
		_itemObjId = itemObjId;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemObjId);
	}
}