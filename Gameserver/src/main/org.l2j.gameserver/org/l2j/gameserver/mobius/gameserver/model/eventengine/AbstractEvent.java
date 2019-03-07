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
package org.l2j.gameserver.mobius.gameserver.model.eventengine;

import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.AbstractScript;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 * @param <T>
 */
public abstract class AbstractEvent<T extends AbstractEventMember<?>>extends AbstractScript
{
	private final Map<Integer, T> _members = new ConcurrentHashMap<>();
	private IEventState _state;
	
	public final Map<Integer, T> getMembers()
	{
		return _members;
	}
	
	public final T getMember(int objectId)
	{
		return _members.get(objectId);
	}
	
	public final void addMember(T member)
	{
		_members.put(member.getObjectId(), member);
	}
	
	public final void broadcastPacket(IClientOutgoingPacket... packets)
	{
		_members.values().forEach(member -> member.sendPacket(packets));
	}
	
	public final IEventState getState()
	{
		return _state;
	}
	
	public final void setState(IEventState state)
	{
		_state = state;
	}
	
	@Override
	public final String getScriptName()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public final Path getScriptPath()
	{
		return null;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is on event, {@code false} otherwise.
	 */
	public boolean isOnEvent(L2PcInstance player)
	{
		return _members.containsKey(player.getObjectId());
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is blocked from leaving the game, {@code false} otherwise.
	 */
	public boolean isBlockingExit(L2PcInstance player)
	{
		return false;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is blocked from receiving death penalty upon death, {@code false} otherwise.
	 */
	public boolean isBlockingDeathPenalty(L2PcInstance player)
	{
		return false;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player can revive after death, {@code false} otherwise.
	 */
	public boolean canRevive(L2PcInstance player)
	{
		return true;
	}
}
