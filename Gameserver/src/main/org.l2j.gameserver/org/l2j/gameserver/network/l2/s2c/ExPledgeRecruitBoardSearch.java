package org.l2j.gameserver.network.l2.s2c;

import java.util.List;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchClan;
import org.l2j.gameserver.model.clansearch.ClanSearchParams;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.tables.ClanTable;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeRecruitBoardSearch extends L2GameServerPacket
{
	private static final int PAGINATION_LIMIT = 12;

	private final ClanSearchParams _params;
	private final List<ClanSearchClan> _clans;

	public ExPledgeRecruitBoardSearch(ClanSearchParams params)
	{
		_params = params;
		_clans = ClanSearchManager.getInstance().listClans(PAGINATION_LIMIT, params);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_params.getCurrentPage());
		writeD(ClanSearchManager.getInstance().getPageCount(PAGINATION_LIMIT));

		writeD(_clans.size());

		for(ClanSearchClan clanHolder : _clans)
		{
			writeD(clanHolder.getClanId());
			writeD(0);
		}

		for(ClanSearchClan clanHolder : _clans)
		{
			Clan clan = ClanTable.getInstance().getClan(clanHolder.getClanId());

			writeD(clan.getCrestId());
			writeD(clan.getAlliance() == null ? 0 : clan.getAlliance().getAllyCrestId());

			writeS(clan.getName());
			writeS(clan.getLeaderName());

			writeD(clan.getLevel());
			writeD(clan.getAllSize());
			writeD(clanHolder.getSearchType().ordinal());

			writeS("");

			writeD(clanHolder.getApplication());
			writeD(clanHolder.getSubUnit());
		}
	}
}