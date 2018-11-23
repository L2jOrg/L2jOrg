package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExRegistPartySubstitute;
import org.l2j.gameserver.network.l2.s2c.ExRegistWaitingSubstituteOk;

public class RequestRegistPartySubstitute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readInt();
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

		final Player target = World.getPlayer(_objectId);
		if(target != null && target.getParty() == party && !target.isPartySubstituteStarted())
		{
			target.startSubstituteTask();
			/**
			 * 3523: Looking for a player who will replace the selected party member.
			**/
			activeChar.sendPacket(new ExRegistPartySubstitute(_objectId), SystemMsg.LOOKING_FOR_A_PLAYER_WHO_WILL_REPLACE_THE_SELECTED_PARTY_MEMBER);
		}
	}
}
