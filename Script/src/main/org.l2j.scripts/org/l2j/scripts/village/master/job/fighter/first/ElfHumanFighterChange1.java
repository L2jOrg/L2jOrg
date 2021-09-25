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
package org.l2j.scripts.village.master.job.fighter.first;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Elf Human class transfer AI
 * @author Adry_85
 */
public final class ElfHumanFighterChange1 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30066, // Pabris
		30288, // Rains
		30373, // Ramos
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int MEDALLION_OF_WARRIOR = 1145;
	private static final int SWORD_OF_RITUAL = 1161;
	private static final int BEZIQUES_RECOMMENDATION = 1190;
	private static final int ELVEN_KNIGHT_BROOCH = 1204;
	private static final int REISAS_RECOMMENDATION = 1217;
	// Classes
	private static final int WARRIOR = 1;
	private static final int KNIGHT = 4;
	private static final int ROGUE = 7;
	private static final int ELVEN_KNIGHT = 19;
	private static final int ELVEN_SCOUT = 22;
	
	private ElfHumanFighterChange1()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30066-01.htm", "30066-02.htm", "30066-03.htm", "30066-04.htm", "30066-05.htm", "30066-06.htm", "30066-07.htm", "30066-08.htm", "30066-09.htm", "30066-10.htm", "30066-11.htm", "30066-12.htm",
				"30066-13.htm", "30066-14.htm", "30066-15.htm", "30066-16.htm", "30066-17.htm", "30288-01.htm", "30288-02.htm", "30288-03.htm", "30288-04.htm", "30288-05.htm", "30288-06.htm", "30288-07.htm",
				"30288-08.htm", "30288-09.htm", "30288-10.htm", "30288-11.htm", "30288-12.htm", "30288-13.htm", "30288-14.htm", "30288-15.htm", "30288-16.htm", "30288-17.htm", "30373-01.htm", "30373-02.htm",
				"30373-03.htm", "30373-04.htm", "30373-05.htm", "30373-06.htm", "30373-07.htm", "30373-08.htm", "30373-09.htm", "30373-10.htm", "30373-11.htm", "30373-12.htm", "30373-13.htm", "30373-14.htm",
				"30373-15.htm", "30373-16.htm", "30373-17.htm", "32094-01.htm", "32094-02.htm", "32094-03.htm", "32094-04.htm", "32094-05.htm", "32094-06.htm", "32094-07.htm", "32094-08.htm", "32094-09.htm",
				"32094-10.htm", "32094-11.htm", "32094-12.htm", "32094-13.htm", "32094-14.htm", "32094-15.htm", "32094-16.htm", "32094-17.htm" ->  event;
			case "1", "4", "7", "19", "22" ->  classChangeRequested(player, npc, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, Npc npc, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmltext = npc.getId() + "-19.htm";
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = npc.getId() + "-20.htm";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmltext = "30066-41.htm";
		} else if (classId == WARRIOR && player.getClassId() == ClassId.FIGHTER) {
			htmltext = classChangeToWarrior(player, npc);
		} else if (classId == KNIGHT && player.getClassId() == ClassId.FIGHTER) {
			htmltext = classChangeToKnight(player, npc);
		} else if (classId == ROGUE && player.getClassId() == ClassId.FIGHTER) {
			htmltext = classChangeToRogue(player, npc);
		} else if (classId == ELVEN_KNIGHT && player.getClassId() == ClassId.ELVEN_FIGHTER) {
			htmltext = classChangeToElvenKnight(player, npc);
		} else if ((classId == ELVEN_SCOUT) && (player.getClassId() == ClassId.ELVEN_FIGHTER)) {
			htmltext = classChangeToScout(player, npc);
		}
		return htmltext;
	}

	private String classChangeToScout(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, REISAS_RECOMMENDATION))
			{
				htmltext = npc.getId() + "-37.htm"; // fnLowLevel22
			}
			else
			{
				htmltext = npc.getId() + "-38.htm"; // fnLowLevelNoProof22
			}
		}
		else if (hasQuestItems(player, REISAS_RECOMMENDATION))
		{
			takeItems(player, REISAS_RECOMMENDATION, -1);
			player.setClassId(ELVEN_SCOUT);
			player.setBaseClass(ELVEN_SCOUT);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-39.htm"; // fnAfterClassChange22
		}
		else
		{
			htmltext = npc.getId() + "-40.htm"; // fnNoProof22
		}
		return htmltext;
	}

	private String classChangeToElvenKnight(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
			{
				htmltext = npc.getId() + "-33.htm"; // fnLowLevel21
			}
			else
			{
				htmltext = npc.getId() + "-34.htm"; // fnLowLevelNoProof21
			}
		}
		else if (hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
		{
			takeItems(player, ELVEN_KNIGHT_BROOCH, -1);
			player.setClassId(ELVEN_KNIGHT);
			player.setBaseClass(ELVEN_KNIGHT);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-35.htm"; // fnAfterClassChange21
		}
		else
		{
			htmltext = npc.getId() + "-36.htm"; // fnNoProof21
		}
		return htmltext;
	}

	private String classChangeToRogue(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20) {
			if (hasQuestItems(player, BEZIQUES_RECOMMENDATION))
			{
				htmltext = npc.getId() + "-29.htm"; // fnLowLevel13
			}
			else
			{
				htmltext = npc.getId() + "-30.htm"; // fnLowLevelNoProof13
			}
		}
		else if (hasQuestItems(player, BEZIQUES_RECOMMENDATION))
		{
			takeItems(player, BEZIQUES_RECOMMENDATION, -1);
			player.setClassId(ROGUE);
			player.setBaseClass(ROGUE);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-31.htm"; // fnAfterClassChange13
		}
		else
		{
			htmltext = npc.getId() + "-32.htm"; // fnNoProof13
		}
		return htmltext;
	}

	private String classChangeToKnight(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, SWORD_OF_RITUAL))
			{
				htmltext = npc.getId() + "-25.htm"; // fnLowLevel12
			}
			else
			{
				htmltext = npc.getId() + "-26.htm"; // fnLowLevelNoProof12
			}
		}
		else if (hasQuestItems(player, SWORD_OF_RITUAL))
		{
			takeItems(player, SWORD_OF_RITUAL, -1);
			player.setClassId(KNIGHT);
			player.setBaseClass(KNIGHT);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-27.htm"; // fnAfterClassChange12
		}
		else
		{
			htmltext = npc.getId() + "-28.htm"; // fnNoProof12
		}
		return htmltext;
	}

	private String classChangeToWarrior(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, MEDALLION_OF_WARRIOR))
			{
				htmltext = npc.getId() + "-21.htm"; // fnLowLevel11
			}
			else
			{
				htmltext = npc.getId() + "-22.htm"; // fnLowLevelNoProof11
			}
		}
		else if (hasQuestItems(player, MEDALLION_OF_WARRIOR))
		{
			takeItems(player, MEDALLION_OF_WARRIOR, -1);
			player.setClassId(WARRIOR);
			player.setBaseClass(WARRIOR);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-23.htm"; // fnAfterClassChange11
		}
		else
		{
			htmltext = npc.getId() + "-24.htm"; // fnNoProof11
		}
		return htmltext;
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext;
		final Race playerRace = player.getRace();
		if (player.isInCategory(CategoryType.FIGHTER_GROUP) && ((playerRace == Race.HUMAN) || (playerRace == Race.ELF)))
		{
			if (playerRace == Race.HUMAN)
			{
				htmltext = npc.getId() + "-01.htm"; // fnClassList1
			}
			else
			{
				htmltext = npc.getId() + "-11.htm"; // fnClassList2
			}
		}
		else
		{
			htmltext = npc.getId() + "-18.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static ElfHumanFighterChange1 provider()
	{
		return new ElfHumanFighterChange1();
	}
}
