package l2s.gameserver.model.actor.instances.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.gameserver.dao.CharacterPremiumItemsDAO;
import l2s.gameserver.model.Player;

/**
 * @author Bonux
 **/
public class PremiumItemList
{
	private static class PremiumItemComparator implements Comparator<PremiumItem>
	{
		private static final PremiumItemComparator _instance = new PremiumItemComparator();

		private static PremiumItemComparator getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(PremiumItem o1, PremiumItem o2)
		{
			if(o2.getReceiveTime() == o1.getReceiveTime())
				return o1.getItemId() - o2.getItemId();
			return o2.getReceiveTime() - o1.getReceiveTime();
		}
	}

	public static final int MAX_ITEMS_SIZE = Integer.MAX_VALUE;

	private List<PremiumItem> _premiumItemList = new ArrayList<PremiumItem>(0);
	private final Player _owner;

	/** Блокировка для чтения/записи вещей из списка и внешних операций */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	public PremiumItemList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		_premiumItemList = CharacterPremiumItemsDAO.getInstance().select(_owner);
	}

	public boolean add(PremiumItem item)
	{
		writeLock();
		try
		{
			PremiumItem same = getSame(item);
			if(same != null)
			{
				long newCount = same.getItemCount() + item.getItemCount();
				if(!CharacterPremiumItemsDAO.getInstance().update(_owner, item, newCount))
					return false;

				same.setItemCount(newCount);
			}
			else
			{
				if(!CharacterPremiumItemsDAO.getInstance().insert(_owner, item))
					return false;

				_premiumItemList.add(item);

				Collections.sort(_premiumItemList, PremiumItemComparator.getInstance());
			}
		}
		finally
		{
			writeUnlock();
		}
		return true;
	}

	public PremiumItem get(int index)
	{
		readLock();
		try
		{
			index = Math.min(_premiumItemList.size() - 1, index);
			return _premiumItemList.get(index);
		}
		finally
		{
			readUnlock();
		}
	}

	public PremiumItem getSame(PremiumItem item)
	{
		readLock();
		try
		{
			for(PremiumItem same : _premiumItemList)
			{
				if(same.getReceiveTime() != item.getReceiveTime())
					continue;

				if(same.getItemId() != item.getItemId())
					continue;

				if(!same.getSender().equals(item.getSender()))
					continue;

				return same;
			}
		}
		finally
		{
			readUnlock();
		}
		return null;
	}

	public boolean remove(PremiumItem item, long count)
	{
		if(count == 0)
			return false;

		writeLock();
		try
		{
			if(count != -1 && item.getItemCount() < count)
				return false;

			long newCount = item.getItemCount() - count;
			if(count != -1 && newCount > 0)
			{
				if(!CharacterPremiumItemsDAO.getInstance().update(_owner, item, newCount))
					return false;

				item.setItemCount(newCount);
			}
			else
			{
				if(!CharacterPremiumItemsDAO.getInstance().delete(_owner, item))
					return false;

				_premiumItemList.remove(item);
			}
		}
		finally
		{
			writeUnlock();
		}

		return true;
	}

	public boolean contains(PremiumItem item)
	{
		readLock();
		try
		{
			return _premiumItemList.contains(item);
		}
		finally
		{
			readUnlock();
		}
	}

	public int size()
	{
		readLock();
		try
		{
			return _premiumItemList.size();
		}
		finally
		{
			readUnlock();
		}
	}

	public PremiumItem[] values()
	{
		readLock();
		try
		{
			return _premiumItemList.toArray(new PremiumItem[_premiumItemList.size()]);
		}
		finally
		{
			readUnlock();
		}
	}

	public boolean isEmpty()
	{
		readLock();
		try
		{
			return _premiumItemList.isEmpty();
		}
		finally
		{
			readUnlock();
		}
	}

	@Override
	public String toString()
	{
		return "PremiumItemList[owner=" + _owner.getName() + "]";
	}

	public final void writeLock()
	{
		writeLock.lock();
	}

	public final void writeUnlock()
	{
		writeLock.unlock();
	}

	public final void readLock()
	{
		readLock.lock();
	}

	public final void readUnlock()
	{
		readLock.unlock();
	}
}