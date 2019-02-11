package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchClan;
import org.l2j.gameserver.model.clansearch.ClanSearchParams;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_params.getCurrentPage());
		buffer.putInt(ClanSearchManager.getInstance().getPageCount(PAGINATION_LIMIT));

		buffer.putInt(_clans.size());

		for(ClanSearchClan clanHolder : _clans)
		{
			buffer.putInt(clanHolder.getClanId());
			buffer.putInt(0);
		}

		for(ClanSearchClan clanHolder : _clans)
		{
			Clan clan = ClanTable.getInstance().getClan(clanHolder.getClanId());

			buffer.putInt(clan.getCrestId());
			buffer.putInt(clan.getAlliance() == null ? 0 : clan.getAlliance().getAllyCrestId());

			writeString(clan.getName(), buffer);
			writeString(clan.getLeaderName(), buffer);

			buffer.putInt(clan.getLevel());
			buffer.putInt(clan.getAllSize());
			buffer.putInt(clanHolder.getSearchType().ordinal());

			writeString("", buffer);

			buffer.putInt(clanHolder.getApplication());
			buffer.putInt(clanHolder.getSubUnit());
		}
	}
}