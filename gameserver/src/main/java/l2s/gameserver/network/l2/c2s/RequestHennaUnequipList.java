package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.HennaUnequipListPacket;

public class RequestHennaUnequipList extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		_symbolId = readD(); //?
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		HennaUnequipListPacket he = new HennaUnequipListPacket(activeChar);
		activeChar.sendPacket(he);
	}
}