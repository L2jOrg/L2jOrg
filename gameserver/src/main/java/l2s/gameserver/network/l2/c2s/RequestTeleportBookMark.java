package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestTeleportBookMark extends L2GameClientPacket
{
	private int slot;

	@Override
	protected void readImpl()
	{
		slot = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar != null)
			activeChar.getBookMarkList().tryTeleport(slot);
	}
}