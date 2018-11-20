package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.templates.spawn.SpawnRange;
import org.l2j.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Deprecated
public class SimpleSpawner extends Spawner implements Cloneable
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(SimpleSpawner.class);

	private NpcTemplate _npcTemplate;

	private int _locx, _locy, _locz, _heading;
	private Territory _territory;

	public SimpleSpawner(NpcTemplate mobTemplate)
	{
		if(mobTemplate == null)
			throw new NullPointerException();

		_npcTemplate = mobTemplate;
		_spawned = new ArrayList<NpcInstance>(1);
	}

	public SimpleSpawner(int npcId)
	{
		NpcTemplate mobTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if(mobTemplate == null)
			throw new NullPointerException("Not find npc: " + npcId);

		_npcTemplate = mobTemplate;
		_spawned = new ArrayList<NpcInstance>(1);
	}

	/**
	 * Return the maximum number of L2NpcInstance that this L2Spawn can manage.<BR><BR>
	 */
	public int getAmount()
	{
		return _maximumCount;
	}

	/**
	 * Return the number of L2NpcInstance that this L2Spawn spawned.<BR><BR>
	 */
	public int getSpawnedCount()
	{
		return _currentCount;
	}

	/**
	 * Return the number of L2NpcInstance that this L2Spawn sheduled.<BR><BR>
	 */
	public int getSheduledCount()
	{
		return _scheduledCount;
	}

	/**
	 * Return the Identifier of the location area where L2NpcInstance can be spwaned.<BR><BR>
	 */
	public Territory getTerritory()
	{
		return _territory;
	}

	/**
	 * Return the position of the spawn point.<BR><BR>
	 */
	public Location getLoc()
	{
		return new Location(_locx, _locy, _locz);
	}

	/**
	 * Return the X position of the spawn point.<BR><BR>
	 */
	public int getLocx()
	{
		return _locx;
	}

	/**
	 * Return the Y position of the spawn point.<BR><BR>
	 */
	public int getLocy()
	{
		return _locy;
	}

	/**
	 * Return the Z position of the spawn point.<BR><BR>
	 */
	public int getLocz()
	{
		return _locz;
	}

	/**
	 * Return the Identifier of the L2NpcInstance manage by this L2Spwan contained in the L2NpcTemplate.<BR><BR>
	 */
	@Override
	public int getMainNpcId()
	{
		return _npcTemplate.getId();
	}

	@Override
	public SpawnRange getRandomSpawnRange()
	{
		if(_locx == 0 && _locz == 0)
			return _territory;
		return getLoc();
	}

	/**
	 * Return the heading of L2NpcInstance when they are spawned.<BR><BR>
	 */
	public int getHeading()
	{
		return _heading;
	}

	/**
	 * Восстанавливает измененное количество
	 */
	public void restoreAmount()
	{
		_maximumCount = _referenceCount;
	}

	/**
	 * Set the Identifier of the location area where L2NpcInstance can be spawned.<BR><BR>
	 */
	public void setTerritory(Territory territory)
	{
		_territory = territory;
	}

	/**
	 * Set the position(x, y, z, heading) of the spawn point.
	 * @param loc Location
	 */
	public void setLoc(Location loc)
	{
		_locx = loc.x;
		_locy = loc.y;
		_locz = loc.z;
		_heading = loc.h;
	}

	/**
	 * Set the X position of the spawn point.<BR><BR>
	 */
	public void setLocx(int locx)
	{
		_locx = locx;
	}

	/**
	 * Set the Y position of the spawn point.<BR><BR>
	 */
	public void setLocy(int locy)
	{
		_locy = locy;
	}

	/**
	 * Set the Z position of the spawn point.<BR><BR>
	 */
	public void setLocz(int locz)
	{
		_locz = locz;
	}

	/**
	 * Set the heading of L2NpcInstance when they are spawned.<BR><BR>
	 */
	public void setHeading(int heading)
	{
		_heading = heading;
	}

	@Override
	public void decreaseCount(NpcInstance oldNpc)
	{
		decreaseCount0(_npcTemplate, oldNpc, oldNpc.getDeathTime());
	}

	@Override
	public NpcInstance doSpawn(boolean spawn)
	{
		return doSpawn0(_npcTemplate, spawn, StatsSet.EMPTY, Collections.emptyList());
	}

	@Override
	protected NpcInstance initNpc(NpcInstance mob, boolean spawn)
	{
		Location newLoc;

		if(_territory != null)
		{
			newLoc = _territory.getRandomLoc(_reflection.getGeoIndex());
			newLoc.setH(Rnd.get(0xFFFF));
		}
		else
		{
			newLoc = getLoc();

			newLoc.h = getHeading() == -1 ? Rnd.get(0xFFFF) : getHeading();
		}

		return initNpc0(mob, newLoc, spawn);
	}

	@Override
	public void respawnNpc(NpcInstance oldNpc)
	{
		oldNpc.refreshID();
		initNpc(oldNpc, true);
	}

	@Override
	public SimpleSpawner clone()
	{
		SimpleSpawner spawnDat = new SimpleSpawner(_npcTemplate);
		spawnDat.setTerritory(_territory);
		spawnDat.setLocx(_locx);
		spawnDat.setLocy(_locy);
		spawnDat.setLocz(_locz);
		spawnDat.setHeading(_heading);
		spawnDat.setAmount(_maximumCount);
		spawnDat.setRespawnDelay(getRespawnDelay(), getRespawnDelayRandom());
		spawnDat.setRespawnPattern(getRespawnPattern());
		return spawnDat;
	}
}