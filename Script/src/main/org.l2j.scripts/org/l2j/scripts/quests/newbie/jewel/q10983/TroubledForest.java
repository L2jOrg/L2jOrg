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
package org.l2j.scripts.quests.newbie.jewel.q10983;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.scripts.quests.newbie.jewel.NoviceJewelQuest;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class TroubledForest extends NoviceJewelQuest {
	private static final int NEWBIE_GUIDE = 30599;
	private static final int HERBIEL = 30150;
	private static final int GOBLIN_RAIDER = 20325;
	private static final int KABOO_ORC = 20468;

	public TroubledForest() {
		super(10983, NEWBIE_GUIDE, HERBIEL);
	}

	@Override
	protected int[] huntMonsters() {
		return new int[] { GOBLIN_RAIDER, KABOO_ORC };
	}

	@Override
	protected NpcStringId questName() {
		return NpcStringId.LV_2_20_TROUBLED_FOREST;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.KILL_ORCS_AND_GOBLINS;
	}

	@Override
	protected Location huntingGroundLocation() {
		return new Location(52746, 49932, -3480);
	}

	@Override
	protected NpcStringId finishHuntingMessage() {
		return NpcStringId.YOU_VE_KILLED_ALL_THE_MONSTERS_NUSE_THE_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_RETURN_TO_GROCER_HERBIEL;
	}

	@Override
	protected int scrollEscapeToTrader() {
		return 91647;
	}

	@Override
	protected String nextQuest() {
		return "CollectSpiderweb";
	}
}