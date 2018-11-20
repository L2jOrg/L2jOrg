package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.items.ItemInstance;

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
		writeInt(_crystalItemId);//unk??
		writeInt(_attributes);
		writeInt(_itemObjId);//unk??
	}
}