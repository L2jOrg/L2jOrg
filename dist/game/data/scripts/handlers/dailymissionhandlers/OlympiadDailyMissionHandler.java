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
package handlers.dailymissionhandlers;

import com.l2jmobius.gameserver.enums.DailyMissionStatus;
import com.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import com.l2jmobius.gameserver.model.DailyMissionDataHolder;
import com.l2jmobius.gameserver.model.DailyMissionPlayerEntry;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.Containers;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import com.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author UnAfraid
 */
public class OlympiadDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _amount;
	
	public OlympiadDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_OLYMPIAD_MATCH_RESULT, (OnOlympiadMatchResult event) -> onOlympiadMatchResult(event), this));
	}
	
	@Override
	public boolean isAvailable(L2PcInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		if (entry != null)
		{
			switch (entry.getStatus())
			{
				case NOT_AVAILABLE: // Initial state
				{
					if (entry.getProgress() >= _amount)
					{
						entry.setStatus(DailyMissionStatus.AVAILABLE);
						storePlayerEntry(entry);
					}
					break;
				}
				case AVAILABLE:
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private void onOlympiadMatchResult(OnOlympiadMatchResult event)
	{
		if (event.getWinner() != null)
		{
			final DailyMissionPlayerEntry winnerEntry = getPlayerEntry(event.getWinner().getObjectId(), true);
			if (winnerEntry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
			{
				if (winnerEntry.increaseProgress() >= _amount)
				{
					winnerEntry.setStatus(DailyMissionStatus.AVAILABLE);
				}
				storePlayerEntry(winnerEntry);
			}
		}
		
		if (event.getLoser() != null)
		{
			final DailyMissionPlayerEntry loseEntry = getPlayerEntry(event.getLoser().getObjectId(), true);
			if (loseEntry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
			{
				if (loseEntry.increaseProgress() >= _amount)
				{
					loseEntry.setStatus(DailyMissionStatus.AVAILABLE);
				}
				storePlayerEntry(loseEntry);
			}
		}
	}
}
