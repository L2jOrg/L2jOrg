package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchClan;
import l2s.gameserver.model.clansearch.ClanSearchPlayer;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.tables.ClanTable;

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
		writeD(_clanId);
		writeS(_clanName);
		writeS(_leaderName);
		writeD(_clanLevel);
		writeD(_memberCount);
		writeD(_searchType.ordinal());
		writeS("");
		writeS(_desc);
	}
}