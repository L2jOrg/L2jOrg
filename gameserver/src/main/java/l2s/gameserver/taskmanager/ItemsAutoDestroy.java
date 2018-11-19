package l2s.gameserver.taskmanager;

import java.util.concurrent.ConcurrentLinkedQueue;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.items.ItemInstance;

//TODO [G1ta0] переписать
public class ItemsAutoDestroy
{
	private static ItemsAutoDestroy _instance;
	private ConcurrentLinkedQueue<ItemInstance> _items = null;
	private ConcurrentLinkedQueue<ItemInstance> _playersItems = null;
	private ConcurrentLinkedQueue<ItemInstance> _herbs = null;

	private ItemsAutoDestroy()
	{
		if(Config.AUTODESTROY_ITEM_AFTER > 0)
		{
			_items = new ConcurrentLinkedQueue<ItemInstance>();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckItemsForDestroy(), 60000, 60000);
		}

		if(Config.AUTODESTROY_PLAYER_ITEM_AFTER > 0)
		{
			_playersItems = new ConcurrentLinkedQueue<ItemInstance>();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckPlayersItemsForDestroy(), 60000, 60000);
		}

		_herbs = new ConcurrentLinkedQueue<ItemInstance>();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckHerbsForDestroy(), 1000, 1000);
	}

	public static ItemsAutoDestroy getInstance()
	{
		if(_instance == null)
			_instance = new ItemsAutoDestroy();
		return _instance;
	}

	public void addItem(ItemInstance item)
	{
		item.setDropTime(System.currentTimeMillis());
		_items.add(item);
	}

	public void addPlayerItem(ItemInstance item)
	{
		item.setDropTime(System.currentTimeMillis());
		_playersItems.add(item);
	}

	public void addHerb(ItemInstance herb)
	{
		herb.setDropTime(System.currentTimeMillis());
		_herbs.add(herb);
	}

	public class CheckItemsForDestroy extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			long _sleep = Config.AUTODESTROY_ITEM_AFTER * 1000L;
			long curtime = System.currentTimeMillis();
			for(ItemInstance item : _items)
			{
				if(item == null || item.getLastDropTime() == 0 || item.getLocation() != ItemInstance.ItemLocation.VOID)
					_items.remove(item);
				else if(item.getLastDropTime() + _sleep < curtime)
				{
					item.deleteMe();
					_items.remove(item);
				}
			}
		}
	}

	public class CheckPlayersItemsForDestroy extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			long _sleep = Config.AUTODESTROY_PLAYER_ITEM_AFTER * 1000L;
			long curtime = System.currentTimeMillis();
			for(ItemInstance item : _playersItems)
			{
				if(item == null || item.getLastDropTime() == 0 || item.getLocation() != ItemInstance.ItemLocation.VOID)
					_playersItems.remove(item);
				else if(item.getLastDropTime() + _sleep < curtime)
				{
					item.deleteMe();
					_playersItems.remove(item);
				}
			}
		}
	}

	public class CheckHerbsForDestroy extends RunnableImpl
	{
		static final long _sleep = 60000;

		@Override
		public void runImpl() throws Exception
		{
			long curtime = System.currentTimeMillis();
			for(ItemInstance item : _herbs)
				if(item == null || item.getLastDropTime() == 0 || item.getLocation() != ItemInstance.ItemLocation.VOID)
					_herbs.remove(item);
				else if(item.getLastDropTime() + _sleep < curtime)
				{
					item.deleteMe();
					_herbs.remove(item);
				}
		}
	}
}