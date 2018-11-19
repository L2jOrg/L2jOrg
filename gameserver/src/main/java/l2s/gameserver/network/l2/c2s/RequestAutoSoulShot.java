package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.SoulShotType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;

public class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private int _action;
	private SoulShotType _type;

	@Override
	protected void readImpl()
	{
		_itemId = readD();
		_action = readD();
		_type = SoulShotType.VALUES[readD()];
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || activeChar.isDead())
			return;

		if(Config.EX_USE_AUTO_SOUL_SHOT)
			sendPacket(new ExAutoSoulShot(_itemId, _action, _type));

		activeChar.getInventory().writeLock();

		try
		{
			ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);
			if(item == null)
				return;

			IItemHandler handler = item.getTemplate().getHandler();
			if(handler == null || !handler.isAutoUse())
				return;

			if(_action == 1 || _action == 3)
			{
				if(!activeChar.isAutoShot(_itemId))
				{
					if(activeChar.manuallyAddAutoShot(_itemId, _type, _action == 3))
						activeChar.useItem(item, false, false);
				}
			}
			else if(activeChar.isAutoShot(_itemId))
				activeChar.manuallyRemoveAutoShot(_itemId, _type, _action == 2);
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}
	}
}