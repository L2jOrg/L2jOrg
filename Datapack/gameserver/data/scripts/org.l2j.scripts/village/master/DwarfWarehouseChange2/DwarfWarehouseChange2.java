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
package village.master.DwarfWarehouseChange2;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfWarehouseChange2 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
	{
		30511, // Gesto
		30676, // Croop
		30685, // Baxt
		30845, // Klump
		30894, // Natools
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_SEARCHER = 2809; // proof11z
	private static final int MARK_OF_GUILDSMAN = 3119; // proof11x
	private static final int MARK_OF_PROSPERITY = 3238; // proof11y
	// Class
	private static final int BOUNTY_HUNTER = 55;
	
	private DwarfWarehouseChange2()
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
			case "30511-03.htm": // master_lv3_ware006fa
			case "30511-04.htm": // master_lv3_ware007fa
			case "30511-05.htm": // master_lv3_ware007fat
			{
				htmltext = event;
				break;
			}
			case "55":
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
			htmltext = "30511-08.htm"; // fnYouAreThirdClass
		}
		else if ((classId == BOUNTY_HUNTER) && (player.getClassId() == ClassId.SCAVENGER))
		{
			if (player.getLevel() < 40)
			{
				if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER))
				{
					htmltext = "30511-09.htm"; // fnLowLevel11
				}
				else
				{
					htmltext = "30511-10.htm"; // fnLowLevelNoProof11
				}
			}
			else if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER))
			{
				takeItems(player, -1, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER);
				player.setClassId(BOUNTY_HUNTER);
				player.setBaseClass(BOUNTY_HUNTER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30511-11.htm"; // fnAfterClassChange11
			}
			else
			{
				htmltext = "30511-12.htm"; // fnNoProof11
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP))
		{
			htmltext = "30511-01.htm"; // fnYouAreFourthClass
		}
		else if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.SCAVENGER) || (classId == ClassId.BOUNTY_HUNTER))
			{
				htmltext = "30511-02.htm"; // fnClassList1
			}
			else
			{
				htmltext = "30511-06.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30511-07.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static DwarfWarehouseChange2 provider()
	{
		return new DwarfWarehouseChange2();
	}
}
