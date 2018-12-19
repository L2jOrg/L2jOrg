package org.l2j.gameserver.instancemanager;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.network.l2.s2c.EventTriggerPacket;

public class EventTriggersManager
{
	private static final EventTriggersManager _instance = new EventTriggersManager();

	public static EventTriggersManager getInstance()
	{
		return _instance;
	}

	private static final int[] EMPTY_INT_ARRAY = new int[0];

	private final TIntObjectMap<TIntSet> _activeTriggers = new TIntObjectHashMap<>();
	private final TIntObjectMap<TIntSet> _activeTriggersByMap = new TIntObjectHashMap<>();

	private EventTriggersManager()
	{}

	public boolean addTrigger(Reflection reflection, int triggerId)
	{
		TIntSet triggers = _activeTriggers.get(reflection.getId());
		if(triggers == null)
		{
			triggers = new TIntHashSet();
			_activeTriggers.put(reflection.getId(), triggers);
		}
		if(triggers.add(triggerId))
		{
			onAddTrigger(reflection, triggerId);
			return true;
		}
		return false;
	}

	public boolean addTrigger(int mapX, int mapY, int triggerId)
	{
		TIntSet triggers = _activeTriggersByMap.get(getMapHash(mapX, mapY));
		if(triggers == null)
		{
			triggers = new TIntHashSet();
			_activeTriggersByMap.put(getMapHash(mapX, mapY), triggers);
		}
		if(triggers.add(triggerId))
		{
			onAddTrigger(ReflectionManager.MAIN, triggerId);
			return true;
		}
		return false;
	}

	public boolean removeTrigger(Reflection reflection, int triggerId)
	{
		TIntSet triggers = _activeTriggers.get(reflection.getId());
		if(triggers != null && triggers.remove(triggerId))
		{
			onRemoveTrigger(reflection, triggerId);
			return true;
		}
		return false;
	}

	public boolean removeTrigger(int mapX, int mapY, int triggerId)
	{
		TIntSet triggers = _activeTriggersByMap.get(getMapHash(mapX, mapY));
		if(triggers != null && triggers.remove(triggerId))
		{
			onRemoveTrigger(ReflectionManager.MAIN, triggerId);
			return true;
		}
		return false;
	}

	public int[] getTriggers(Reflection reflection, boolean all)
	{
		if(all && reflection.isMain())
		{
			TIntSet allTriggers = new TIntHashSet();

			TIntSet triggers = _activeTriggers.get(reflection.getId());
			if(triggers != null)
				allTriggers.addAll(triggers);

			for(TIntSet t : _activeTriggersByMap.valueCollection())
				allTriggers.addAll(t);

			return allTriggers.toArray();
		}

		TIntSet triggers = _activeTriggers.get(reflection.getId());
		if(triggers == null)
			return EMPTY_INT_ARRAY;

		return triggers.toArray();
	}

	public int[] getTriggers(int mapX, int mapY)
	{
		TIntSet triggers = _activeTriggersByMap.get(getMapHash(mapX, mapY));
		if(triggers == null)
			return EMPTY_INT_ARRAY;

		return triggers.toArray();
	}

	public void removeTriggers(Reflection reflection)
	{
		TIntSet triggers = _activeTriggers.remove(reflection.getId());
		if(triggers != null)
		{
			for(int triggerId : triggers.toArray())
				onRemoveTrigger(reflection, triggerId);
		}

		if(reflection.isMain())
		{
			for(TIntSet t : _activeTriggersByMap.valueCollection())
			{
				for(int triggerId : t.toArray())
					onRemoveTrigger(reflection, triggerId);
			}
			_activeTriggersByMap.clear();
		}
	}

	private void onAddTrigger(Reflection reflection, int triggerId)
	{
		EventTriggerPacket packet = new EventTriggerPacket(triggerId, true);
		for(Player player : reflection.getPlayers())
			player.sendPacket(packet);
	}

	private void onRemoveTrigger(Reflection reflection, int triggerId)
	{
		EventTriggerPacket packet = new EventTriggerPacket(triggerId, false);
		for(Player player : reflection.getPlayers())
			player.sendPacket(packet);
	}

	private static int getMapHash(int mapX, int mapY)
	{
		return mapX * 1000 + mapY;
	}
}