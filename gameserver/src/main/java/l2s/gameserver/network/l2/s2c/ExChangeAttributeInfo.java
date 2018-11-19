package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @author Bonux
 */
public class ExChangeAttributeInfo extends L2GameServerPacket
{
	private int _crystalItemId;
	private int _attributes;
	private int _itemObjId;

	public ExChangeAttributeInfo(int crystalItemId, ItemInstance item)
	{
		_crystalItemId = crystalItemId;
		_attributes = 0;
		for(Element e : Element.VALUES)
		{
			if(e == item.getAttackElement())
				continue;
			_attributes |= e.getMask();
		}
	}

	protected void writeImpl()
	{
		writeD(_crystalItemId);//unk??
		writeD(_attributes);
		writeD(_itemObjId);//unk??
	}
}