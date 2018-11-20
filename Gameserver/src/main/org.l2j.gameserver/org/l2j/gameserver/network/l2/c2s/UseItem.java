package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.ItemFunctions;

public class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setActive();

		activeChar.getInventory().writeLock();
		try
		{
			ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
			if(item == null)
			{
				activeChar.sendActionFailed();
				return;
			}

			if(_ctrlPressed)
			{
				if(item.isWeapon() || item.isArmor() || item.isAccessory())
				{
					boolean hasRestrictions = false;

					StringBuilder sb = new StringBuilder();
					sb.append("<font color=LEVEL>Ограничения:</font>").append("<br1>");
					if((item.getCustomFlags() & ItemInstance.FLAG_NO_DROP) == ItemInstance.FLAG_NO_DROP)
					{
						sb.append("Нельзя выбросить").append("<br1>");
						hasRestrictions = true;
					}
					if((item.getCustomFlags() & ItemInstance.FLAG_NO_TRADE) == ItemInstance.FLAG_NO_TRADE)
					{
						sb.append("Нельзя продать/обменять").append("<br1>");
						hasRestrictions = true;
					}
					if((item.getCustomFlags() & ItemInstance.FLAG_NO_TRANSFER) == ItemInstance.FLAG_NO_TRANSFER)
					{
						sb.append("Нельзя положить на склад").append("<br1>");
						hasRestrictions = true;
					}
					if((item.getCustomFlags() & ItemInstance.FLAG_NO_CRYSTALLIZE) == ItemInstance.FLAG_NO_CRYSTALLIZE)
					{
						sb.append("Нельзя кристализовать").append("<br1>");
						hasRestrictions = true;
					}

					if(hasRestrictions)
					{
						HtmlUtils.sendHtm(activeChar, sb.toString());
						return;
					}
				}
			}

			ItemFunctions.useItem(activeChar, item, _ctrlPressed, true);
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}
	}
}