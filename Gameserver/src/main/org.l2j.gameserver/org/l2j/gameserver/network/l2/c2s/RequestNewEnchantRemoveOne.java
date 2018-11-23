package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExEnchantOneRemoveFail;
import org.l2j.gameserver.network.l2.s2c.ExEnchantOneRemoveOK;

/**
 * @author Bonux
**/
public class RequestNewEnchantRemoveOne extends L2GameClientPacket
{
	private int _item1ObjectId;

	@Override
	protected void readImpl()
	{
		_item1ObjectId = readInt();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		final ItemInstance item1 = activeChar.getInventory().getItemByObjectId(_item1ObjectId);
		if(item1 == null)
		{
			activeChar.sendPacket(ExEnchantOneRemoveFail.STATIC);
			return;
		}

		if(activeChar.getSynthesisItem1() != item1)
		{
			activeChar.sendPacket(ExEnchantOneRemoveFail.STATIC);
			return;
		}

		activeChar.setSynthesisItem1(null);
		activeChar.sendPacket(ExEnchantOneRemoveOK.STATIC);
	}
}