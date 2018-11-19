package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private String _storename;

	@Override
	protected void readImpl()
	{
		_storename = readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setBuyStoreName(_storename);
		activeChar.storePrivateStore();
		activeChar.broadcastPrivateStoreInfo();
	}
}