package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchParams;
import l2s.gameserver.model.clansearch.base.ClanSearchClanSortType;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;
import l2s.gameserver.model.clansearch.base.ClanSearchSortOrder;
import l2s.gameserver.model.clansearch.base.ClanSearchTargetType;
import l2s.gameserver.network.l2.s2c.ExPledgeRecruitBoardSearch;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitBoardSearch extends L2GameClientPacket
{
	private ClanSearchParams _params;

	@Override
	protected void readImpl()
	{
		_params = new ClanSearchParams(readD(), ClanSearchListType.getType(readD()), ClanSearchTargetType.valueOf(readD()), readS(), ClanSearchClanSortType.valueOf(readD()), ClanSearchSortOrder.valueOf(readD()), readD(), readD());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//if(!((L2GameClient)getClient()).getFloodProtectors().getClanSearch().tryPerformAction(FloodAction.CLAN_BOARD_SEARCH))
			//return;

		activeChar.sendPacket(new ExPledgeRecruitBoardSearch(_params));
	}
}