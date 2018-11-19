package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.templates.npc.NpcTemplate;

public class SaveableMonsterInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public SaveableMonsterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		RaidBossSpawnManager.getInstance().onBossDeath(this);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		RaidBossSpawnManager.getInstance().onBossSpawned(this);
	}
}