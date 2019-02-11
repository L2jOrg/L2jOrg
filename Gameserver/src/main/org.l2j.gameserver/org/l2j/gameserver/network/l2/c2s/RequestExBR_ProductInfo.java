package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExBR_ProductInfoPacket;

import java.nio.ByteBuffer;

public class RequestExBR_ProductInfo extends L2GameClientPacket
{
	private int _productId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_productId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExBR_ProductInfoPacket(activeChar, _productId));
	}
}