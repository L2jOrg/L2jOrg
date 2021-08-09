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
package org.l2j.scripts.quests.newbie.jewel.q10989;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class DangerousPredators extends NoviceJewelQuest {

	private static final int NEWBIE_GUIDE = 30601;
	private static final int GERALD = 30650;

	private static final int LONGTAIL_KELTIR = 20533;
	private static final int ELDER_LONGTAIL_KELTIR = 20539;
	private static final int BLACK_WOLF = 21983;

	public DangerousPredators() {
		super(10989, NEWBIE_GUIDE, GERALD);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[]{ LONGTAIL_KELTIR, ELDER_LONGTAIL_KELTIR, BLACK_WOLF };
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_DANGEROUS_PREDATORS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(115960, -174659, -960);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_USE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_HEAD_PRIEST_OF_THE_EARTH_GERALD;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91650;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_WOLVES_AND_BEARDED_KELTIRS_2;
	}
}