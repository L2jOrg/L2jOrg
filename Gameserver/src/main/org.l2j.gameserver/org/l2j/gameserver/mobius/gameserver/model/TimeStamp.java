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

import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * Simple class containing all necessary information to maintain<br>
 * valid time stamps and reuse for skills and items reuse upon re-login.<br>
 * <b>Filter this carefully as it becomes redundant to store reuse for small delays.</b>
 * @author Yesod, Zoey76
 */
public class TimeStamp
{
	/** Item or skill ID. */
	private final int _id1;
	/** Item object ID or skill level. */
	private final int _id2;
	/** Skill level. */
	private final int _id3;
	/** Item or skill reuse time. */
	private final long _reuse;
	/** Time stamp. */
	private final long _stamp;
	/** Shared reuse group. */
	private final int _group;
	
	/**
	 * Skill time stamp constructor.
	 * @param skill the skill upon the stamp will be created.
	 * @param reuse the reuse time for this skill.
	 * @param systime overrides the system time with a customized one.
	 */
	public TimeStamp(Skill skill, long reuse, long systime)
	{
		_id1 = skill.getId();
		_id2 = skill.getLevel();
		_id3 = skill.getSubLevel();
		_reuse = reuse;
		_stamp = systime > 0 ? systime : System.currentTimeMillis() + reuse;
		_group = -1;
	}
	
	/**
	 * Item time stamp constructor.
	 * @param item the item upon the stamp will be created.
	 * @param reuse the reuse time for this item.
	 * @param systime overrides the system time with a customized one.
	 */
	public TimeStamp(L2ItemInstance item, long reuse, long systime)
	{
		_id1 = item.getId();
		_id2 = item.getObjectId();
		_id3 = 0;
		_reuse = reuse;
		_stamp = systime > 0 ? systime : System.currentTimeMillis() + reuse;
		_group = item.getSharedReuseGroup();
	}
	
	/**
	 * Gets the time stamp.
	 * @return the time stamp, either the system time where this time stamp was created or the custom time assigned
	 */
	public long getStamp()
	{
		return _stamp;
	}
	
	/**
	 * Gets the item ID.
	 * @return the item ID
	 */
	public int getItemId()
	{
		return _id1;
	}
	
	/**
	 * Gets the item object ID.
	 * @return the item object ID
	 */
	public int getItemObjectId()
	{
		return _id2;
	}
	
	/**
	 * Gets the skill ID.
	 * @return the skill ID
	 */
	public int getSkillId()
	{
		return _id1;
	}
	
	/**
	 * Gets the skill level.
	 * @return the skill level
	 */
	public int getSkillLvl()
	{
		return _id2;
	}
	
	/**
	 * Gets the skill sub level.
	 * @return the skill level
	 */
	public int getSkillSubLvl()
	{
		return _id3;
	}
	
	/**
	 * Gets the reuse.
	 * @return the reuse
	 */
	public long getReuse()
	{
		return _reuse;
	}
	
	/**
	 * Get the shared reuse group.<br>
	 * Only used on items.
	 * @return the shared reuse group
	 */
	public int getSharedReuseGroup()
	{
		return _group;
	}
	
	/**
	 * Gets the remaining time.
	 * @return the remaining time for this time stamp to expire
	 */
	public long getRemaining()
	{
		return Math.max(_stamp - System.currentTimeMillis(), 0);
	}
	
	/**
	 * Verifies if the reuse delay has passed.
	 * @return {@code true} if this time stamp has expired, {@code false} otherwise
	 */
	public boolean hasNotPassed()
	{
		return System.currentTimeMillis() < _stamp;
	}
}
