package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExBR_ProductInfoPacket;

public class RequestExBR_ProductInfo extends L2GameClientPacket
{
	private int _productId;

	@Override
	protected void readImpl()
	{
		_productId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExBR_ProductInfoPacket(activeChar, _productId));
	}
}