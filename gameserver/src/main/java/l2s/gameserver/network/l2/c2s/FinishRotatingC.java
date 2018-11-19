package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.FinishRotatingPacket;

/**
 * format:		cdd
 */
public class FinishRotatingC extends L2GameClientPacket
{
	private int _degree;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl()
	{
		_degree = readD();
		_unknown = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.broadcastPacket(new FinishRotatingPacket(activeChar, _degree, 0));
	}
}