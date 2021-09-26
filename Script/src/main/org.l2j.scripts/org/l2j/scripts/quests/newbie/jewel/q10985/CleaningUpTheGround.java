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
package org.l2j.scripts.quests.newbie.jewel.q10985;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class CleaningUpTheGround extends NoviceJewelQuest {

	private static final int NEWBIE_GUIDE = 30600;
	private static final int VOLODOS = 30137;

	private static final int ASHEN_WOLF = 20456;
	private static final int GOBLIN = 20003;
	private static final int IMP = 20004;

	public CleaningUpTheGround() {
		super(10985, NEWBIE_GUIDE, VOLODOS);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[] {ASHEN_WOLF, GOBLIN, IMP};
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_CLEANING_UP_THE_GROUNDS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(8945, 3529, -2504);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_USE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_GROCER_VOLLODOS;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91648;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_MONSTERS_NEAR_THE_VILLAGE;
	}

	@Override
	protected String nextQuest() {
		return "SwampMonster";
	}
}