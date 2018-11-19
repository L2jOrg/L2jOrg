package l2s.gameserver.templates.spawn;

import l2s.commons.time.cron.SchedulingPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date  15:22/15.12.2010
 */
public class SpawnTemplate
{
	private final String _name;
	private final PeriodOfDay _periodOfDay;
	private final int _count;
	private final int _respawn;
	private final int _respawnRandom;
	private final SchedulingPattern _respawnPattern;
	private final List<SpawnNpcInfo> _npcList = new ArrayList<SpawnNpcInfo>(1);
	private final List<SpawnRange> _spawnRangeList = new ArrayList<SpawnRange>(1);

	public SpawnTemplate(String name, PeriodOfDay periodOfDay, int count, int respawn, int respawnRandom, String respawnPattern)
	{
		_name = name;
		_periodOfDay = periodOfDay;
		_count = count;
		_respawn = respawn;
		_respawnRandom = respawnRandom;
		_respawnPattern = respawnPattern == null || respawnPattern.isEmpty() ? null : new SchedulingPattern(respawnPattern);
	}

	//----------------------------------------------------------------------------------------------------------
	public void addSpawnRange(SpawnRange range)
	{
		_spawnRangeList.add(range);
	}

	public SpawnRange getSpawnRange(int index)
	{
		return _spawnRangeList.get(index);
	}

	//----------------------------------------------------------------------------------------------------------
	public void addNpc(SpawnNpcInfo info)
	{
		_npcList.add(info);
	}

	public SpawnNpcInfo getNpcId(int index)
	{
		return _npcList.get(index);
	}

	//----------------------------------------------------------------------------------------------------------

	public List<SpawnNpcInfo> getNpcList()
	{
		return _npcList;
	}

	public List<SpawnRange> getSpawnRangeList()
	{
		return _spawnRangeList;
	}

	public String getName()
	{
		return _name;
	}

	public int getCount()
	{
		return _count;
	}

	public int getRespawn()
	{
		return _respawn;
	}

	public int getRespawnRandom()
	{
		return _respawnRandom;
	}

	public SchedulingPattern getRespawnPattern()
	{
		return _respawnPattern;
	}

	public PeriodOfDay getPeriodOfDay()
	{
		return _periodOfDay;
	}
}