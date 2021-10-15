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
package org.l2j.scripts.quests.newbie.armor.q10986;

import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.armor.MoonArmorHunting;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public class SwampMonster extends MoonArmorHunting {

	private static final int VOLODOS = 30137;

	private static final int MARSH_ZOMBIE = 20015;
	private static final int MARSH_ZOMBIE_SCOUT = 20020;
	private static final int DARK_HORROR = 20105;
	private static final int LESSER_DARK_HORROR = 20025;

	public SwampMonster() {
		super(10986, VOLODOS, ClassId.DARK_FIGHTER, ClassId.DARK_MAGE);
	}

	@Override
	protected IntCollection huntMonsters() {
		return IntSet.of(MARSH_ZOMBIE, MARSH_ZOMBIE_SCOUT, DARK_HORROR, LESSER_DARK_HORROR);
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_TERRIBLE_SWAMP_MONSTERS;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_ZOMBIES_AND_DARK_HORRORS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-19004, 47388, -3608);
	}
}