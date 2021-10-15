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
package org.l2j.scripts.quests.newbie.armor.q10962;

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
public final class NewHorizons extends MoonArmorHunting {

	private static final int LEAHEN = 34111;

	private static final int MOUNTAIN_WEREWORLF = 21985;
	private static final int MOUNTAIN_FUNGUES = 21986;
	private static final int MUERTOS_WARRIOR = 21987;
	private static final int MUERTOS_CAPTAIN = 21988;
	
	public NewHorizons() {
		super(10962, LEAHEN, ClassId.JIN_KAMAEL_SOLDIER);
	}

	@Override
	protected IntCollection huntMonsters() {
		return IntSet.of(MOUNTAIN_WEREWORLF, MOUNTAIN_FUNGUES, MUERTOS_WARRIOR, MUERTOS_CAPTAIN);
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-107827, 47535, -1448);
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_NEW_HORIZONS;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GOLDEN_HILLS;
	}
}