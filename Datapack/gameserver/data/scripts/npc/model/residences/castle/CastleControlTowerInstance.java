package npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Spawner;
import org.l2j.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.util.HashSet;
import java.util.Set;

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