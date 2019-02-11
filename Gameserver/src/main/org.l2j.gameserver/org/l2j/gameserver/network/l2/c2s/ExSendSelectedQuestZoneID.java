package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExSendSelectedQuestZoneID extends L2GameClientPacket
{
	private int _questZoneId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_questZoneId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setQuestZoneId(_questZoneId);

		if(activeChar.isGM())
			activeChar.sendMessage("Current quest zone ID: " + _questZoneId);
	}
}