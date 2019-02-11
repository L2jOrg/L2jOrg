package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class SnoopQuit extends L2GameClientPacket
{
	private int _snoopID;

	/**
	 * format: cd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_snoopID = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Player player = (Player) GameObjectsStorage.findObject(_snoopID);
		if(player == null)
			return;

		player.removeSnooper(activeChar);
	}
}