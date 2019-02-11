package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * Format: (c) S
 * S: pledge name?
 */
public class RequestPledgeExtendedInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _name;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer, 16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		if(activeChar.isGM())
			activeChar.sendMessage("RequestPledgeExtendedInfo");

		// TODO this
	}
}