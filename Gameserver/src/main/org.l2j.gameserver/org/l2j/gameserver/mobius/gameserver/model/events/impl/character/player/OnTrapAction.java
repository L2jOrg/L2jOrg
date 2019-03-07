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
package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import com.l2jmobius.gameserver.enums.TrapAction;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2TrapInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnTrapAction implements IBaseEvent
{
	private final L2TrapInstance _trap;
	private final L2Character _trigger;
	private final TrapAction _action;
	
	public OnTrapAction(L2TrapInstance trap, L2Character trigger, TrapAction action)
	{
		_trap = trap;
		_trigger = trigger;
		_action = action;
	}
	
	public L2TrapInstance getTrap()
	{
		return _trap;
	}
	
	public L2Character getTrigger()
	{
		return _trigger;
	}
	
	public TrapAction getAction()
	{
		return _action;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_TRAP_ACTION;
	}
	
}
