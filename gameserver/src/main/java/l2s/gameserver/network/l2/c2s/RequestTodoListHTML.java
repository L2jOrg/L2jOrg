package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestTodoListHTML extends L2GameClientPacket
{
	private int _tab;
	private String _linkName;

	@Override
	protected void readImpl()
	{
		_tab = readC();
		_linkName = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
	}
}