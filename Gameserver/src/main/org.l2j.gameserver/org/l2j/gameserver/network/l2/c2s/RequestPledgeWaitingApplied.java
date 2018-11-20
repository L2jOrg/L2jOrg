package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.network.l2.s2c.ExPledgeWaitingListApplied;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeWaitingApplied extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		ClanSearchPlayer csPlayer = ClanSearchManager.getInstance().findAnyApplicant(activeChar.getObjectId());
		if(csPlayer != null)
			activeChar.sendPacket(new ExPledgeWaitingListApplied(csPlayer));
	}
}