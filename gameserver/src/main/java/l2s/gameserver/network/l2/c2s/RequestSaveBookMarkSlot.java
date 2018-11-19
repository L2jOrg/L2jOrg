package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExGetBookMarkInfoPacket;

/**
 * SdS
 */
public class RequestSaveBookMarkSlot extends L2GameClientPacket
{
	private String name, acronym;
	private int icon;

	@Override
	protected void readImpl()
	{
		name = readS(32);
		icon = readD();
		acronym = readS(4);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar != null && activeChar.getBookMarkList().add(name, acronym, icon))
			activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));
	}
}