package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPutItemResultForVariationMake extends L2GameServerPacket
{
	private int _itemObjId;
	private int _unk1;
	private int _unk2;

	public ExPutItemResultForVariationMake(int itemObjId)
	{
		_itemObjId = itemObjId;
		_unk1 = 1;
		_unk2 = 1;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemObjId);
		buffer.putInt(_unk1);
		buffer.putInt(_unk2);
	}
}