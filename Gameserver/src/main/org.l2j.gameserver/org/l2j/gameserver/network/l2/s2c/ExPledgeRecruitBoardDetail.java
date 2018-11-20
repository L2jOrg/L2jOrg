package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.clansearch.ClanSearchClan;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeRecruitBoardDetail extends L2GameServerPacket
{
	private final ClanSearchClan _clan;

	public ExPledgeRecruitBoardDetail(ClanSearchClan clan)
	{
		_clan = clan;
	}

	protected void writeImpl()
	{
		writeInt(_clan.getClanId());
		writeInt(_clan.getSearchType().ordinal());
		writeString("");
		writeString(_clan.getDesc());
		writeInt(_clan.getApplication());
		writeInt(_clan.getSubUnit());
	}
}