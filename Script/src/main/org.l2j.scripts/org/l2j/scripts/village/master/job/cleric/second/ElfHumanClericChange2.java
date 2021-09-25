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
package org.l2j.scripts.village.master.job.cleric.second;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Elf Human class transfer AI.
 * @author Adry_85
 */
public final class ElfHumanClericChange2 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30120, // Maximilian
		30191, // Hollint
		30857, // Orven
		30905, // Squillari
		31279, // Gregory
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_PILGRIM = 2721; // proof11x, proof12x, proof21x
	private static final int MARK_OF_TRUST = 2734; // proof11y, proof12y
	private static final int MARK_OF_HEALER = 2820; // proof11z, proof21z
	private static final int MARK_OF_REFORMER = 2821; // proof12z
	private static final int MARK_OF_LIFE = 3140; // proof21y
	// Classes
	private static final int BISHOP = 16;
	private static final int PROPHET = 17;
	private static final int ELDER = 30;
	
	private ElfHumanClericChange2()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30120-02.htm", "30120-03.htm", "30120-04.htm", "30120-05.htm", "30120-06.htm", "30120-07.htm",
				 "30120-08.htm", "30120-10.htm", "30120-11.htm", "30120-12.htm" ->  event;
			case "16", "17", "30" ->  classChangeRequested(player, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, int classId){
		String htmlText = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)){
			htmlText = "30120-15.htm";
		} else if (classId == BISHOP && player.getClassId() == ClassId.CLERIC) {
			htmlText = classChangeToBishop(player);
		}
		else if (classId == PROPHET && player.getClassId() == ClassId.CLERIC) {
			htmlText = classChangeToProphet(player);
		}
		else if (classId == ELDER && player.getClassId() == ClassId.ORACLE) {
			htmlText = classChangeToElder(player);
		}
		return htmlText;
	}

	private String classChangeToElder(Player player) {
		String htmlText;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER)) {
				htmlText = "30120-24.htm";
			} else {
				htmlText = "30120-25.htm";
			}
		} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER)) {
			takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER);
			player.setClassId(ELDER);
			player.setBaseClass(ELDER);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmlText = "30120-26.htm";
		} else {
			htmlText = "30120-27.htm";
		}
		return htmlText;
	}

	private String classChangeToProphet(Player player) {
		String htmlText;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER)) {
				htmlText = "30120-20.htm";
			} else {
				htmlText = "30120-21.htm";
			}
		} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER)) {
			takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER);
			player.setClassId(PROPHET);
			player.setBaseClass(PROPHET);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmlText = "30120-22.htm";
		} else {
			htmlText = "30120-23.htm";
		}
		return htmlText;
	}

	private String classChangeToBishop(Player player) {
		String htmlText;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER)) {
				htmlText = "30120-16.htm";
			} else {
				htmlText = "30120-17.htm";
			}
		} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER)) {
			takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER);
			player.setClassId(BISHOP);
			player.setBaseClass(BISHOP);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmlText = "30120-18.htm";
		} else {
			htmlText = "30120-19.htm";
		}
		return htmlText;
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext;
		if (player.isInCategory(CategoryType.CLERIC_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS)))
		{
			htmltext = "30120-01.htm"; // fnYouAreFourthClass
		}
		else if (player.isInCategory(CategoryType.CLERIC_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS)))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.CLERIC) || (classId == ClassId.BISHOP) || (classId == ClassId.PROPHET))
			{
				htmltext = "30120-02.htm"; // fnClassList1
			}
			else if ((classId == ClassId.ORACLE) || (classId == ClassId.ELDER))
			{
				htmltext = "30120-09.htm"; // fnClassList2
			}
			else
			{
				htmltext = "30120-13.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30120-14.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static ElfHumanClericChange2 provider()
	{
		return new ElfHumanClericChange2();
	}
}
