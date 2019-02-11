package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExResponseCommissionInfo;

import java.nio.ByteBuffer;

public class RequestCommissionInfo extends L2GameClientPacket
{
	public int _itemObjId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_itemObjId = buffer.getInt();

	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new ExResponseCommissionInfo(item));
	}
}
