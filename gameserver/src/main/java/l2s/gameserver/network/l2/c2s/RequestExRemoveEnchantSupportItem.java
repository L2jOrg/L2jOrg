package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExRemoveEnchantSupportItemResult;

/**
 * @author Bonux
**/
public class RequestExRemoveEnchantSupportItem extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(ExRemoveEnchantSupportItemResult.STATIC);
	}
}