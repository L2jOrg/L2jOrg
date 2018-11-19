package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchPlayer;
import l2s.gameserver.network.l2.s2c.ExPledgeWaitingListApplied;

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