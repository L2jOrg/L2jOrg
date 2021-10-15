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
package org.l2j.scripts.village.master.job.orc.first;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Orc class transfer AI.
 * @author Adry_85
 */
public final class OrcChange1 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30500, // Osborn
		30505, // Drikus
		30508, // Castor
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int MARK_OF_RAIDER = 1592;
	private static final int KHAVATARI_TOTEM = 1615;
	private static final int MASK_OF_MEDIUM = 1631;
	
	private OrcChange1()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		return switch (event) {
			case "30500-01.htm", "30500-02.htm", "30500-03.htm", "30500-04.htm", "30500-05.htm", "30500-06.htm", "30500-07.htm", "30500-08.htm", "30505-01.htm", "30505-02.htm",
				 "30505-03.htm", "30505-04.htm", "30505-05.htm", "30505-06.htm", "30505-07.htm", "30505-08.htm", "30508-01.htm", "30508-02.htm", "30508-03.htm", "30508-04.htm",
				 "30508-05.htm", "30508-06.htm", "30508-07.htm", "30508-08.htm", "32097-01.htm", "32097-02.htm", "32097-03.htm", "32097-04.htm", "32097-05.htm", "32097-06.htm",
					"32097-07.htm", "32097-08.htm" -> event;
			case "45", "47", "50" ->  classChangeRequested(player, npc, Integer.parseInt(event));
			default -> null;
		};
	}
	
	private String classChangeRequested(Player player, Npc npc, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmltext = npc.getId() + "-09.htm";
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = npc.getId() + "-10.htm";
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmltext = "30500-24.htm";
		} else if (classId == 45 && player.getClassId() == ClassId.ORC_FIGHTER) {
			htmltext = changeClassToRaider(player, npc);
		} else if (classId == 47 && player.getClassId() == ClassId.ORC_FIGHTER) {
			htmltext = changeClassToMonk(player, npc);
		} else if (classId == 50 && player.getClassId() == ClassId.ORC_MAGE) {
			htmltext = changeClassToShaman(player, npc);
		}
		return htmltext;
	}

	private String changeClassToShaman(Player player, Npc npc) {
		String htmlText;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, MASK_OF_MEDIUM))
			{
				htmlText = npc.getId() + "-19.htm";
			}
			else
			{
				htmlText = npc.getId() + "-20.htm";
			}
		}
		else if (hasQuestItems(player, MASK_OF_MEDIUM))
		{
			takeItems(player, MASK_OF_MEDIUM, -1);
			player.setClassId(50);
			player.setBaseClass(50);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmlText = npc.getId() + "-22.htm"; // fnAfterClassChange21
		}
		else
		{
			htmlText = npc.getId() + "-21.htm"; // fnNoProof21
		}
		return htmlText;
	}

	private String changeClassToMonk(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, KHAVATARI_TOTEM))
			{
				htmltext = npc.getId() + "-15.htm";
			}
			else
			{
				htmltext = npc.getId() + "-16.htm"; // fnLowLevelNoProof12
			}
		}
		else if (hasQuestItems(player, KHAVATARI_TOTEM))
		{
			takeItems(player, KHAVATARI_TOTEM, -1);
			player.setClassId(47);
			player.setBaseClass(47);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-18.htm"; // fnAfterClassChange12
		}
		else
		{
			htmltext = npc.getId() + "-17.htm"; // fnNoProof12
		}
		return htmltext;
	}

	private String changeClassToRaider(Player player, Npc npc) {
		String htmltext;
		if (player.getLevel() < 20)
		{
			if (hasQuestItems(player, MARK_OF_RAIDER))
			{
				htmltext = npc.getId() + "-11.htm";
			}
			else
			{
				htmltext = npc.getId() + "-12.htm";
			}
		}
		else if (hasQuestItems(player, MARK_OF_RAIDER))
		{
			takeItems(player, MARK_OF_RAIDER, -1);
			player.setClassId(45);
			player.setBaseClass(45);
			player.broadcastUserInfo();
			giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
			htmltext = npc.getId() + "-14.htm";
		}
		else
		{
			htmltext = npc.getId() + "-13.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.getRace() == Race.ORC)
		{
			if (player.isInCategory(CategoryType.FIGHTER_GROUP))
			{
				htmltext = npc.getId() + "-01.htm"; // fnClassList1
			}
			else if (player.isInCategory(CategoryType.MAGE_GROUP))
			{
				htmltext = npc.getId() + "-06.htm"; // fnClassList2
			}
		}
		else
		{
			htmltext = npc.getId() + "-23.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static OrcChange1 provider()
	{
		return new OrcChange1();
	}
}
