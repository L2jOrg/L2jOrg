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
package org.l2j.scripts.quests.newbie.armor.q10982;

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
public class SpiderHunt extends MoonArmorHunting {

	private static final int JACKSON = 30002;

	private static final int GIANT_SPIDER = 20103;
	private static final int GIANT_FANG_SPIDER = 20106;
	private static final int GIANT_BLADE_SPIDER = 20108;
	
	public SpiderHunt() {
		super(10982, JACKSON, ClassId.FIGHTER, ClassId.MAGE);
	}

	@Override
	protected IntCollection huntMonsters() {
		return IntSet.of(GIANT_SPIDER, GIANT_FANG_SPIDER, GIANT_BLADE_SPIDER);
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_SPIDER_HUNT;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_GIANT_SPIDERS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-117409, 227185, -2896);
	}

}