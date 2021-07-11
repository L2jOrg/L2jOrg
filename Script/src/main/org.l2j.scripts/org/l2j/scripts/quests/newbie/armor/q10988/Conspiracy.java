/*
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
package org.l2j.scripts.quests.newbie.armor.q10988;

import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.armor.MoonArmorHuntingQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public class Conspiracy extends MoonArmorHuntingQuest {

	private static final int USKA = 30560;

	private static final int KASHA_SPIDER = 20474;
	private static final int KASHA_BLADE_SPIDER = 20478;
	private static final int MARAKU_WEREVOLF_CHIEFTAIN = 20364;
	private static final int EVIL_EYE_PATROL = 20428;
	
	public Conspiracy() {
		super(10988, USKA, ClassId.ORC_FIGHTER, ClassId.ORC_MAGE);
	}

	@Override
	protected IntCollection huntMonsters() {
		return IntSet.of(KASHA_SPIDER, KASHA_BLADE_SPIDER, MARAKU_WEREVOLF_CHIEFTAIN, EVIL_EYE_PATROL);
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_CONSPIRACY;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.EXPOSE_A_PLOT_OF_MARAKU_WEREWOLVES;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(13136, -131688, -1312);
	}
}