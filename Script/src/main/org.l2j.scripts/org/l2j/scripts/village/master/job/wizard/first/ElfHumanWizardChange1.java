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
package org.l2j.scripts.village.master.job.wizard.first;

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
public final class ElfHumanWizardChange1 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30037, // Levian
		30070, // Sylvain
		30289, // Raymond
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int MARK_OF_FAITH = 1201;
	private static final int ETERNITY_DIAMOND = 1230;
	private static final int LEAF_OF_ORACLE = 1235;
	private static final int BEAD_OF_SEASON = 1292;
	// Classes
	private static final int WIZARD = 11;
	private static final int CLERIC = 15;
	private static final int ELVEN_WIZARD = 26;
	private static final int ORACLE = 29;
	
	private ElfHumanWizardChange1()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		String htmltext = null;
		switch (event) {
			case "30037-01.htm", "30037-02.htm", "30037-03.htm", "30037-04.htm", "30037-05.htm", "30037-06.htm", "30037-07.htm", "30037-08.htm", "30037-09.htm", "30037-10.htm", "30037-11.htm",
					"30037-12.htm", "30037-13.htm", "30037-14.htm", "30070-01.htm", "30070-02.htm", "30070-03.htm", "30070-04.htm", "30070-05.htm", "30070-06.htm", "30070-07.htm", "30070-08.htm",
					"30070-09.htm", "30070-10.htm", "30070-11.htm", "30070-12.htm", "30070-13.htm", "30070-14.htm", "30289-01.htm", "30289-02.htm", "30289-03.htm", "30289-04.htm", "30289-05.htm",
					"30289-06.htm", "30289-07.htm", "30289-08.htm", "30289-09.htm", "30289-10.htm", "30289-11.htm", "30289-12.htm", "30289-13.htm", "30289-14.htm", "32095-01.htm", "32095-02.htm",
					"32095-03.htm", "32095-04.htm", "32095-05.htm", "32095-06.htm", "32095-07.htm", "32095-08.htm", "32095-09.htm", "32095-10.htm", "32095-11.htm", "32095-12.htm", "32095-13.htm",
					"32095-14.htm", "32098-01.htm", "32098-02.htm", "32098-03.htm", "32098-04.htm", "32098-05.htm", "32098-06.htm", "32098-07.htm", "32098-08.htm", "32098-09.htm", "32098-10.htm",
					"32098-11.htm", "32098-12.htm", "32098-13.htm", "32098-14.htm" -> htmltext = event;
			case "11", "15", "26", "29" -> htmltext = classChangeRequested(player, npc, Integer.parseInt(event));
		}
		return htmltext;
	}
	
	private String classChangeRequested(Player player, Npc npc, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmltext = npc.getId() + "-16.htm";
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = npc.getId() + "-17.htm";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmltext = "30037-34.htm";
		} else if (classId == WIZARD && player.getClassId() == ClassId.MAGE) {
			htmltext = changeClassToWizard(player, npc);
		} else if (classId == CLERIC && player.getClassId() == ClassId.MAGE) {
			htmltext = changeClassToCleric(player, npc);
		} else if (classId == ELVEN_WIZARD && player.getClassId() == ClassId.ELVEN_MAGE) {
			htmltext = changeClassToElvenWizard(player, npc);
		} else if (classId == ORACLE && player.getClassId() == ClassId.ELVEN_MAGE) {
			htmltext = changeClassToOracle(player, npc);
		}
		return htmltext;
	}

	private String changeClassToOracle(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, LEAF_OF_ORACLE))
			{
				htmltext = npc.getId() + "-30.htm"; // fnLowLevel22
			}
			else
			{
				htmltext = npc.getId() + "-31.htm"; // fnLowLevelNoProof22
			}
		}
		else if (hasQuestItems(player, LEAF_OF_ORACLE))
		{
			takeItems(player, LEAF_OF_ORACLE, -1);
			player.setClassId(ORACLE);
			player.setBaseClass(ORACLE);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-32.htm"; // fnAfterClassChange22
		}
		else
		{
			htmltext = npc.getId() + "-33.htm"; // fnNoProof22
		}
		return htmltext;
	}

	private String changeClassToElvenWizard(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, ETERNITY_DIAMOND))
			{
				htmltext = npc.getId() + "-26.htm"; // fnLowLevel21
			}
			else
			{
				htmltext = npc.getId() + "-27.htm"; // fnLowLevelNoProof21
			}
		}
		else if (hasQuestItems(player, ETERNITY_DIAMOND))
		{
			takeItems(player, ETERNITY_DIAMOND, -1);
			player.setClassId(ELVEN_WIZARD);
			player.setBaseClass(ELVEN_WIZARD);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-28.htm"; // fnAfterClassChange21
		}
		else
		{
			htmltext = npc.getId() + "-29.htm"; // fnNoProof21
		}
		return htmltext;
	}

	private String changeClassToCleric(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, MARK_OF_FAITH))
			{
				htmltext = npc.getId() + "-22.htm"; // fnLowLevel12
			}
			else
			{
				htmltext = npc.getId() + "-23.htm"; // fnLowLevelNoProof12
			}
		}
		else if (hasQuestItems(player, MARK_OF_FAITH))
		{
			takeItems(player, MARK_OF_FAITH, -1);
			player.setClassId(CLERIC);
			player.setBaseClass(CLERIC);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-24.htm"; // fnAfterClassChange12
		}
		else
		{
			htmltext = npc.getId() + "-25.htm"; // fnNoProof12
		}
		return htmltext;
	}

	private String changeClassToWizard(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, BEAD_OF_SEASON))
			{
				htmltext = npc.getId() + "-18.htm"; // fnLowLevel11
			}
			else
			{
				htmltext = npc.getId() + "-19.htm"; // fnLowLevelNoProof11
			}
		}
		else if (hasQuestItems(player, BEAD_OF_SEASON))
		{
			takeItems(player, BEAD_OF_SEASON, -1);
			player.setClassId(WIZARD);
			player.setBaseClass(WIZARD);
			// SystemMessage and cast skill is done by setClassId
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-20.htm"; // fnAfterClassChange11
		}
		else
		{
			htmltext = npc.getId() + "-21.htm"; // fnNoProof11
		}
		return htmltext;
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext;
		final Race playerRace = player.getRace();
		if (player.isInCategory(CategoryType.MAGE_GROUP) && ((playerRace == Race.HUMAN) || (playerRace == Race.ELF)))
		{
			if (playerRace == Race.HUMAN)
			{
				htmltext = npc.getId() + "-01.htm"; // fnClassList1
			}
			else
			{
				htmltext = npc.getId() + "-08.htm"; // fnClassList2
			}
		}
		else
		{
			htmltext = npc.getId() + "-15.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static ElfHumanWizardChange1 provider()
	{
		return new ElfHumanWizardChange1();
	}
}
