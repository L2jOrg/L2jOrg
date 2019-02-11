package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExChangeAttributeInfo;

import java.nio.ByteBuffer;

public class SendChangeAttributeTargetItem extends L2GameClientPacket
{
	public int _crystalItemId;
	public int _itemObjId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_crystalItemId = buffer.getInt(); //Change Attribute Crystall ID
		_itemObjId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
		if(item == null || !item.isWeapon())
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.sendPacket(new ExChangeAttributeInfo(_crystalItemId, item));
	}
}
