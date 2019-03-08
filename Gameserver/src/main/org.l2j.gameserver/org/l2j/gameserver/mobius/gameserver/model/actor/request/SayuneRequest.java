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
package org.l2j.gameserver.mobius.gameserver.model.actor.request;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SayuneData;
import org.l2j.gameserver.mobius.gameserver.enums.SayuneType;
import org.l2j.gameserver.mobius.gameserver.model.SayuneEntry;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune.ExFlyMove;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune.ExFlyMoveBroadcast;
import org.l2j.gameserver.mobius.gameserver.util.Broadcast;

import java.util.*;

/**
 * @author UnAfraid
 */
public class SayuneRequest extends AbstractRequest
{
	private final int _mapId;
	private boolean _isSelecting;
	private final Deque<SayuneEntry> _possibleEntries = new LinkedList<>();
	
	public SayuneRequest(L2PcInstance player, int mapId)
	{
		super(player);
		_mapId = mapId;
		
		final SayuneEntry map = SayuneData.getInstance().getMap(_mapId);
		Objects.requireNonNull(map);
		
		_possibleEntries.addAll(map.getInnerEntries());
	}
	
	@Override
	public boolean isUsing(int objectId)
	{
		return false;
	}
	
	private SayuneEntry findEntry(int pos)
	{
		if (_possibleEntries.isEmpty())
		{
			return null;
		}
		else if (_isSelecting)
		{
			return _possibleEntries.stream().filter(loc -> loc.getId() == pos).findAny().orElse(null);
		}
		return _possibleEntries.removeFirst();
	}
	
	public synchronized void move(L2PcInstance activeChar, int pos)
	{
		final SayuneEntry map = SayuneData.getInstance().getMap(_mapId);
		if ((map == null) || map.getInnerEntries().isEmpty())
		{
			activeChar.sendMessage("MapId: " + _mapId + " was not found in the map!");
			return;
		}
		
		final SayuneEntry nextEntry = findEntry(pos);
		if (nextEntry == null)
		{
			activeChar.removeRequest(getClass());
			return;
		}
		
		// If player was selecting unset and set his possible path
		if (_isSelecting)
		{
			_isSelecting = false;
			
			// Set next possible path
			if (!nextEntry.isSelector())
			{
				_possibleEntries.clear();
				_possibleEntries.addAll(nextEntry.getInnerEntries());
			}
		}
		
		final SayuneType type = (pos == 0) && nextEntry.isSelector() ? SayuneType.START_LOC : nextEntry.isSelector() ? SayuneType.MULTI_WAY_LOC : SayuneType.ONE_WAY_LOC;
		final List<SayuneEntry> locations = nextEntry.isSelector() ? nextEntry.getInnerEntries() : Arrays.asList(nextEntry);
		if (nextEntry.isSelector())
		{
			_possibleEntries.clear();
			_possibleEntries.addAll(locations);
			_isSelecting = true;
		}
		
		activeChar.sendPacket(new ExFlyMove(activeChar, type, _mapId, locations));
		
		final SayuneEntry activeEntry = locations.get(0);
		Broadcast.toKnownPlayersInRadius(activeChar, new ExFlyMoveBroadcast(activeChar, type, map.getId(), activeEntry), 1000);
		activeChar.setXYZ(activeEntry);
	}
	
	public void onLogout()
	{
		final SayuneEntry map = SayuneData.getInstance().getMap(_mapId);
		if ((map != null) && !map.getInnerEntries().isEmpty())
		{
			final SayuneEntry nextEntry = findEntry(0);
			if (_isSelecting || ((nextEntry != null) && nextEntry.isSelector()))
			{
				// If player is on selector or next entry is selector go back to first entry
				getActiveChar().setXYZ(map);
			}
			else
			{
				// Try to find last entry to set player, if not set him to first entry
				final SayuneEntry lastEntry = map.getInnerEntries().get(map.getInnerEntries().size() - 1);
				if (lastEntry != null)
				{
					getActiveChar().setXYZ(lastEntry);
				}
				else
				{
					getActiveChar().setXYZ(map);
				}
			}
		}
	}
}
