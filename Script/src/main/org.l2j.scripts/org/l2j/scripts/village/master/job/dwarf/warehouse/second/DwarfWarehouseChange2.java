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
package org.l2j.scripts.village.master.job.dwarf.warehouse.second;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfWarehouseChange2 extends AbstractNpcAI {

	private static final int[] NPCS = {
		30511, // Gesto
		30676, // Croop
		30685, // Baxt
		30845, // Klump
		30894, // Natools
	};

	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_SEARCHER = 2809;
	private static final int MARK_OF_GUILDSMAN = 3119;
	private static final int MARK_OF_PROSPERITY = 3238;

	private static final int BOUNTY_HUNTER = 55;
	
	private DwarfWarehouseChange2() {
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30511-03.htm", "30511-04.htm", "30511-05.htm" -> event;
			case "55" -> classChangeRequested(player, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, int classId) {
		String htmlText = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmlText = "30511-08.htm";
		}
		else if (classId == BOUNTY_HUNTER && player.getClassId() == ClassId.SCAVENGER) {
			htmlText = classChangeToBountyHunter(player);
		}
		return htmlText;
	}

	private String classChangeToBountyHunter(Player player) {
		String htmlText;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER)) {
				htmlText = "30511-09.htm";
			} else {
				htmlText = "30511-10.htm";
			}
		} else if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER)) {
			takeItems(player, -1, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER);
			player.setClassId(BOUNTY_HUNTER);
			player.setBaseClass(BOUNTY_HUNTER);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmlText = "30511-11.htm";
		} else {
			htmlText = "30511-12.htm";
		}
		return htmlText;
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		String htmlText;
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
			htmlText = "30511-01.htm";
		} else if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
			final ClassId classId = player.getClassId();
			if (classId == ClassId.SCAVENGER || classId == ClassId.BOUNTY_HUNTER) {
				htmlText = "30511-02.htm";
			} else {
				htmlText = "30511-06.htm";
			}
		} else {
			htmlText = "30511-07.htm";
		}
		return htmlText;
	}
	
	public static DwarfWarehouseChange2 provider()
	{
		return new DwarfWarehouseChange2();
	}
}
