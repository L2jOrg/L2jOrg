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
package org.l2j.scripts.ai.others.OlyManager;

import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Olympiad Manager AI.
 * @author St3eT
 * @author JoeAlisson
 */
public final class OlympiadManager extends AbstractNpcAI {
	private static final int MANAGER = 31688;
	private static final int EQUIPMENT_MULTISELL = 3168801;

	private OlympiadManager() {
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		if(!Olympiad.getInstance().checkLevelAndClassRestriction(player)){
			return "no-requirements.html";
		}

		String htmltext = null;
		
		switch (event) {
			case "info.html":
			case "history.html":
			case "rules.html":
			case "rewards.html": {
				htmltext = event;
				break;
			}
			case "index": {
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "calculatePoints": {
				if (Olympiad.getInstance().getUnclaimedPoints(player) > 0) {
					htmltext = "calculate-enough.html";
				} else {
					htmltext = "calculate-no-enough.html";
				}
				break;
			}
			case "calculatePointsDone": {
				Olympiad.getInstance().changePoints(player);
				break;
			}
			case "showEquipmentReward": {
				MultisellEngine.getInstance().separateAndSend(EQUIPMENT_MULTISELL, player, npc, false);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player) {
		return Olympiad.getInstance().checkLevelAndClassRestriction(player) ?  "main.html" : "no-requirements.html";
	}
	
	public static AbstractNpcAI provider() {
		return new OlympiadManager();
	}
}