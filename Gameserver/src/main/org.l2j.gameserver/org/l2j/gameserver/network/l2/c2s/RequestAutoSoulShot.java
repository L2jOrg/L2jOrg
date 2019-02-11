package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.items.IItemHandler;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.SoulShotType;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExAutoSoulShot;

import java.nio.ByteBuffer;

public class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private int _action;
	private SoulShotType _type;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_itemId = buffer.getInt();
		_action = buffer.getInt();
		_type = SoulShotType.VALUES[buffer.getInt()];
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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