package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchClan;
import l2s.gameserver.network.l2.s2c.ExPledgeRecruitBoardDetail;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitBoardDetail extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		ClanSearchClan clan = ClanSearchManager.getInstance().getClan(_clanId);
		if(clan == null)
			return;

		activeChar.sendPacket(new ExPledgeRecruitBoardDetail(clan));
	}
}