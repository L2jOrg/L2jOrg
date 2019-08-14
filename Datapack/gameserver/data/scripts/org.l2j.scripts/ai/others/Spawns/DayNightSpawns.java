/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.Spawns;

import ai.AbstractNpcAI;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.spawns.SpawnTemplate;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public final class DayNightSpawns extends AbstractNpcAI
{
	private static final String NIGHT_GROUP_NAME = "nightTime";
	private static final String DAY_GROUP_NAME = "dayTime";
	private final Set<SpawnTemplate> _templates = ConcurrentHashMap.newKeySet();
	
	private DayNightSpawns()
	{
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (_templates.add(template))
		{
			manageSpawns(template, WorldTimeController.getInstance().isNight());
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		_templates.remove(template);
	}
	
	@RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDayNightChange(OnDayNightChange event)
	{
		_templates.forEach(template -> manageSpawns(template, event.isNight()));
	}
	
	private void manageSpawns(SpawnTemplate template, boolean isNight)
	{
		template.getGroups().forEach(group ->
		{
			if (NIGHT_GROUP_NAME.equalsIgnoreCase(group.getName()))
			{
				if (!isNight)
				{
					group.despawnAll();
				}
				else
				{
					group.spawnAll();
				}
			}
			else if (DAY_GROUP_NAME.equalsIgnoreCase(group.getName()))
			{
				if (isNight)
				{
					group.despawnAll();
				}
				else
				{
					group.spawnAll();
				}
			}
		});
	}
	
	public static AbstractNpcAI provider()
	{
		return new DayNightSpawns();
	}
}
