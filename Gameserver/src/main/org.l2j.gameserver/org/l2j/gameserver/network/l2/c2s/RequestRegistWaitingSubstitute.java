package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

public final class RequestRegistWaitingSubstitute extends L2GameClientPacket
{
	private boolean _enable;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_enable = buffer.getInt() == 1;
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isAutoSearchParty() != _enable)
		{
			if(_enable)
			{
				if(activeChar.mayPartySearch(true, true))
					activeChar.enableAutoSearchParty();
			}
			else
				activeChar.disablePartySearch(true);

			if(_enable)
				activeChar.sendPacket(SystemMsg.YOU_ARE_REGISTERED_ON_THE_WAITING_LIST);
			else
				activeChar.sendPacket(SystemMsg.STOPPED_SEARCHING_THE_PARTY);
		}
	}
}