package org.l2j.gameserver.model;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.l2j.commons.lang.ArrayUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * Переработанный класс региона мира
 *
 * @author G1ta0
 */
public final class WorldRegion implements Iterable<GameObject>
{
	public final static WorldRegion[] EMPTY_L2WORLDREGION_ARRAY = new WorldRegion[0];

	/**
	 * Активация/деактивация соседних регионов
	 */
	public class ActivateTask extends RunnableImpl
	{
		private boolean _isActivating;

		public ActivateTask(boolean isActivating)
		{
			_isActivating = isActivating;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_isActivating)
				World.activate(WorldRegion.this);
			else
				World.deactivate(WorldRegion.this);
		}
	}

	/** Координаты региона в мире */
	private final int tileX, tileY, tileZ;
	/** Все объекты в регионе */
	private volatile GameObject[] _objects = GameObject.EMPTY_L2OBJECT_ARRAY;
	/** Количество объектов в регионе */
	private int _objectsCount = 0;
	/** Зоны пересекающие этот регион */
	private volatile Zone[] _zones = Zone.EMPTY_L2ZONE_ARRAY;
	/** Количество игроков в регионе */
	private int _playersCount = 0;
	/** Активен ли регион */
	private final AtomicBoolean _isActive = new AtomicBoolean();
	/** Запланированная задача активации/деактивации текущего и соседних регионов */
	private Future<?> _activateTask;
	/** Блокировка для чтения/записи объектов из региона */
	private final Lock lock = new ReentrantLock();

	WorldRegion(int x, int y, int z)
	{
		tileX = x;
		tileY = y;
		tileZ = z;
	}

	int getX()
	{
		return tileX;
	}

	int getY()
	{
		return tileY;
	}

	int getZ()
	{
		return tileZ;
	}

	/**
	 * Активация региона, включить или выключить AI всех NPC в регионе
	 *
	 * @param activate - переключатель
	 */
	void setActive(boolean activate)
	{
		if(!_isActive.compareAndSet(!activate, activate))
			return;

		NpcInstance npc;
		for(GameObject obj : this)
		{
			if(!obj.isNpc())
				continue;
			npc = (NpcInstance) obj;
			if(npc.getAI().isActive() != isActive())
			{
				if(isActive())
				{
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					npc.getAI().startAITask();
					npc.startRandomAnimation();
				}
				else if(!npc.getAI().isGlobalAI())
				{
					npc.stopRandomAnimation();
					npc.getAI().stopAITask();
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				}
			}
		}
	}

	void addToPlayers(GameObject object, Creature dropper)
	{
		if(object == null)
			return;

		Player player = null;
		if(object.isPlayer())
			player = (Player) object;
		else if(object.isObservePoint())
			player = ((ObservePoint) object).getPlayer();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		Player p;

		for(GameObject obj : this)
		{
			if(obj.getObjectId() == oid || obj.getReflectionId() != rid)
				continue;
			// Если object - игрок, показать ему все видимые обьекты в регионе
			if(player != null)
				player.sendPacket(player.addVisibleObject(obj, null));

			// Показать обьект всем игрокам в регионе
			if(obj.isPlayer() || obj.isObservePoint())
			{
				if(obj.isPlayer() && ((Player) obj).isInObserverMode())
					continue;

				p = obj.getPlayer();
				p.sendPacket(p.addVisibleObject(object, dropper));
			}
		}
	}

	void removeFromPlayers(GameObject object)
	{
		if(object == null)
			return;

		Player player = null;
		if(object.isPlayer())
			player = (Player) object;
		else if(object.isObservePoint())
			player = ((ObservePoint) object).getPlayer();

		int oid = object.getObjectId();
		int rid = object.getReflectionId();

		Player p;
		List<L2GameServerPacket> d = null;

		for(GameObject obj : this)
		{
			if(obj.getObjectId() == oid || obj.getReflectionId() != rid)
				continue;

			// Если object - игрок, убрать у него все видимые обьекты в регионе
			if(player != null)
				player.sendPacket(player.removeVisibleObject(obj, null));

			// Убрать обьект у всех игроков в регионе
			if(obj.isPlayer() || obj.isObservePoint())
			{
				if(obj.isPlayer() && ((Player)obj).isInObserverMode())
					continue;

				p = obj.getPlayer();
				p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList(p) : d));
			}
		}
	}

	public void addObject(GameObject obj)
	{
		if(obj == null)
			return;

		lock.lock();
		try
		{
			GameObject[] objects = _objects;

			GameObject[] resizedObjects = new GameObject[_objectsCount + 1];
			System.arraycopy(objects, 0, resizedObjects, 0, _objectsCount);
			objects = resizedObjects;
			objects[_objectsCount++] = obj;

			_objects = resizedObjects;

			if(obj.isPlayer())
				if(_playersCount++ == 0)
				{
					if(_activateTask != null)
						_activateTask.cancel(false);
					//активируем регион и соседние регионы через секунду
					_activateTask = ThreadPoolManager.getInstance().schedule(new ActivateTask(true), 1000L);
				}
		}
		finally
		{
			lock.unlock();
		}
	}

	public void removeObject(GameObject obj)
	{
		if(obj == null)
			return;

		lock.lock();
		try
		{
			GameObject[] objects = _objects;

			int index = -1;

			for(int i = 0; i < _objectsCount; i++)
			{
				if(objects[i] == obj)
				{
					index = i;
					break;
				}
			}

			if(index == -1) //Ошибочная ситуация
				return;

			_objectsCount--;

			GameObject[] resizedObjects = new GameObject[_objectsCount];
			objects[index] = objects[_objectsCount];
			System.arraycopy(objects, 0, resizedObjects, 0, _objectsCount);

			_objects = resizedObjects;

			if(obj.isPlayer())
				if(--_playersCount == 0)
				{
					if(_activateTask != null)
						_activateTask.cancel(false);
					//деактивируем регион и соседние регионы через минуту
					_activateTask = ThreadPoolManager.getInstance().schedule(new ActivateTask(false), 60000L);
				}
		}
		finally
		{
			lock.unlock();
		}
	}

	public int getObjectsSize()
	{
		return _objectsCount;
	}

	public int getPlayersCount()
	{
		return _playersCount;
	}

	public boolean isEmpty()
	{
		return _playersCount == 0;
	}

	public boolean isActive()
	{
		return _isActive.get();
	}

	void addZone(Zone zone)
	{
		lock.lock();
		try
		{
			_zones = ArrayUtils.add(_zones, zone);
		}
		finally
		{
			lock.unlock();
		}
	}

	void removeZone(Zone zone)
	{
		lock.lock();
		try
		{
			_zones = ArrayUtils.remove(_zones, zone);
		}
		finally
		{
			lock.unlock();
		}
	}

	Zone[] getZones()
	{
		// Без синхронизации и копирования, т.к. удаление/добавление зон происходит достаточно редко
		return _zones;
	}

	@Override
	public String toString()
	{
		return "[" + tileX + ", " + tileY + ", " + tileZ + "]";
	}

	@Override
	public Iterator<GameObject> iterator()
	{
		return new InternalIterator(_objects);
	}

	private class InternalIterator implements Iterator<GameObject>
	{
		final GameObject[] objects;
		int cursor = 0;

		public InternalIterator(final GameObject[] objects)
		{
			this.objects = objects;
		}

		@Override
		public boolean hasNext()
		{
			if(cursor < objects.length)
				return objects[cursor] != null;
			return false;
		}

		@Override
		public GameObject next()
		{
			return objects[cursor++];
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}