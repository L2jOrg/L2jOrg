package org.l2j.gameserver.network.l2.s2c;

import java.util.List;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.model.clansearch.ClanSearchWaiterParams;
import org.l2j.gameserver.model.clansearch.base.ClanSearchListType;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeDraftListSearch extends L2GameServerPacket
{
	private final List<ClanSearchPlayer> _waiters;

	public ExPledgeDraftListSearch(ClanSearchWaiterParams params)
	{
		_waiters = ClanSearchManager.getInstance().listWaiters(params);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_waiters.size());
		for(ClanSearchPlayer waiter : _waiters)
		{
			writeD(waiter.getCharId());
			writeS(waiter.getName());
			writeD(waiter.getSearchType().ordinal());
			writeD(waiter.getClassId());
			writeD(waiter.getLevel());
		}
	}
}