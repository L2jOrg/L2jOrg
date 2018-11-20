package org.l2j.gameserver.model;

import java.util.Iterator;

import org.l2j.commons.collections.EmptyIterator;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;

/**
 * @author VISTALL
 * @date 14:03/22.06.2011
 */
public interface PlayerGroup extends Iterable<Player>
{
	public static final PlayerGroup EMPTY = new PlayerGroup()
	{
		@Override
		public void broadCast(IBroadcastPacket... packet)
		{

		}

		@Override
		public int getMemberCount()
		{
			return 0;
		}

		@Override
		public Player getGroupLeader()
		{
			return null;
		}

		@Override
		public Iterator<Player> iterator()
		{
			return EmptyIterator.getInstance();
		}
	};

	void broadCast(IBroadcastPacket... packet);

	int getMemberCount();

	Player getGroupLeader();
}