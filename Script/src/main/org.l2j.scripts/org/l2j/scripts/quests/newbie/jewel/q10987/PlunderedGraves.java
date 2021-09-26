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
package org.l2j.scripts.quests.newbie.jewel.q10987;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class PlunderedGraves extends NoviceJewelQuest {

	private static final int NEWBIE_GUIDE = 30602;
	private static final int USKA = 30560;

	private static final int KASHA_WOLF = 20475;
	private static final int KASHA_TIMBER_WOLF = 20477;
	private static final int GOBLIN_TOMB_RAIDER = 20319;
	private static final int RAKECLAW_IMP_HUNTER = 20312;

	public PlunderedGraves() {
		super(10987, NEWBIE_GUIDE, USKA);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[] {KASHA_WOLF, KASHA_TIMBER_WOLF, GOBLIN_TOMB_RAIDER, RAKECLAW_IMP_HUNTER};
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_PLUNDERED_GRAVES;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-39527, -117654, -1840);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_USE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_ACCESSORY_MERCHANT_USKA;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91649;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.TRACK_DOWN_GRAVE_ROBBERS;
	}

	@Override
	protected String nextQuest() {
		return "Conspiracy";
	}
}