/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.events.squash.common;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.scripts.events.squash.Squash;

/**
 * @author vGodFather
 * @author JoeAlisson
 */
public class CommonSquash extends Squash
{
	private static final int MANAGER = 31860;
	private static final int NECTAR_SKILL = 2005;

	private static final int[][] SQUASH_SPAWN_CHANCES = {
			{13016, 5},
			{12775, 10},
			{12776, 30}
	};

	private static final int[][] LARGE_SQUASH_SPAWN_CHANCES = {
			{13017, 5},
			{12778, 10},
			{12779, 30}
	};

	private CommonSquash() {
		super(MANAGER,
			IntSet.of(12774, 12775, 12776, 12777, 12778, 12779, 13016, 13017),
			IntSet.of(12778, 12779, 13016, 13017));
	}

	@Override
	protected int getNectaSkill() {
		return NECTAR_SKILL;
	}

	@Override
	protected int getYoungSquash() {
		return 12774;
	}

	@Override
	protected int[][] squashSpawnChances() {
		return SQUASH_SPAWN_CHANCES;
	}

	@Override
	protected int getLargeYoungSquash() {
		return 12777;
	}

	@Override
	protected int[][] largeSquashSpawnChances() {
		return LARGE_SQUASH_SPAWN_CHANCES;
	}

	public static AbstractScript provider() {
		 return new CommonSquash();
	}
}