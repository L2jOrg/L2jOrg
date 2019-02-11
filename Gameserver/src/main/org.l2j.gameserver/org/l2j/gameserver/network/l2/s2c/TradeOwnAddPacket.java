package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class TradeOwnAddPacket extends L2GameServerPacket
{
	private ItemInfo _item;
	private long _amount;

	public TradeOwnAddPacket(ItemInfo item, long amount)
	{
		_item = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) 1); // item count
		writeItemInfo(buffer, _item, _amount);
	}
}