package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.utils.VariationUtils;

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
		writeD(_itemObjectId);
		writeD(_itemId);
		writeD(_aug1);
		writeD(_aug2);
		writeQ(_price);
		writeD(0x01);
	}
}