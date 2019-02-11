package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPledgeWaitingList;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeWaitingList extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_clanId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExPledgeWaitingList(_clanId));
	}
}