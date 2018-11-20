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
		writeInt(_params.getCurrentPage());
		writeInt(ClanSearchManager.getInstance().getPageCount(PAGINATION_LIMIT));

		writeInt(_clans.size());

		for(ClanSearchClan clanHolder : _clans)
		{
			writeInt(clanHolder.getClanId());
			writeInt(0);
		}

		for(ClanSearchClan clanHolder : _clans)
		{
			Clan clan = ClanTable.getInstance().getClan(clanHolder.getClanId());

			writeInt(clan.getCrestId());
			writeInt(clan.getAlliance() == null ? 0 : clan.getAlliance().getAllyCrestId());

			writeString(clan.getName());
			writeString(clan.getLeaderName());

			writeInt(clan.getLevel());
			writeInt(clan.getAllSize());
			writeInt(clanHolder.getSearchType().ordinal());

			writeString("");

			writeInt(clanHolder.getApplication());
			writeInt(clanHolder.getSubUnit());
		}
	}
}