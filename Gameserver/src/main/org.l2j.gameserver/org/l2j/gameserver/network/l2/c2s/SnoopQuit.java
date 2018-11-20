package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;

public class SnoopQuit extends L2GameClientPacket
{
	private int _snoopID;

	/**
	 * format: cd
	 */
	@Override
	protected void readImpl()
	{
		_snoopID = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Player player = (Player) GameObjectsStorage.findObject(_snoopID);
		if(player == null)
			return;

		player.removeSnooper(activeChar);
	}
}