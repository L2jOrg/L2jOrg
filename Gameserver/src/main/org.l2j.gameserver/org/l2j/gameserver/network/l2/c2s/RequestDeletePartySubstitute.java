package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestDeletePartySubstitute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//_objectId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		final Party party = activeChar.getParty();
		if(party == null || party.getPartyLeader() != activeChar)
			return;

		/*TODO[Bonux]: Ertheia
		final Player target = World.getPlayer(_objectId);
		if(target != null && target.getParty() == party && target.isPartySubstituteStarted())
		{
			target.stopSubstituteTask();
			activeChar.sendPacket(new ExDeletePartySubstitute(_objectId));
		}*/
	}
}
