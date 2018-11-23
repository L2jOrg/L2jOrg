package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchParams;
import org.l2j.gameserver.model.clansearch.base.ClanSearchClanSortType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchListType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchSortOrder;
import org.l2j.gameserver.model.clansearch.base.ClanSearchTargetType;
import org.l2j.gameserver.network.l2.s2c.ExPledgeRecruitBoardSearch;

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
		_params = new ClanSearchParams(readInt(), ClanSearchListType.getType(readInt()), ClanSearchTargetType.valueOf(readInt()), readString(), ClanSearchClanSortType.valueOf(readInt()), ClanSearchSortOrder.valueOf(readInt()), readInt(), readInt());
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