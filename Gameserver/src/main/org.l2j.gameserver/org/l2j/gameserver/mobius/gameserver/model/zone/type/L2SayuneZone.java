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
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import com.l2jmobius.Config;
import org.l2j.commons.concurrent.ThreadPool;
import org.l2j.gameserver.mobius.gameserver.enums.CategoryType;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player.FlyMoveStartTask;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;

/**
 * @author UnAfraid
 */
public class L2SayuneZone extends L2ZoneType
{
	private int _mapId = -1;
	
	public L2SayuneZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "mapId":
			{
				_mapId = Integer.parseInt(value);
				break;
			}
			default:
			{
				super.setParameter(name, value);
			}
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character.isPlayer() && (character.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || Config.FREE_JUMPS_FOR_ALL) && !character.getActingPlayer().isMounted() && !character.isTransformed())
		{
			character.setInsideZone(ZoneId.SAYUNE, true);
			ThreadPoolManager.getInstance().execute(new FlyMoveStartTask(this, character.getActingPlayer()));
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.SAYUNE, false);
		}
	}
	
	public int getMapId()
	{
		return _mapId;
	}
}
