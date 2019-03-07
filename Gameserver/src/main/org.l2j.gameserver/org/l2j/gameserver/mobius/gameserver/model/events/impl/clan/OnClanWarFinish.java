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
package org.l2j.gameserver.mobius.gameserver.model.events.impl.clan;

import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnClanWarFinish implements IBaseEvent
{
	private final L2Clan _clan1;
	private final L2Clan _clan2;
	
	public OnClanWarFinish(L2Clan clan1, L2Clan clan2)
	{
		_clan1 = clan1;
		_clan2 = clan2;
	}
	
	public L2Clan getClan1()
	{
		return _clan1;
	}
	
	public L2Clan getClan2()
	{
		return _clan2;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_CLAN_WAR_FINISH;
	}
}
