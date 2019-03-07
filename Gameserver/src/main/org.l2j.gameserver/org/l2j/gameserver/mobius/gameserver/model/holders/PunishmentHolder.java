package org.l2j.gameserver.mobius.gameserver.model.holders;

import org.l2j.gameserver.mobius.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.mobius.gameserver.model.punishment.PunishmentType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public final class PunishmentHolder
{
	private final Map<String, Map<PunishmentType, PunishmentTask>> _holder = new ConcurrentHashMap<>();
	
	/**
	 * Stores the punishment task in the Map.
	 * @param task
	 */
	public void addPunishment(PunishmentTask task)
	{
		if (!task.isExpired())
		{
			final String key = String.valueOf(task.getKey());
			_holder.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(task.getType(), task);
		}
	}
	
	/**
	 * Removes previously stopped task from the Map.
	 * @param task
	 */
	public void stopPunishment(PunishmentTask task)
	{
		final String key = String.valueOf(task.getKey());
		if (_holder.containsKey(key))
		{
			task.stopPunishment();
			final Map<PunishmentType, PunishmentTask> punishments = _holder.get(key);
			punishments.remove(task.getType());
			if (punishments.isEmpty())
			{
				_holder.remove(key);
			}
		}
	}
	
	public void stopPunishment(PunishmentType type)
	{
		_holder.values().stream().flatMap(p -> p.values().stream()).filter(p -> p.getType() == type).forEach(t ->
		{
			t.stopPunishment();
			final String key = String.valueOf(t.getKey());
			final Map<PunishmentType, PunishmentTask> punishments = _holder.get(key);
			punishments.remove(t.getType());
			if (punishments.isEmpty())
			{
				_holder.remove(key);
			}
		});
	}
	
	/**
	 * @param key
	 * @param type
	 * @return {@code true} if Map contains the current key and type, {@code false} otherwise.
	 */
	public boolean hasPunishment(String key, PunishmentType type)
	{
		return getPunishment(key, type) != null;
	}
	
	/**
	 * @param key
	 * @param type
	 * @return {@link PunishmentTask} by specified key and type if exists, null otherwise.
	 */
	public PunishmentTask getPunishment(String key, PunishmentType type)
	{
		if (_holder.containsKey(key))
		{
			return _holder.get(key).get(type);
		}
		return null;
	}
}
