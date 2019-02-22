package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import org.l2j.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @date 8:58/17.03.2011
 */
public class CastleFlameTowerInstance extends SiegeToggleNpcInstance
{
	private static final long serialVersionUID = 1L;

	private Set<String> _zoneList;

	public CastleFlameTowerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onDeathImpl(Creature killer)
	{
		List<CastleSiegeEvent> events = getEvents(CastleSiegeEvent.class);
		if(events.isEmpty())
			return;

		for(CastleSiegeEvent event : events)
		{
			if(!event.isInProgress())
				continue;

			for(String s : _zoneList)
			{
				List<CastleDamageZoneObject> objects = event.getObjects(s);
				for(CastleDamageZoneObject zone : objects)
					zone.getZone().setActive(false);
			}
		}
	}

	@Override
	public void setZoneList(Set<String> set)
	{
		_zoneList = set;
	}
}
