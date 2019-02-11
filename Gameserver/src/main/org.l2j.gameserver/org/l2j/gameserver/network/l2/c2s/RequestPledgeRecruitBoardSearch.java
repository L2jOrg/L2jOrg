package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchParams;
import org.l2j.gameserver.model.clansearch.base.ClanSearchClanSortType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchListType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchSortOrder;
import org.l2j.gameserver.model.clansearch.base.ClanSearchTargetType;
import org.l2j.gameserver.network.l2.s2c.ExPledgeRecruitBoardSearch;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitBoardSearch extends L2GameClientPacket
{
	private ClanSearchParams _params;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_params = new ClanSearchParams(buffer.getInt(), ClanSearchListType.getType(buffer.getInt()), ClanSearchTargetType.valueOf(buffer.getInt()), readString(buffer), ClanSearchClanSortType.valueOf(buffer.getInt()), ClanSearchSortOrder.valueOf(buffer.getInt()), buffer.getInt(), buffer.getInt());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		//if(!((L2GameClient)client).getFloodProtectors().getClanSearch().tryPerformAction(FloodAction.CLAN_BOARD_SEARCH))
			//return;

		activeChar.sendPacket(new ExPledgeRecruitBoardSearch(_params));
	}
}