/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Olympiad Manager AI.
 * @author St3eT
 */
public final class OlyManager extends AbstractNpcAI {
	private static final int MANAGER = 31688;
	private static final int EQUIPMENT_MULTISELL = 3168801;

	private OlyManager() {
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "OlyManager-info.html":
			case "OlyManager-infoHistory.html":
			case "OlyManager-infoRules.html":
			case "OlyManager-infoPoints.html":
			case "OlyManager-infoPointsCalc.html":
			case "OlyManager-rank.html":
			case "OlyManager-rewards.html":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "calculatePoints":
			{
				if (player.getUnclaimedOlympiadPoints() > 0)
				{
					htmltext = "OlyManager-calculateEnough.html";
				}
				else
				{
					htmltext = "OlyManager-calculateNoEnough.html";
				}
				break;
			}
			case "calculatePointsDone":
			{
				if (player.isInventoryUnder80())
				{
					final int tradePoints = player.getUnclaimedOlympiadPoints();
					if (tradePoints > 0)
					{
						player.setUnclaimedOlympiadPoints(0);
						giveItems(player, Config.ALT_OLY_COMP_RITEM, tradePoints * Config.ALT_OLY_MARK_PER_POINT);
					}
				}
				else
				{
					player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				}
				break;
			}
			case "showEquipmentReward":
			{
				MultisellEngine.getInstance().separateAndSend(EQUIPMENT_MULTISELL, player, npc, false);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player) {
		return (!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (player.getLevel() < 55) ? "OlyManager-noNoble.html" : "OlyManager-noble.html";
	}
	
	public static AbstractNpcAI provider()
	{
		return new OlyManager();
	}
}