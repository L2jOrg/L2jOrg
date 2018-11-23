package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.l2.s2c.ExDeletePartySubstitute;

public class RequestDeletePartySubstitute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		//_objectId = readInt();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
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
