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
package village.master.DarkElfChange1;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;

import static org.l2j.commons.util.Util.isDigit;

/**
 * Dark Elven Change Part 1.
 * @author nonom
 */
public final class DarkElfChange1 extends AbstractNpcAI
{
	// NPCs
	private static int[] NPCS =
	{
		30290, // Xenos
		30297, // Tobias
		30462, // Tronix
	};
	// Items
	private static int GAZE_OF_ABYSS = 1244;
	private static int IRON_HEART = 1252;
	private static int JEWEL_OF_DARKNESS = 1261;
	private static int ORB_OF_ABYSS = 1270;
	// Rewards
	private static int SHADOW_WEAPON_COUPON_DGRADE = 8869;
	// @formatter:off
	private static int[][] CLASSES = 
	{
		{ 32, 31, 15, 16, 17, 18, GAZE_OF_ABYSS }, // PK
		{ 35, 31, 19, 20, 21, 22, IRON_HEART }, // AS
		{ 39, 38, 23, 24, 25, 26, JEWEL_OF_DARKNESS }, // DW
		{ 42, 38, 27, 28, 29, 30, ORB_OF_ABYSS }, // SO
	};
	// @formatter:on
	private DarkElfChange1()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (isDigit(event))
		{
			final int i = Integer.valueOf(event);
			final ClassId cid = player.getClassId();
			if ((cid.getRace() == Race.DARK_ELF) && (cid.getId() == CLASSES[i][1]))
			{
				int suffix;
				final boolean item = hasQuestItems(player, CLASSES[i][6]);
				if (player.getLevel() < 20)
				{
					suffix = (!item) ? CLASSES[i][2] : CLASSES[i][3];
				}
				else
				{
					if (!item)
					{
						suffix = CLASSES[i][4];
					}
					else
					{
						suffix = CLASSES[i][5];
						giveItems(player, SHADOW_WEAPON_COUPON_DGRADE, 15);
						takeItems(player, CLASSES[i][6], -1);
						player.setClassId(CLASSES[i][0]);
						player.setBaseClass(CLASSES[i][0]);
						playSound(player, QuestSound.ITEMSOUND_QUEST_FANFARE_2);
						player.broadcastUserInfo();
					}
				}
				event = npc.getId() + "-" + suffix + ".html";
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		if (player.isSubClassActive())
		{
			return htmltext;
		}
		
		final ClassId cid = player.getClassId();
		if (cid.getRace() == Race.DARK_ELF)
		{
			switch (cid)
			{
				case DARK_FIGHTER:
				{
					htmltext = npc.getId() + "-01.html";
					break;
				}
				case DARK_MAGE:
				{
					htmltext = npc.getId() + "-08.html";
					break;
				}
				default:
				{
					if (cid.level() == 1)
					{
						// first occupation change already made
						return npc.getId() + "-32.html";
					}
					else if (cid.level() >= 2)
					{
						// second/third occupation change already made
						return npc.getId() + "-31.html";
					}
				}
			}
		}
		else
		{
			htmltext = npc.getId() + "-33.html"; // other races
		}
		return htmltext;
	}
	
	public static DarkElfChange1 provider()
	{
		return new DarkElfChange1();
	}
}
