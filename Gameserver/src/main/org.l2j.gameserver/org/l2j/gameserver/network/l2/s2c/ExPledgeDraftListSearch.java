package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.model.clansearch.ClanSearchWaiterParams;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_waiters.size());
		for(ClanSearchPlayer waiter : _waiters)
		{
			buffer.putInt(waiter.getCharId());
			writeString(waiter.getName(), buffer);
			buffer.putInt(waiter.getSearchType().ordinal());
			buffer.putInt(waiter.getClassId());
			buffer.putInt(waiter.getLevel());
		}
	}
}