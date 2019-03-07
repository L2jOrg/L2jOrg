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
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author Sdw
 */
public class LevelDailyMissionHandler extends AbstractDailyMissionHandler
{
	private final int _level;
	private final boolean _dualclass;
	
	public LevelDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
		_level = holder.getParams().getInt("level");
		_dualclass = holder.getParams().getBoolean("dualclass", false);
	}
	
	@Override
	public void init()
	{
		Containers.Players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LEVEL_CHANGED, (OnPlayerLevelChanged event) -> onPlayerLevelChanged(event), this));
	}
	
	@Override
	public boolean isAvailable(L2PcInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		if (entry != null)
		{
			switch (entry.getStatus())
			{
				case NOT_AVAILABLE:
				{
					if ((player.getLevel() >= _level) && (player.isDualClassActive() == _dualclass))
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
	
	@Override
	public void reset()
	{
		// Level rewards doesn't reset daily
	}
	
	@Override
	public int getProgress(L2PcInstance player)
	{
		return _level;
	}
	
	private void onPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final L2PcInstance player = event.getActiveChar();
		if ((player.getLevel() >= _level) && (player.isDualClassActive() == _dualclass))
		{
			final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
			if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
			{
				entry.setStatus(DailyMissionStatus.AVAILABLE);
				storePlayerEntry(entry);
			}
		}
	}
}
