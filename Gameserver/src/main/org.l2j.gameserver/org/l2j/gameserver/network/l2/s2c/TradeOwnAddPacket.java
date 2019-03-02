package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class TradeOwnAddPacket extends L2GameServerPacket
{
	private final int sendType;
	private ItemInfo _item;
	private long _amount;

	public TradeOwnAddPacket(int sendType, ItemInfo item, long amount) {
		this.sendType = sendType;
		_item = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(0x01);
		}
		buffer.putInt(0x01);
		writeItemInfo(buffer, _item, _amount);
	}
}