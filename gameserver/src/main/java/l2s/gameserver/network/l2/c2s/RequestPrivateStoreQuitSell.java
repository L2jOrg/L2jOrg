package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestPrivateStoreQuitSell extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isInStoreMode() || activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		activeChar.storePrivateStore();
		activeChar.standUp();
		activeChar.broadcastCharInfo();
	}
}