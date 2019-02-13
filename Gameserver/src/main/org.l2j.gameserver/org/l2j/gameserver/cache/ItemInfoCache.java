package org.l2j.gameserver.cache;

import org.l2j.commons.cache.CacheFactory;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;

import javax.cache.Cache;

public class ItemInfoCache
{
	private final static ItemInfoCache _instance = new ItemInfoCache();

	public final static ItemInfoCache getInstance()
	{
		return _instance;
	}

	private Cache<Integer, ItemInfo> cache;

	private ItemInfoCache()
	{
		cache = CacheFactory.getInstance().getCache(this.getClass().getName(), Integer.class, ItemInfo.class);
	}

	public void put(ItemInstance item)
	{
		cache.put(item.getObjectId(), new ItemInfo(item));
	}

	/**
	 * Получить информацию из кеша, по objecId предмета. Если игрок онлайн и все еще владеет этим предметом
	 * информация будет обновлена.
	 * 
	 * @param objectId - идентификатор предмета
	 * @return возвращает описание вещи, или null если описания нет, или уже удалено из кеша
	 */
	public ItemInfo get(int objectId) {
		ItemInfo info = cache.get(objectId);

		Player player;

		if(info != null) {
			player = World.getPlayer(info.getOwnerId());

			ItemInstance item = null;

			if(player != null)
				item = player.getInventory().getItemByObjectId(objectId);

			if(item != null)
				if(item.getItemId() == info.getItemId())
					cache.put(item.getObjectId(), info = new ItemInfo(item));
		}

		return info;
	}
}