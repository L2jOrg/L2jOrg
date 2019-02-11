package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
	// format: cS
	private String _name;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer, 16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setManufactureName(_name);
		activeChar.storePrivateStore();
		activeChar.broadcastPrivateStoreInfo();
	}
}