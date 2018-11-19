package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.EnchantResultPacket;

public class RequestExCancelEnchantItem extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendPacket(EnchantResultPacket.CANCEL);
		}
	}
}