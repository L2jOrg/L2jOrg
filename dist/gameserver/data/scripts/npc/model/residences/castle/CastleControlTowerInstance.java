package npc.model.residences.castle;

import java.util.HashSet;
import java.util.Set;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class CastleControlTowerInstance extends SiegeToggleNpcInstance
{
	private static final long serialVersionUID = 1L;

	private Set<Spawner> _spawnList = new HashSet<Spawner>();

	public CastleControlTowerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onDeathImpl(Creature killer)
	{
		for(Spawner spawn : _spawnList)
			spawn.stopRespawn();
		_spawnList.clear();
	}

	@Override
	public void register(Spawner spawn)
	{
		_spawnList.add(spawn);
	}
}