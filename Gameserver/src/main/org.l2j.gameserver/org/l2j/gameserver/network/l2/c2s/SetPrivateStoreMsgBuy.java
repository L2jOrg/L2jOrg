package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket
{
	private String _storename;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_storename = readString(buffer, 32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setBuyStoreName(_storename);
		activeChar.storePrivateStore();
		activeChar.broadcastPrivateStoreInfo();
	}
}