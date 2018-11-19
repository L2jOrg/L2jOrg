package npc.model.residences.castle;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.templates.npc.NpcTemplate;

import npc.model.residences.SiegeGuardInstance;

/**
 * @author Bonux
**/
public class CastleLordInstance extends SiegeGuardInstance
{
	private static final long serialVersionUID = 1L;

	public CastleLordInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		for(CastleSiegeEvent event : getEvents(CastleSiegeEvent.class))
			event.onLordDie(this);
	}
}