package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.VariationUtils;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class ExPutItemResultForVariationCancel extends L2GameServerPacket
{
	private int _itemObjectId;
	private int _itemId;
	private int _aug1;
	private int _aug2;
	private long _price;

	public ExPutItemResultForVariationCancel(ItemInstance item)
	{
		_itemObjectId = item.getObjectId();
		_itemId = item.getItemId();
		_aug1 = item.getVariation1Id();
		_aug2 = item.getVariation2Id();
		_price = VariationUtils.getRemovePrice(item);
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemObjectId);
		buffer.putInt(_itemId);
		buffer.putInt(_aug1);
		buffer.putInt(_aug2);
		buffer.putLong(_price);
		buffer.putInt(0x01);
	}
}