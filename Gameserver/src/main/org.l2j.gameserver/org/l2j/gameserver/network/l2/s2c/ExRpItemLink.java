package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * ddQhdhhhhhdhhhhhhhh - Gracia Final
 */
public class ExRpItemLink extends L2GameServerPacket
{
	private ItemInfo _item;

	public ExRpItemLink(ItemInfo item)
	{
		_item = item;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeItemInfo(buffer, _item);
	}
}