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
package org.l2j.scripts.village.master.job.dwarf.blacksmith.first;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfBlacksmithChange1 extends AbstractNpcAI {

	private static final int[] NPCS = {
		30499, // Tapoy
		30504, // Mendio
		30595, // Opix
	};

	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int FINAL_PASS_CERTIFICATE = 1635;

	private static final int ARTISAN = 56;
	
	private DwarfBlacksmithChange1() {
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30499-01.htm", "30499-02.htm", "30499-03.htm", "30499-04.htm", "30504-01.htm", "30504-02.htm", "30504-03.htm", "30504-04.htm", "30595-01.htm", "30595-02.htm",
				"30595-03.htm", "30595-04.htm", "32093-01.htm", "32093-02.htm", "32093-03.htm", "32093-04.htm" -> event;
			case "56" -> classChangeRequested(player, npc, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, Npc npc, int classId) {
		String htmlText = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmlText = npc.getId() + "-06.htm";
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmlText = npc.getId() + "-07.htm";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmlText = "30499-12.htm";
		}
		else if (classId == ARTISAN && player.getClassId() == ClassId.DWARVEN_FIGHTER) {
			htmlText = classChangeToArtisan(player, npc);
		}
		return htmlText;
	}

	private String classChangeToArtisan(Player player, Npc npc) {
		String htmlText;
		if (player.getLevel() < 20) {
			if (hasQuestItems(player, FINAL_PASS_CERTIFICATE)) {
				htmlText = npc.getId() + "-08.htm";
			} else {
				htmlText = npc.getId() + "-09.htm";
			}
		}
		else if (hasQuestItems(player, FINAL_PASS_CERTIFICATE)) {
			takeItems(player, FINAL_PASS_CERTIFICATE, -1);
			player.setClassId(ARTISAN);
			player.setBaseClass(ARTISAN);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmlText = npc.getId() + "-10.htm";
		} else {
			htmlText = npc.getId() + "-11.htm";
		}
		return htmlText;
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		String htmlText;
		if (player.isInCategory(CategoryType.WARSMITH_GROUP)) {
			htmlText = npc.getId() + "-01.htm";
		} else {
			htmlText = npc.getId() + "-05.htm";
		}
		return htmlText;
	}
	
	public static DwarfBlacksmithChange1 provider()
	{
		return new DwarfBlacksmithChange1();
	}
}
