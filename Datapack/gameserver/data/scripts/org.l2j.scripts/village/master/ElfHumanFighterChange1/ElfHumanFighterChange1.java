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
package village.master.ElfHumanFighterChange1;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * Elf Human class transfer AI
 * @author Adry_85
 */
public final class ElfHumanFighterChange1 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
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
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "30066-01.htm": // pabris003h
			case "30066-02.htm": // pabris006ha
			case "30066-03.htm": // pabris007ha
			case "30066-04.htm": // pabris007hat
			case "30066-05.htm": // pabris006hb
			case "30066-06.htm": // pabris007hb
			case "30066-07.htm": // pabris007hbt
			case "30066-08.htm": // pabris006hc
			case "30066-09.htm": // pabris007hc
			case "30066-10.htm": // pabris007hct
			case "30066-11.htm": // pabris003e
			case "30066-12.htm": // pabris006ea
			case "30066-13.htm": // pabris007ea
			case "30066-14.htm": // pabris007eat
			case "30066-15.htm": // pabris006eb
			case "30066-16.htm": // pabris007eb
			case "30066-17.htm": // pabris007ebt
			case "30288-01.htm": // master_rains003h
			case "30288-02.htm": // master_rains006ha
			case "30288-03.htm": // master_rains007ha
			case "30288-04.htm": // master_rains007hat
			case "30288-05.htm": // master_rains006hb
			case "30288-06.htm": // master_rains007hb
			case "30288-07.htm": // master_rains007hbt
			case "30288-08.htm": // master_rains006hc
			case "30288-09.htm": // master_rains007hc
			case "30288-10.htm": // master_rains007hct
			case "30288-11.htm": // master_rains003e
			case "30288-12.htm": // master_rains006ea
			case "30288-13.htm": // master_rains007ea
			case "30288-14.htm": // master_rains007eat
			case "30288-15.htm": // master_rains006eb
			case "30288-16.htm": // master_rains007eb
			case "30288-17.htm": // master_rains007ebt
			case "30373-01.htm": // grandmaster_ramos003h
			case "30373-02.htm": // grandmaster_ramos006ha
			case "30373-03.htm": // grandmaster_ramos007ha
			case "30373-04.htm": // grandmaster_ramos007hat
			case "30373-05.htm": // grandmaster_ramos006hb
			case "30373-06.htm": // grandmaster_ramos007hb
			case "30373-07.htm": // grandmaster_ramos007hbt
			case "30373-08.htm": // grandmaster_ramos006hc
			case "30373-09.htm": // grandmaster_ramos007hc
			case "30373-10.htm": // grandmaster_ramos007hct
			case "30373-11.htm": // grandmaster_ramos003e
			case "30373-12.htm": // grandmaster_ramos006ea
			case "30373-13.htm": // grandmaster_ramos007ea
			case "30373-14.htm": // grandmaster_ramos007eat
			case "30373-15.htm": // grandmaster_ramos006eb
			case "30373-16.htm": // grandmaster_ramos007eb
			case "30373-17.htm": // grandmaster_ramos007ebt
			case "32094-01.htm": // grandmaster_shull003h
			case "32094-02.htm": // grandmaster_shull006ha
			case "32094-03.htm": // grandmaster_shull007ha
			case "32094-04.htm": // grandmaster_shull007hat
			case "32094-05.htm": // grandmaster_shull006hb
			case "32094-06.htm": // grandmaster_shull007hb
			case "32094-07.htm": // grandmaster_shull007hbt
			case "32094-08.htm": // grandmaster_shull006hc
			case "32094-09.htm": // grandmaster_shull007hc
			case "32094-10.htm": // grandmaster_shull007hct
			case "32094-11.htm": // grandmaster_shull003e
			case "32094-12.htm": // grandmaster_shull006ea
			case "32094-13.htm": // grandmaster_shull007ea
			case "32094-14.htm": // grandmaster_shull007eat
			case "32094-15.htm": // grandmaster_shull006eb
			case "32094-16.htm": // grandmaster_shull007eb
			case "32094-17.htm": // grandmaster_shull007ebt
			{
				htmltext = event;
				break;
			}
			case "1":
			case "4":
			case "7":
			case "19":
			case "22":
			{
				htmltext = ClassChangeRequested(player, npc, Integer.valueOf(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(Player player, Npc npc, int classId)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			htmltext = npc.getId() + "-19.htm"; // fnYouAreSecondClass
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = npc.getId() + "-20.htm"; // fnYouAreThirdClass
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "30066-41.htm"; // fnYouAreFourthClass
		}
		else if ((classId == WARRIOR) && (player.getClassId() == ClassId.FIGHTER))
		{
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
		}
		else if ((classId == KNIGHT) && (player.getClassId() == ClassId.FIGHTER))
		{
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
		}
		else if ((classId == ROGUE) && (player.getClassId() == ClassId.FIGHTER))
		{
			if (player.getLevel() < 20)
			{
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
		}
		else if ((classId == ELVEN_KNIGHT) && (player.getClassId() == ClassId.ELVEN_FIGHTER))
		{
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
		}
		else if ((classId == ELVEN_SCOUT) && (player.getClassId() == ClassId.ELVEN_FIGHTER))
		{
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
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = null;
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
