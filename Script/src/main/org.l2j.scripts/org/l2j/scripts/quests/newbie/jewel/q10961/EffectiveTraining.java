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
package org.l2j.scripts.quests.newbie.jewel.q10961;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public final class EffectiveTraining extends NoviceJewelQuest {

	private static final int NEWBIE_GUIDE = 34110;
	private static final int REAHEN = 34111;

	private static final int GREY_KELTIR = 21981;
	private static final int ELDER_GREY_KELTIR = 21982;
	private static final int BLACK_WOLF = 21983;
	private static final int ELDER_BLACK_WOLF = 21984;

	public EffectiveTraining() {
		super(10961, NEWBIE_GUIDE, REAHEN);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[] { GREY_KELTIR, ELDER_GREY_KELTIR, BLACK_WOLF, ELDER_BLACK_WOLF };
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_EFFECTIVE_TRAINING;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_ON_THE_HILL_OF_HOPE;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(-120020, 55668, -1560);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_NUSE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_TRADER_REAHEN;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91917;
	}
}