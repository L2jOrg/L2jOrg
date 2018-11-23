package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.SynthesisDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExEnchantTwoFail;
import org.l2j.gameserver.network.l2.s2c.ExEnchantTwoOK;
import org.l2j.gameserver.templates.item.support.SynthesisData;

/**
 * @author Bonux
**/
public class RequestNewEnchantPushTwo extends L2GameClientPacket
{
	private int _item2ObjectId;

	@Override
	protected void readImpl()
	{
		_item2ObjectId = readInt();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		final ItemInstance item2 = activeChar.getInventory().getItemByObjectId(_item2ObjectId);
		if(item2 == null)
		{
			activeChar.sendPacket(ExEnchantTwoFail.STATIC);
			return;
		}

		final ItemInstance item1 = activeChar.getSynthesisItem1();
		if(item1 == item2)
		{
			activeChar.sendPacket(ExEnchantTwoFail.STATIC);
			return;
		}

		SynthesisData data = null;
		for(SynthesisData d : SynthesisDataHolder.getInstance().getDatas())
		{
			if(item1 == null || item1.getItemId() == d.getItem1Id())
			{
				if(item2.getItemId() == d.getItem2Id())
				{
					data = d;
					break;
				}
			}

			if(item1 == null || item1.getItemId() == d.getItem2Id())
			{
				if(item2.getItemId() == d.getItem1Id())
				{
					data = d;
					break;
				}
			}
		}

		if(data == null)
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_VALID_COMBINATION);
			activeChar.sendPacket(ExEnchantTwoFail.STATIC);
			return;
		}

		activeChar.setSynthesisItem2(item2);
		activeChar.sendPacket(ExEnchantTwoOK.STATIC);
	}
}