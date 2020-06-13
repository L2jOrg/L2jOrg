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
package village.master.ElfHumanWizardChange2;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * Elf Human class transfer AI.
 * @author Adry_85
 */
public final class ElfHumanWizardChange2 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
	{
		30115, // Jurek
		30174, // Arkenias
		30176, // Valleria
		30694, // Scraide
		30854, // Drikiyan
		31587, // Halaster
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_SCHOLAR = 2674; // proof11x, proof12x, proof13x, proof21x, proof22x
	private static final int MARK_OF_TRUST = 2734; // proof11y, proof12y, proof13y
	private static final int MARK_OF_MAGUS = 2840; // proof11z, proof21z
	private static final int MARK_OF_WITCHCRAFT = 3307; // proof12z
	private static final int MARK_OF_SUMMONER = 3336; // proof13z, proof22z
	private static final int MARK_OF_LIFE = 3140; // proof21y, proof22y
	// Classes
	private static final int SORCERER = 12;
	private static final int NECROMANCER = 13;
	private static final int WARLOCK = 14;
	private static final int SPELLSINGER = 27;
	private static final int ELEMENTAL_SUMMONER = 28;
	
	private ElfHumanWizardChange2()
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
			case "30115-02.htm": // master_lv3_hew003h
			case "30115-03.htm": // master_lv3_hew006ha
			case "30115-04.htm": // master_lv3_hew007ha
			case "30115-05.htm": // master_lv3_hew007hat
			case "30115-06.htm": // master_lv3_hew006hb
			case "30115-07.htm": // master_lv3_hew007hb
			case "30115-08.htm": // master_lv3_hew007hbt
			case "30115-09.htm": // master_lv3_hew006hc
			case "30115-10.htm": // master_lv3_hew007hc
			case "30115-11.htm": // master_lv3_hew007hct
			case "30115-12.htm": // master_lv3_hew003e
			case "30115-13.htm": // master_lv3_hew006ea
			case "30115-14.htm": // master_lv3_hew007ea
			case "30115-15.htm": // master_lv3_hew007eat
			case "30115-16.htm": // master_lv3_hew006eb
			case "30115-17.htm": // master_lv3_hew007eb
			case "30115-18.htm": // master_lv3_hew007ebt
			{
				htmltext = event;
				break;
			}
			case "12":
			case "13":
			case "14":
			case "27":
			case "28":
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
			htmltext = "30115-21.htm"; // fnYouAreThirdClass
		}
		else if ((classId == SORCERER) && (player.getClassId() == ClassId.WIZARD))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_MAGUS))
				{
					htmltext = "30115-22.htm"; // fnLowLevel11
				}
				else
				{
					htmltext = "30115-23.htm"; // fnLowLevelNoProof11
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_MAGUS))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_MAGUS);
				player.setClassId(SORCERER);
				player.setBaseClass(SORCERER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30115-24.htm"; // fnAfterClassChange11
			}
			else
			{
				htmltext = "30115-25.htm"; // fnNoProof11
			}
		}
		else if ((classId == NECROMANCER) && (player.getClassId() == ClassId.WIZARD))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_WITCHCRAFT))
				{
					htmltext = "30115-26.htm"; // fnLowLevel12
				}
				else
				{
					htmltext = "30115-27.htm"; // fnLowLevelNoProof12
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_WITCHCRAFT))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_WITCHCRAFT);
				player.setClassId(NECROMANCER);
				player.setBaseClass(NECROMANCER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30115-28.htm"; // fnAfterClassChange12
			}
			else
			{
				htmltext = "30115-29.htm"; // fnNoProof12
			}
		}
		else if ((classId == WARLOCK) && (player.getClassId() == ClassId.WIZARD))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_SUMMONER))
				{
					htmltext = "30115-30.htm"; // fnLowLevel13
				}
				else
				{
					htmltext = "30115-31.htm"; // fnLowLevelNoProof13
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_SUMMONER))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_TRUST, MARK_OF_SUMMONER);
				player.setClassId(WARLOCK);
				player.setBaseClass(WARLOCK);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30115-32.htm"; // fnAfterClassChange13
			}
			else
			{
				htmltext = "30115-33.htm"; // fnNoProof13
			}
		}
		else if ((classId == SPELLSINGER) && (player.getClassId() == ClassId.ELVEN_WIZARD))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_MAGUS))
				{
					htmltext = "30115-34.htm"; // fnLowLevel21
				}
				else
				{
					htmltext = "30115-35.htm"; // fnLowLevelNoProof21
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_MAGUS))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_MAGUS);
				player.setClassId(SPELLSINGER);
				player.setBaseClass(SPELLSINGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30115-36.htm"; // fnAfterClassChange21
			}
			else
			{
				htmltext = "30115-37.htm"; // fnNoProof21
			}
		}
		else if ((classId == ELEMENTAL_SUMMONER) && (player.getClassId() == ClassId.ELVEN_WIZARD))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_SUMMONER))
				{
					htmltext = "30115-38.htm"; // fnLowLevel22
				}
				else
				{
					htmltext = "30115-39.htm"; // fnLowLevelNoProof22
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_SUMMONER))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_LIFE, MARK_OF_SUMMONER);
				player.setClassId(ELEMENTAL_SUMMONER);
				player.setBaseClass(ELEMENTAL_SUMMONER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30115-40.htm"; // fnAfterClassChange22
			}
			else
			{
				htmltext = "30115-41.htm"; // fnNoProof22
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.WIZARD_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_MALL_CLASS) || player.isInCategory(CategoryType.ELF_MALL_CLASS)))
		{
			htmltext = "30115-01.htm"; // fnYouAreFourthClass
		}
		else if (player.isInCategory(CategoryType.WIZARD_GROUP) && (player.isInCategory(CategoryType.HUMAN_MALL_CLASS) || player.isInCategory(CategoryType.ELF_MALL_CLASS)))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.WIZARD) || (classId == ClassId.SORCERER) || (classId == ClassId.NECROMANCER) || (classId == ClassId.WARLOCK))
			{
				htmltext = "30115-02.htm"; // fnClassList1
			}
			else if ((classId == ClassId.ELVEN_WIZARD) || (classId == ClassId.SPELLSINGER) || (classId == ClassId.ELEMENTAL_SUMMONER))
			{
				htmltext = "30115-12.htm"; // fnClassList2
			}
			else
			{
				htmltext = "30115-19.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30115-20.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static ElfHumanWizardChange2 provider()
	{
		return new ElfHumanWizardChange2();
	}
}
