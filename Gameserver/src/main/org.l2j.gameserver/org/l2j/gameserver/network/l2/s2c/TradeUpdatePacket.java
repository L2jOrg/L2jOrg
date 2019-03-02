package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class TradeUpdatePacket extends L2GameServerPacket
{
	private final int sendType;
	private ItemInfo _item;
	private long _amount;

	public TradeUpdatePacket(int sendType, ItemInfo item, long amount) {
		this.sendType = sendType;
		_item = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) sendType);
		buffer.putInt(0x01);
		if(sendType == 2) {
			buffer.putInt(0x01);
			buffer.putShort((short) (_amount > 0 && _item.getItem().isStackable() ? 3 : 2));
			writeItemInfo(buffer, _item, _amount);
		}
	}
}