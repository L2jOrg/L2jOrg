/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.datatables.drop;

import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.holders.DropHolder;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class EventDropHolder extends DropHolder {

	private final int minLevel;
	private final int maxLevel;
	private final IntCollection monsterIds;
	
	public EventDropHolder(int itemId, long min, long max, double chance, int minLevel, int maxLevel, IntCollection monsterIds) {
		super(null, itemId, min, max, chance);
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.monsterIds = monsterIds;
	}
	
	public int getMinLevel()
	{
		return minLevel;
	}
	
	public int getMaxLevel()
	{
		return maxLevel;
	}

	public boolean hasMonster(int id) {
		return monsterIds.contains(id);
	}

	public boolean checkLevel(int level) {
		return minLevel <= level && maxLevel >= level;
	}
}
