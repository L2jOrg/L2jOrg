package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchClan;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.model.clansearch.base.ClanSearchListType;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.tables.ClanTable;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeWaitingListApplied extends L2GameServerPacket
{
	private int _clanId = 0;
	private String _clanName = "";
	private String _leaderName = "";
	private int _clanLevel = 0;
	private int _memberCount = 0;
	private ClanSearchListType _searchType = ClanSearchListType.SLT_ANY;
	private String _desc = "";

	public ExPledgeWaitingListApplied(ClanSearchPlayer playerHolder)
	{
		if(playerHolder != null)
		{
			ClanSearchClan clanHolder = ClanSearchManager.getInstance().getClan(playerHolder.getPrefferedClanId());
			if(clanHolder != null)
			{
				Clan clan = ClanTable.getInstance().getClan(clanHolder.getClanId());
				if(clan != null)
				{
					_clanId = clanHolder.getClanId();
					_clanName = clan.getName();
					_leaderName = clan.getLeaderName();
					_clanLevel = clan.getLevel();
					_memberCount = clan.getAllSize();
					_searchType = clanHolder.getSearchType();
					_desc = clanHolder.getDesc();
				}
			}
		}
	}

	public ExPledgeWaitingListApplied(Player player)
	{
		this(ClanSearchManager.getInstance().getWaiter(player.getObjectId()));
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_clanId);
		writeString(_clanName);
		writeString(_leaderName);
		writeInt(_clanLevel);
		writeInt(_memberCount);
		writeInt(_searchType.ordinal());
		writeString("");
		writeString(_desc);
	}
}