package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.HtmlMessage;

/**
 * @author Bonux
**/
public class ExPCCafeRequestOpenWindowWithoutNPC extends L2GameClientPacket
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

		HtmlMessage html = new HtmlMessage(5);
		html.setFile("pc_bang_shop.htm");
		activeChar.sendPacket(html);
	}
}