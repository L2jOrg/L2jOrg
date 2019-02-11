package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author : Ragnarok & Bonux
 * @date : 22.04.12  12:09
 */
public class ExResponseCommissionBuyItem extends L2GameServerPacket
{
	public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem();

	private int _code;
	private int _itemId;
	private long _count;

	public ExResponseCommissionBuyItem()
	{
		_code = 0;
	}

	public ExResponseCommissionBuyItem(int itemId, long count)
	{
		_code = 1;
		_itemId = itemId;
		_count = count;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_code);
		if(_code == 0)
			return;

		buffer.putInt(0x00); //unk, maybe item object Id
		buffer.putInt(_itemId);
		buffer.putLong(_count);
	}
}
