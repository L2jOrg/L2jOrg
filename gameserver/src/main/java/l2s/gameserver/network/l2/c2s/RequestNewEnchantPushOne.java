package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.SynthesisDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantOneFail;
import l2s.gameserver.network.l2.s2c.ExEnchantOneOK;
import l2s.gameserver.templates.item.support.SynthesisData;

/**
 * @author Bonux
**/
public class RequestNewEnchantPushOne extends L2GameClientPacket
{
	private int _item1ObjectId;

	@Override
	protected void readImpl()
	{
		_item1ObjectId = readD();
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
			activeChar.sendPacket(ExEnchantOneFail.STATIC);
			return;
		}

		final ItemInstance item2 = activeChar.getSynthesisItem2();
		if(item1 == item2)
		{
			activeChar.sendPacket(ExEnchantOneFail.STATIC);
			return;
		}

		SynthesisData data = null;
		for(SynthesisData d : SynthesisDataHolder.getInstance().getDatas())
		{
			if(item2 == null || item2.getItemId() == d.getItem1Id())
			{
				if(item1.getItemId() == d.getItem2Id())
				{
					data = d;
					break;
				}
			}

			if(item2 == null || item2.getItemId() == d.getItem2Id())
			{
				if(item1.getItemId() == d.getItem1Id())
				{
					data = d;
					break;
				}
			}
		}

		if(data == null)
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_VALID_COMBINATION);
			activeChar.sendPacket(ExEnchantOneFail.STATIC);
			return;
		}

		activeChar.setSynthesisItem1(item1);
		activeChar.sendPacket(ExEnchantOneOK.STATIC);
	}
}