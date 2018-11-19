package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;

public class ReflectionBossInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;

	private final static long COLLAPSE_AFTER_DEATH_TIME = 5 * 60 * 1000L; // 5 мин

	public ReflectionBossInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		clearReflection();
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
	 */
	protected void clearReflection()
	{
		Reflection reflection = getReflection();
		if(!reflection.isDefault())
			reflection.startCollapseTimer(COLLAPSE_AFTER_DEATH_TIME);
	}
}