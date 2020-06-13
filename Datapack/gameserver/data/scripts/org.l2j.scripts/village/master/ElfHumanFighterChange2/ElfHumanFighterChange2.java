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
package village.master.ElfHumanFighterChange2;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * Elf Human class transfer AI.
 * @author Adry_85
 */
public final class ElfHumanFighterChange2 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
	{
		30109, // Hannavalt
		30187, // Klaus
		30689, // Siria
		30849, // Sedrick
		30900, // Marcus
		31276, // Bernhard
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_CHALLENGER = 2627; // proof11x, proof12x, proof42x
	private static final int MARK_OF_DUTY = 2633; // proof21x, proof22x, proof41x
	private static final int MARK_OF_SEEKER = 2673; // proof31x, proof32x, proof51x, proof52x
	private static final int MARK_OF_TRUST = 2734; // proof11y, proof12y, proof21y, proof22y, proof31y, proof32y
	private static final int MARK_OF_DUELIST = 2762; // proof11z, proof42z
	private static final int MARK_OF_SEARCHER = 2809; // proof31z, proof51z
	private static final int MARK_OF_HEALER = 2820; // proof21z, proof41z
	private static final int MARK_OF_LIFE = 3140; // proof41y, proof42y, proof51y, proof52y
	private static final int MARK_OF_CHAMPION = 3276; // proof12z
	private static final int MARK_OF_SAGITTARIUS = 3293; // proof32z, proof52z
	private static final int MARK_OF_WITCHCRAFT = 3307; // proof22z
	
	// Classes
	private static final int GLADIATOR = 2;
	private static final int WARLORD = 3;
	private static final int PALADIN = 5;
	private static final int DARK_AVENGER = 6;
	private static final int TREASURE_HUNTER = 8;
	private static final int HAWKEYE = 9;
	private static final int TEMPLE_KNIGHT = 20;
	private static final int SWORDSINGER = 21;
	private static final int PLAINS_WALKER = 23;
	private static final int SILVER_RANGER = 24;
	
	private ElfHumanFighterChange2()
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
			case "30109-02.htm": // master_lv3_hef003w
			case "30109-03.htm": // master_lv3_hef006wa
			case "30109-04.htm": // master_lv3_hef007wa
			case "30109-05.htm": // master_lv3_hef007wat
			case "30109-06.htm": // master_lv3_hef006wb
			case "30109-07.htm": // master_lv3_hef007wb
			case "30109-08.htm": // master_lv3_hef007wbt
			case "30109-09.htm": // master_lv3_hef003k
			case "30109-10.htm": // master_lv3_hef006ka
			case "30109-11.htm": // master_lv3_hef007ka
			case "30109-12.htm": // master_lv3_hef007kat
			case "30109-13.htm": // master_lv3_hef006kb
			case "30109-14.htm": // master_lv3_hef007kb
			case "30109-15.htm": // master_lv3_hef007kbt
			case "30109-16.htm": // master_lv3_hef003r
			case "30109-17.htm": // master_lv3_hef006ra
			case "30109-18.htm": // master_lv3_hef007ra
			case "30109-19.htm": // master_lv3_hef007rat
			case "30109-20.htm": // master_lv3_hef006rb
			case "30109-21.htm": // master_lv3_hef007rb
			case "30109-22.htm": // master_lv3_hef007rbt
			case "30109-23.htm": // master_lv3_hef003e
			case "30109-24.htm": // master_lv3_hef006ea
			case "30109-25.htm": // master_lv3_hef007ea
			case "30109-26.htm": // master_lv3_hef007eat
			case "30109-27.htm": // master_lv3_hef006eb
			case "30109-28.htm": // master_lv3_hef007eb
			case "30109-29.htm": // master_lv3_hef007ebt
			case "30109-30.htm": // master_lv3_hef003s
			case "30109-31.htm": // master_lv3_hef006sa
			case "30109-32.htm": // master_lv3_hef007sa
			case "30109-33.htm": // master_lv3_hef007sat
			case "30109-34.htm": // master_lv3_hef006sb
			case "30109-35.htm": // master_lv3_hef007sb
			case "30109-36.htm": // master_lv3_hef007sbt
			{
				htmltext = event;
				break;
			}
			case "2":
			case "3":
			case "5":
			case "6":
			case "8":
			case "9":
			case "20":
			case "21":
			case "23":
			case "24":
			{
				htmltext = ClassChangeRequested(player, Integer.valueOf(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(Player player, int classId)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "30109-39.htm"; // fnYouAreThirdClass
		}
		else if ((classId == GLADIATOR) && (player.getClassId() == ClassId.WARRIOR))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_DUELIST))
				{
					htmltext = "30109-40.htm"; // fnLowLevel11
				}
				else
				{
					htmltext = "30109-41.htm"; // fnLowLevelNoProof11
				}
			}
			else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_DUELIST))
			{
				takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_DUELIST);
				player.setClassId(GLADIATOR);
				player.setBaseClass(GLADIATOR);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-42.htm"; // fnAfterClassChange11
			}
			else
			{
				htmltext = "30109-43.htm"; // fnNoProof11
			}
		}
		else if ((classId == WARLORD) && (player.getClassId() == ClassId.WARRIOR))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_CHAMPION))
				{
					htmltext = "30109-44.htm"; // fnLowLevel12
				}
				else
				{
					htmltext = "30109-45.htm"; // fnLowLevelNoProof12
				}
			}
			else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_CHAMPION))
			{
				takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_TRUST, MARK_OF_CHAMPION);
				player.setClassId(WARLORD);
				player.setBaseClass(WARLORD);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-46.htm"; // fnAfterClassChange12
			}
			else
			{
				htmltext = "30109-47.htm"; // fnNoProof12
			}
		}
		else if ((classId == PALADIN) && (player.getClassId() == ClassId.KNIGHT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_HEALER))
				{
					htmltext = "30109-48.htm"; // fnLowLevel21
				}
				else
				{
					htmltext = "30109-49.htm"; // fnLowLevelNoProof21
				}
			}
			else if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_HEALER))
			{
				takeItems(player, -1, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_HEALER);
				player.setClassId(PALADIN);
				player.setBaseClass(PALADIN);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-50.htm"; // fnAfterClassChange21
			}
			else
			{
				htmltext = "30109-51.htm"; // fnNoProof21
			}
		}
		else if ((classId == DARK_AVENGER) && (player.getClassId() == ClassId.KNIGHT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_WITCHCRAFT))
				{
					htmltext = "30109-52.htm"; // fnLowLevel22
				}
				else
				{
					htmltext = "30109-53.htm"; // fnLowLevelNoProof22
				}
			}
			else if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_WITCHCRAFT))
			{
				takeItems(player, -1, MARK_OF_DUTY, MARK_OF_TRUST, MARK_OF_WITCHCRAFT);
				player.setClassId(DARK_AVENGER);
				player.setBaseClass(DARK_AVENGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-54.htm"; // fnAfterClassChange22
			}
			else
			{
				htmltext = "30109-55.htm"; // fnNoProof22
			}
		}
		else if ((classId == TREASURE_HUNTER) && (player.getClassId() == ClassId.ROGUE))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SEARCHER))
				{
					htmltext = "30109-56.htm"; // fnLowLevel31
				}
				else
				{
					htmltext = "30109-57.htm"; // fnLowLevelNoProof31
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SEARCHER))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SEARCHER);
				player.setClassId(TREASURE_HUNTER);
				player.setBaseClass(TREASURE_HUNTER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-58.htm"; // fnAfterClassChange31
			}
			else
			{
				htmltext = "30109-59.htm"; // fnNoProof31
			}
		}
		else if ((classId == HAWKEYE) && (player.getClassId() == ClassId.ROGUE))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SAGITTARIUS))
				{
					htmltext = "30109-60.htm"; // fnLowLevel32
				}
				else
				{
					htmltext = "30109-61.htm"; // fnLowLevelNoProof32
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SAGITTARIUS))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_TRUST, MARK_OF_SAGITTARIUS);
				player.setClassId(HAWKEYE);
				player.setBaseClass(HAWKEYE);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-62.htm"; // fnAfterClassChange32
			}
			else
			{
				htmltext = "30109-63.htm"; // fnNoProof32
			}
		}
		else if ((classId == TEMPLE_KNIGHT) && (player.getClassId() == ClassId.ELVEN_KNIGHT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_LIFE, MARK_OF_HEALER))
				{
					htmltext = "30109-64.htm"; // fnLowLevel41
				}
				else
				{
					htmltext = "30109-65.htm"; // fnLowLevelNoProof41
				}
			}
			else if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_LIFE, MARK_OF_HEALER))
			{
				takeItems(player, -1, MARK_OF_DUTY, MARK_OF_LIFE, MARK_OF_HEALER);
				player.setClassId(TEMPLE_KNIGHT);
				player.setBaseClass(TEMPLE_KNIGHT);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-66.htm"; // fnAfterClassChange41
			}
			else
			{
				htmltext = "30109-67.htm"; // fnNoProof41
			}
		}
		else if ((classId == SWORDSINGER) && (player.getClassId() == ClassId.ELVEN_KNIGHT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_LIFE, MARK_OF_DUELIST))
				{
					htmltext = "30109-68.htm"; // fnLowLevel42
				}
				else
				{
					htmltext = "30109-69.htm"; // fnLowLevelNoProof42
				}
			}
			else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_LIFE, MARK_OF_DUELIST))
			{
				takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_LIFE, MARK_OF_DUELIST);
				player.setClassId(SWORDSINGER);
				player.setBaseClass(SWORDSINGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-70.htm"; // fnAfterClassChange42
			}
			else
			{
				htmltext = "30109-71.htm"; // fnNoProof42
			}
		}
		else if ((classId == PLAINS_WALKER) && (player.getClassId() == ClassId.ELVEN_SCOUT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SEARCHER))
				{
					htmltext = "30109-72.htm"; // fnLowLevel51
				}
				else
				{
					htmltext = "30109-73.htm"; // fnLowLevelNoProof51
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SEARCHER))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SEARCHER);
				player.setClassId(PLAINS_WALKER);
				player.setBaseClass(PLAINS_WALKER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-74.htm"; // fnAfterClassChange51
			}
			else
			{
				htmltext = "30109-75.htm"; // fnNoProof51
			}
		}
		else if ((classId == SILVER_RANGER) && (player.getClassId() == ClassId.ELVEN_SCOUT))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SAGITTARIUS))
				{
					htmltext = "30109-76.htm"; // fnLowLevel52
				}
				else
				{
					htmltext = "30109-77.htm"; // fnLowLevelNoProof52
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SAGITTARIUS))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_LIFE, MARK_OF_SAGITTARIUS);
				player.setClassId(SILVER_RANGER);
				player.setBaseClass(SILVER_RANGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30109-78.htm"; // fnAfterClassChange52
			}
			else
			{
				htmltext = "30109-79.htm"; // fnNoProof52
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FIGHTER_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_FALL_CLASS) || player.isInCategory(CategoryType.ELF_FALL_CLASS)))
		{
			htmltext = "30109-01.htm"; // fnYouAreFourthClass
		}
		else if (player.isInCategory(CategoryType.FIGHTER_GROUP) && (player.isInCategory(CategoryType.HUMAN_FALL_CLASS) || player.isInCategory(CategoryType.ELF_FALL_CLASS)))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.WARRIOR) || (classId == ClassId.GLADIATOR) || (classId == ClassId.WARLORD))
			{
				htmltext = "30109-02.htm"; // fnClassList1
			}
			else if ((classId == ClassId.KNIGHT) || (classId == ClassId.PALADIN) || (classId == ClassId.DARK_AVENGER))
			{
				htmltext = "30109-09.htm"; // fnClassList2
			}
			else if ((classId == ClassId.ROGUE) || (classId == ClassId.TREASURE_HUNTER) || (classId == ClassId.HAWKEYE))
			{
				htmltext = "30109-16.htm"; // fnClassList3
			}
			else if ((classId == ClassId.ELVEN_KNIGHT) || (classId == ClassId.TEMPLE_KNIGHT) || (classId == ClassId.SWORDSINGER))
			{
				htmltext = "30109-23.htm"; // fnClassList4
			}
			else if ((classId == ClassId.ELVEN_SCOUT) || (classId == ClassId.PLAINS_WALKER) || (classId == ClassId.SILVER_RANGER))
			{
				htmltext = "30109-30.htm"; // fnClassList5
			}
			else
			{
				htmltext = "30109-37.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30109-38.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static ElfHumanFighterChange2 provider()
	{
		return new ElfHumanFighterChange2();
	}
}
