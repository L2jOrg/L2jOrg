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
package org.l2j.scripts.events.squash.xmas;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.scripts.events.squash.Squash;

/**
 * @author vGodFather
 * @author JoeAlisson
 */
public class MerrySquashmas extends Squash
{
	private static final int MANAGER = 33888;
	private static final int SNOWY_NECTAR_SKILL = 17110;

	private static final int[][] SQUASH_SPAWN_CHANCES = {
		{13402, 5},
		{13401, 10},
		{13400, 30}
	};

	private static final int[][] LARGE_SQUASH_SPAWN_CHANCES = {
		{13406, 5},
		{13405, 10},
		{13404, 30}
	};
	
	private MerrySquashmas() {
		super(MANAGER,
				IntSet.of(13399, 13400, 13401, 13402, 13403, 13404, 13405, 13406),
				IntSet.of(13403, 13404, 13405, 13406));
	}

	@Override
	protected int getNectaSkill() {
		return SNOWY_NECTAR_SKILL;
	}

	@Override
	protected int getYoungSquash() {
		return 13399;
	}

	@Override
	protected int[][] squashSpawnChances() {
		return SQUASH_SPAWN_CHANCES;
	}

	@Override
	protected int getLargeYoungSquash() {
		return 13403;
	}

	@Override
	protected int[][] largeSquashSpawnChances() {
		return LARGE_SQUASH_SPAWN_CHANCES;
	}

	public static AbstractScript provider() {
		return new MerrySquashmas();
	}
}