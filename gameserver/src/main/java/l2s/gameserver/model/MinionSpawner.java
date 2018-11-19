package l2s.gameserver.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.MinionData;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.spawn.SpawnRange;
import l2s.gameserver.utils.Location;

public class MinionSpawner extends Spawner
{
	private static final long serialVersionUID = 1L;
	private final MinionData _minionData;
	private final NpcInstance _master;
	private final List<NpcInstance> _reSpawned = new CopyOnWriteArrayList<NpcInstance>();

	private Location _spawnLoc = null;

	public MinionSpawner(MinionData minionData, NpcInstance master)
	{
		_minionData = minionData;
		_master = master;

		_spawned = new CopyOnWriteArrayList<NpcInstance>();

		_referenceCount = minionData.getAmount();
		_maximumCount = minionData.getAmount();

		int respawnTime = minionData.getRespawnTime() == -1 ? (master.isRaid() ? Config.DEFAULT_RAID_MINIONS_RESPAWN_DELAY : 0) : minionData.getRespawnTime();
		_nativeRespawnDelay = respawnTime;
		_respawnDelay = respawnTime;
		_respawnDelayRandom = 0;
		_respawnPattern = null;
	}

	public void setLoc(Location loc)
	{
		_spawnLoc = loc;
	}


	@Override
	public boolean isDoRespawn()
	{
		return super.isDoRespawn() && _master.isVisible() && !_master.isDead();
	}

	@Override
	public Reflection getReflection()
	{
		return _master.getReflection();
	}

	@Override
	public String getName()
	{
		return "Privates of " + _master.getName();
	}

	@Override
	public void decreaseCount(NpcInstance oldNpc)
	{
		oldNpc.setSpawn(null);
		oldNpc.deleteMe();

		if(!_spawned.remove(oldNpc))
			return;

		if(!hasRespawn())
		{
			decreaseCount0(null, null, oldNpc.getDeathTime());
			return;
		}

		NpcTemplate template = NpcHolder.getInstance().getTemplate(_minionData.getMinionId());
		if(template == null)
		{
			decreaseCount0(null, null, oldNpc.getDeathTime());
			return;
		}

		NpcInstance npc = template.getNewInstance(_minionData.getParameters());
		npc.setSpawn(this);

		_reSpawned.add(npc);

		decreaseCount0(template, npc, oldNpc.getDeathTime());
	}

	@Override
	public NpcInstance doSpawn(boolean spawn)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(_minionData.getMinionId());
		if(template == null)
			return null;
		return doSpawn0(template, spawn, _minionData.getParameters(), Collections.emptyList());
	}

	@Override
	protected NpcInstance initNpc(NpcInstance mob, boolean spawn)
	{
		_reSpawned.remove(mob);

		SpawnRange range = getRandomSpawnRange();
		mob.setSpawnRange(range);
		mob.setLeader(_master);
		mob.setHeading(_master.getHeading());
		mob.setRandomWalk(false);
		return initNpc0(mob, range.getRandomLoc(getReflection().getGeoIndex()), spawn);
	}

	@Override
	public int getMainNpcId()
	{
		return _minionData.getMinionId();
	}

	@Override
	public void respawnNpc(NpcInstance oldNpc)
	{
		initNpc(oldNpc, true);
	}

	@Override
	public void deleteAll()
	{
		super.deleteAll();

		for(NpcInstance npc : _reSpawned)
		{
			npc.setSpawn(null);
			npc.deleteMe();
		}

		_reSpawned.clear();
	}

	@Override
	public SpawnRange getRandomSpawnRange()
	{
		if(_spawnLoc != null)
			return _spawnLoc;
		return _master.getRndMinionPosition();
	}

	@Override
	public void setAmount(int amount)
	{}

	@Override
	public void setRespawnDelay(int respawnDelay, int respawnDelayRandom)
	{}

	@Override
	public void setRespawnDelay(int respawnDelay)
	{}

	@Override
	public void setRespawnPattern(SchedulingPattern pattern)
	{}

	@Override
	public void setRespawnTime(int respawnTime)
	{}

	@Override
	public MinionSpawner clone()
	{
		return new MinionSpawner(_minionData, _master);
	}
}