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
package org.l2j.scripts.quests.newbie.armor.q10984;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.armor.MoonArmorItemHunter;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public class CollectSpiderweb extends MoonArmorItemHunter {

	private static final int HERBIEL = 30150;

	private static final int BIG_SPIDER_WEB = 91652;
	private static final IntIntMap MONSTER_DROP_CHANCES = new HashIntIntMap();
	static {
		MONSTER_DROP_CHANCES.put(20308, 100);
		MONSTER_DROP_CHANCES.put(20460, 100);
		MONSTER_DROP_CHANCES.put(20466, 100);
	}

	public CollectSpiderweb() {
		super(10984, HERBIEL, BIG_SPIDER_WEB, ClassId.ELVEN_FIGHTER, ClassId.ELVEN_MAGE);
	}

	@Override
	protected IntCollection huntMonsters() {
		return MONSTER_DROP_CHANCES.keySet();
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_15_20_SPIDER_WEB;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(5135, 68148, -3256);
	}

	@Override
	protected int dropChance(int npcId) {
		return MONSTER_DROP_CHANCES.getOrDefault(npcId, 0);
	}
}