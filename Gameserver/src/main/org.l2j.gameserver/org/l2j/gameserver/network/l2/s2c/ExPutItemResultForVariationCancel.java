package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.utils.VariationUtils;

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
	protected void writeImpl()
	{
		writeInt(_itemObjectId);
		writeInt(_itemId);
		writeInt(_aug1);
		writeInt(_aug2);
		writeLong(_price);
		writeInt(0x01);
	}
}