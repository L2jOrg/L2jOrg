package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExChangeAttributeInfo;

public class SendChangeAttributeTargetItem extends L2GameClientPacket
{
	public int _crystalItemId;
	public int _itemObjId;

	@Override
	protected void readImpl()
	{
		_crystalItemId = readD(); //Change Attribute Crystall ID
		_itemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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
