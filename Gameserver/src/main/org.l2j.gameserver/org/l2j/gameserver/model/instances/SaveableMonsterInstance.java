package org.l2j.gameserver.model.instances;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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