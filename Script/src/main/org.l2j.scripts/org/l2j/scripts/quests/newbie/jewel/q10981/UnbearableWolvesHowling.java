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
package org.l2j.scripts.quests.newbie.jewel.q10981;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class UnbearableWolvesHowling extends NoviceJewelQuest {
	private static final int NEWBIE_GUIDE = 30598;
	private static final int JACKSON = 30002;

	private static final int BEARDED_KELTIR = 20481;
	private static final int WOLF = 20120;

	public UnbearableWolvesHowling() {
		super(10981, NEWBIE_GUIDE, JACKSON);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[] { BEARDED_KELTIR, WOLF};
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_UNBEARABLE_WOLVES_HOWLING;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_WOLVES_AND_BEARDED_KELTIRS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-90050, 241763, -3560);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_NUSE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_ARMOR_MERCHANT_JACKSON;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91646;
	}
}