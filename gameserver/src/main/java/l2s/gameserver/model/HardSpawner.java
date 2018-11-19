package l2s.gameserver.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.MinionData;
import l2s.gameserver.templates.spawn.SpawnNpcInfo;
import l2s.gameserver.templates.spawn.SpawnRange;
import l2s.gameserver.templates.spawn.SpawnTemplate;

/**
 * @author VISTALL
 * @date 4:58/19.05.2011
 */
public class HardSpawner extends Spawner
{
	private static final long serialVersionUID = 1L;

	private final SpawnTemplate _template;

	private final List<NpcInstance> _reSpawned = new CopyOnWriteArrayList<NpcInstance>();

	public HardSpawner(SpawnTemplate template)
	{
		_template = template;
		_spawned = new CopyOnWriteArrayList<NpcInstance>();
	}

	@Override
	public String getName()
	{
		return _template.getName();
	}

	@Override
	public void decreaseCount(NpcInstance oldNpc)
	{
		oldNpc.setSpawn(null); // [VISTALL] нужно убирать спавн что бы не вызвать зацикливания, и остановки спавна
		oldNpc.deleteMe();

		if(!_spawned.remove(oldNpc))
			return;

		if(!hasRespawn())
		{
			decreaseCount0(null, null, oldNpc.getDeathTime());
			return;
		}

		SpawnNpcInfo npcInfo = getRandomNpcInfo();

		NpcInstance npc = npcInfo.getTemplate().getNewInstance(npcInfo.getParameters());
		npc.setSpawn(this);

		List<MinionData> minionsData = npcInfo.getMinionData();
		if (!minionsData.isEmpty())
		{
			for(MinionData minionData : minionsData)
				npc.getMinionList().addMinion(minionData);
		}

		_reSpawned.add(npc);

		decreaseCount0(npcInfo.getTemplate(), npc, oldNpc.getDeathTime());
	}

	@Override
	public NpcInstance doSpawn(boolean spawn)
	{
		SpawnNpcInfo npcInfo = getRandomNpcInfo();

		return doSpawn0(npcInfo.getTemplate(), spawn, npcInfo.getParameters(), npcInfo.getMinionData());
	}

	@Override
	protected NpcInstance initNpc(NpcInstance mob, boolean spawn)
	{
		_reSpawned.remove(mob);

		SpawnRange range = getRandomSpawnRange();
		mob.setSpawnRange(range);
		return initNpc0(mob, range.getRandomLoc(getReflection().getGeoIndex()), spawn);
	}

	@Override
	public int getMainNpcId()
	{
		return _template.getNpcId(0).getTemplate().getId();
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

	private SpawnNpcInfo getRandomNpcInfo()
	{
		return Rnd.get(_template.getNpcList());
	}

	@Override
	public SpawnRange getRandomSpawnRange()
	{
		return Rnd.get(_template.getSpawnRangeList());
	}

	@Override
	public HardSpawner clone()
	{
		HardSpawner spawnDat = new HardSpawner(_template);
		spawnDat.setAmount(_maximumCount);
		spawnDat.setRespawnDelay(getRespawnDelay(), getRespawnDelayRandom());
		spawnDat.setRespawnPattern(getRespawnPattern());
		spawnDat.setRespawnTime(0);
		return spawnDat;
	}
}