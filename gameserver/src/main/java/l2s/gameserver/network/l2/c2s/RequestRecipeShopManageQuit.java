package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestRecipeShopManageQuit extends L2GameClientPacket
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

		if(!activeChar.isInStoreMode() || activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE)
		{
			activeChar.sendActionFailed();
			return;
		}

		/*TODO[Ertheia]: Fix this.
		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		activeChar.standUp();
		activeChar.broadcastCharInfo();*/
	}
}