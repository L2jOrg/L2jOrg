package npc.model.residences.castle;

import npc.model.residences.SiegeGuardInstance;
import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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