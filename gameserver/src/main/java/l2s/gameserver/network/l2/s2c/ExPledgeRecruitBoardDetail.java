package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.clansearch.ClanSearchClan;

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
		writeD(_clan.getClanId());
		writeD(_clan.getSearchType().ordinal());
		writeS("");
		writeS(_clan.getDesc());
		writeD(_clan.getApplication());
		writeD(_clan.getSubUnit());
	}
}