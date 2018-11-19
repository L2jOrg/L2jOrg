package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class RequestUpdateBlockMemo extends L2GameClientPacket
{
	private String _name;
	private String _memo;

	@Override
	protected void readImpl()
	{
		_name = readS();
		_memo = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.getBlockList().updateMemo(_name, _memo);
	}
}