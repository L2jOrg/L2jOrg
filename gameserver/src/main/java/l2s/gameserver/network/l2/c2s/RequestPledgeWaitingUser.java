package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchPlayer;
import l2s.gameserver.network.l2.s2c.ExPledgeWaitingList;
import l2s.gameserver.network.l2.s2c.ExPledgeWaitingUser;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeWaitingUser extends L2GameClientPacket
{
	private int _clanId;
	private int _charId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
		_charId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		ClanSearchPlayer csPlayer = ClanSearchManager.getInstance().getApplicant(_clanId, _charId);
		if(csPlayer == null)
			activeChar.sendPacket(new ExPledgeWaitingList(_clanId));
		else
			activeChar.sendPacket(new ExPledgeWaitingUser(_charId, csPlayer.getDesc()));
	}
}