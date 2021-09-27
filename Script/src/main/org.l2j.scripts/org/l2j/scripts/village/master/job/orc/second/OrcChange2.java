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
package org.l2j.scripts.village.master.job.orc.second;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Orc class transfer AI.
 * @author Adry_85
 */
public final class OrcChange2 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30513, // Penatus
		30681, // Karia
		30704, // Garvarentz
		30865, // Ladanza
		30913, // Tushku
		31288, // Aklan
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_CHALLENGER = 2627; // proof11x, proof21x
	private static final int MARK_OF_PILGRIM = 2721; // proof31x, proof32x
	private static final int MARK_OF_DUELIST = 2762; // proof21z
	private static final int MARK_OF_WARSPIRIT = 2879; // proof32z
	private static final int MARK_OF_GLORY = 3203; // proof11y, proof21y, proof31y, proof32y
	private static final int MARK_OF_CHAMPION = 3276; // proof11z
	private static final int MARK_OF_LORD = 3390; // proof31z
	// Classes
	private static final int DESTROYER = 46;
	private static final int TYRANT = 48;
	private static final int OVERLORD = 51;
	private static final int WARCRYER = 52;
	
	private OrcChange2()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30513-03.htm", "30513-04.htm", "30513-05.htm", "30513-07.htm", "30513-08.htm", "30513-09.htm", "30513-10.htm",
					"30513-11.htm", "30513-12.htm", "30513-13.htm", "30513-14.htm", "30513-15.htm", "30513-16.htm" -> event;
			case "46", "48", "51", "52" ->  classChangeRequested(player, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = "30513-19.htm";
		} else if (classId == DESTROYER && player.getClassId() == ClassId.ORC_RAIDER) {
			htmltext = changeClassToDestroyer(player);
		} else if (classId == TYRANT && player.getClassId() == ClassId.ORC_MONK) {
			htmltext = changeClassToTyrant(player);
		} else if (classId == OVERLORD && player.getClassId() == ClassId.ORC_SHAMAN) {
			htmltext = changeClassToOverlord(player);
		} else if (classId == WARCRYER && player.getClassId() == ClassId.ORC_SHAMAN) {
			htmltext = changeClassToWarCryer(player);
		}
		return htmltext;
	}

	private String changeClassToWarCryer(Player player) {
		String htmlText;
		if (player.getLevel() < 40)
		{
			if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_WARSPIRIT))
			{
				htmlText = "30513-32.htm"; // fnLowLevel32
			}
			else
			{
				htmlText = "30513-33.htm"; // fnLowLevelNoProof32
			}
		}
		else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_WARSPIRIT))
		{
			takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_WARSPIRIT);
			player.setClassId(WARCRYER);
			player.setBaseClass(WARCRYER);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmlText = "30513-34.htm"; // fnAfterClassChange32
		}
		else
		{
			htmlText = "30513-35.htm"; // fnNoProof32
		}
		return htmlText;
	}

	private String changeClassToOverlord(Player player) {
		String htmltext;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_LORD))
			{
				htmltext = "30513-28.htm"; // fnLowLevel31
			}
			else
			{
				htmltext = "30513-29.htm"; // fnLowLevelNoProof31
			}
		}
		else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_LORD))
		{
			takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_GLORY, MARK_OF_LORD);
			player.setClassId(OVERLORD);
			player.setBaseClass(OVERLORD);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmltext = "30513-30.htm"; // fnAfterClassChange31
		}
		else
		{
			htmltext = "30513-31.htm"; // fnNoProof31
		}
		return htmltext;
	}

	private String changeClassToTyrant(Player player) {
		String htmltext;
		if (player.getLevel() < 40) {
			if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_DUELIST)) {
				htmltext = "30513-24.htm";
			} else {
				htmltext = "30513-25.htm";
			}
		}
		else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_DUELIST))
		{
			takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_DUELIST);
			player.setClassId(TYRANT);
			player.setBaseClass(TYRANT);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmltext = "30513-26.htm"; // fnAfterClassChange21
		}
		else
		{
			htmltext = "30513-27.htm"; // fnNoProof21
		}
		return htmltext;
	}

	private String changeClassToDestroyer(Player player) {
		String htmltext;
		if (player.getLevel() < 40)
		{
			if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_CHAMPION))
			{
				htmltext = "30513-20.htm"; // fnLowLevel11
			}
			else
			{
				htmltext = "30513-21.htm"; // fnLowLevelNoProof11
			}
		}
		else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_CHAMPION))
		{
			takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_GLORY, MARK_OF_CHAMPION);
			player.setClassId(DESTROYER);
			player.setBaseClass(DESTROYER);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
			htmltext = "30513-22.htm"; // fnAfterClassChange11
		}
		else
		{
			htmltext = "30513-23.htm"; // fnNoProof11
		}
		return htmltext;
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext;
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.ORC_MALL_CLASS) || player.isInCategory(CategoryType.ORC_FALL_CLASS)))
		{
			htmltext = "30513-01.htm"; // fnYouAreFourthClass
		}
		else if (player.isInCategory(CategoryType.ORC_MALL_CLASS) || player.isInCategory(CategoryType.ORC_FALL_CLASS))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.ORC_RAIDER) || (classId == ClassId.DESTROYER))
			{
				htmltext = "30513-02.htm"; // fnClassList1
			}
			else if ((classId == ClassId.ORC_MONK) || (classId == ClassId.TYRANT))
			{
				htmltext = "30513-06.htm"; // fnClassList2
			}
			else if ((classId == ClassId.ORC_SHAMAN) || (classId == ClassId.OVERLORD) || (classId == ClassId.WARCRYER))
			{
				htmltext = "30513-10.htm"; // fnClassList3
			}
			else
			{
				htmltext = "30513-17.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30513-18.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static OrcChange2 provider()
	{
		return new OrcChange2();
	}
}
