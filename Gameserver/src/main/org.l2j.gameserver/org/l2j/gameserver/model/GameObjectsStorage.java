package org.l2j.gameserver.model;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.instances.StaticObjectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Общее "супер" хранилище для всех объектов L2Object,
 * объекты делятся на типы - максимум 32 (0 - 31), для каждого типа свое хранилище,
 * в каждом хранилище может хранится до 67 108 864 объектов (0 - 67108863),
 * каждому объекту назначается уникальный 64-битный индентификатор типа long
 * который бинарно складывается из objId + тип + индекс в хранилище
 *
 * @author Drin & Diamond
 */
public class GameObjectsStorage
{
	private static TIntObjectMap<GameObject> _objects = new TIntObjectHashMap<>(60000 * Config.RATE_MOB_SPAWN + Config.MAXIMUM_ONLINE_USERS + 1000);
	private static TIntObjectMap<StaticObjectInstance> _staticObjects = new TIntObjectHashMap<>(1000);
	private static TIntObjectMap<NpcInstance> _npcs = new TIntObjectHashMap<>(60000 * Config.RATE_MOB_SPAWN);
	private static TIntObjectMap<Player> _players = TCollections.synchronizedMap(new TIntObjectHashMap<>(Config.MAXIMUM_ONLINE_USERS));

	public static GameObject findObject(int objId)
	{
		return _objects.get(objId);
	}

	public static Collection<GameObject> getObjects()
	{
		return _objects.valueCollection();
	}

	public static Collection<StaticObjectInstance> getStaticObjects()
	{
		return _staticObjects.valueCollection();
	}

	public static StaticObjectInstance getStaticObject(int id)
	{
		for(StaticObjectInstance object : _staticObjects.valueCollection())
			if(object.getUId() == id)
				return object;
		return null;
	}

	public static Player getPlayer(String name)
	{
		for(Player player : _players.valueCollection())
			if(player.getName().equalsIgnoreCase(name))
				return player;
		return null;
	}

	public static Player getPlayer(int objId)
	{
		return _players.get(objId);
	}

	public static Collection<Player> getPlayers()
	{
		return _players.valueCollection();
	}

	public static NpcInstance getNpc(String npcName)
	{
		NpcInstance result = null;
		for(NpcInstance temp : getNpcs())
			if(temp.getName().equalsIgnoreCase(npcName))
			{
				if(!temp.isDead())
					return temp;
				result = temp;
			}
		return result;
	}

	public static NpcInstance getNpc(int objId)
	{
		return _npcs.get(objId);
	}

	public static Collection<NpcInstance> getNpcs()
	{
		return _npcs.valueCollection();
	}

	public static NpcInstance getByNpcId(int npcId)
	{
		NpcInstance result = null;
		for(NpcInstance temp : getNpcs())
			if(temp.getNpcId() == npcId)
			{
				if(!temp.isDead())
					return temp;
				result = temp;
			}
		return result;
	}

	public static List<NpcInstance> getAllByNpcId(int npcId, boolean justAlive)
	{
		return getAllByNpcId(npcId, justAlive, justAlive);
	}

	public static List<NpcInstance> getAllByNpcId(int npcId, boolean justAlive, boolean justSpawned)
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();
		for(NpcInstance temp : getNpcs())
			if(temp.getNpcId() == npcId && (!justAlive || !temp.isDead()) && (!justSpawned || temp.isVisible()))
				result.add(temp);
		return result;
	}

	public static List<NpcInstance> getAllByNpcId(int[] npcIds, boolean justAlive)
	{
		return getAllByNpcId(npcIds, justAlive, justAlive);
	}

	public static List<NpcInstance> getAllByNpcId(int[] npcIds, boolean justAlive, boolean justSpawned)
	{
		List<NpcInstance> result = new ArrayList<NpcInstance>();
		for(NpcInstance temp : getNpcs())
			if((!justAlive || !temp.isDead()) && (!justSpawned || temp.isVisible()))
				if(ArrayUtils.contains(npcIds, temp.getNpcId()))
					result.add(temp);
		return result;
	}

	/**
	 * Кладет объект в хранилище и возвращает уникальный индентификатор по которому его можно будет найти в хранилище
	 */
	public static <T extends GameObject> void put(T o)
	{
		TIntObjectMap<T> map = getMapForObject(o);
		if(map != null)
			map.put(o.getObjectId(), o);

		_objects.put(o.getObjectId(), o);
	}

	public static <T extends GameObject> void remove(T o)
	{
		TIntObjectMap<T> map = getMapForObject(o);
		if(map != null)
			map.remove(o.getObjectId());

		_objects.remove(o.getObjectId());
	}

	@SuppressWarnings("unchecked")
	private static <T extends GameObject> TIntObjectMap<T> getMapForObject(T o)
	{
		if(o.isStaticObject())
			return (TIntObjectMap<T>) _staticObjects;

		if(o.isNpc())
			return (TIntObjectMap<T>) _npcs;

		if(o.isPlayer())
			return (TIntObjectMap<T>) _players;

		return null;
	}
}