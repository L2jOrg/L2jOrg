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
package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.enums.DailyMissionStatus;

/**
 * @author UnAfraid
 */
public class DailyMissionPlayerEntry
{
	private final int _objectId;
	private final int _rewardId;
	private DailyMissionStatus _status = DailyMissionStatus.NOT_AVAILABLE;
	private int _progress;
	private long _lastCompleted;
	private boolean _recentlyCompleted;
	
	public DailyMissionPlayerEntry(int objectId, int rewardId)
	{
		_objectId = objectId;
		_rewardId = rewardId;
	}
	
	public DailyMissionPlayerEntry(int objectId, int rewardId, int status, int progress, long lastCompleted)
	{
		this(objectId, rewardId);
		_status = DailyMissionStatus.valueOf(status);
		_progress = progress;
		_lastCompleted = lastCompleted;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public int getRewardId()
	{
		return _rewardId;
	}
	
	public DailyMissionStatus getStatus()
	{
		return _status;
	}
	
	public void setStatus(DailyMissionStatus status)
	{
		_status = status;
	}
	
	public int getProgress()
	{
		return _progress;
	}
	
	public void setProgress(int progress)
	{
		_progress = progress;
	}
	
	public int increaseProgress()
	{
		_progress++;
		return _progress;
	}
	
	public long getLastCompleted()
	{
		return _lastCompleted;
	}
	
	public void setLastCompleted(long lastCompleted)
	{
		_lastCompleted = lastCompleted;
	}
	
	public boolean getRecentlyCompleted()
	{
		return _recentlyCompleted;
	}
	
	public void setRecentlyCompleted(boolean recentlyCompleted)
	{
		_recentlyCompleted = recentlyCompleted;
	}
}
