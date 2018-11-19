package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
	// format: cS
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setManufactureName(_name);
		activeChar.storePrivateStore();
		activeChar.broadcastPrivateStoreInfo();
	}
}