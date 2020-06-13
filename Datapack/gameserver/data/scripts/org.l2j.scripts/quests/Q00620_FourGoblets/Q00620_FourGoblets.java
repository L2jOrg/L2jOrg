/*
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
package quests.Q00620_FourGoblets;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.MathUtil;


/**
 * Zoey76: TODO: Use Location DTO instead of array of int.
 * @author sandman
 */
public class Q00620_FourGoblets extends Quest
{
	// NPCs
	private static final int NAMELESS_SPIRIT = 31453;
	private static final int GHOST_OF_WIGOTH_1 = 31452;
	private static final int GHOST_OF_WIGOTH_2 = 31454;
	private static final int CONQ_SM = 31921;
	private static final int EMPER_SM = 31922;
	private static final int SAGES_SM = 31923;
	private static final int JUDGE_SM = 31924;
	private static final int GHOST_CHAMBERLAIN_1 = 31919;
	private static final int GHOST_CHAMBERLAIN_2 = 31920;
	private static final int[] NPCS =
	{
		NAMELESS_SPIRIT,
		GHOST_OF_WIGOTH_1,
		GHOST_OF_WIGOTH_2,
		CONQ_SM,
		EMPER_SM,
		SAGES_SM,
		JUDGE_SM,
		GHOST_CHAMBERLAIN_1,
		GHOST_CHAMBERLAIN_2
	};
	
	// Reward
	private static final int ANTIQUE_BROOCH = 7262;
	// Items
	private static final int ENTRANCE_PASS = 7075;
	private static final int GRAVE_PASS = 7261;
	private static final int[] GOBLETS =
	{
		7256,
		7257,
		7258,
		7259
	};
	private static final int BOSS_1 = 25339;
	private static final int BOSS_2 = 25342;
	private static final int BOSS_3 = 25346;
	private static final int BOSS_4 = 25349;
	
	private static final int RELIC = 7254;
	private static final int SEALED_BOX = 7255;
	private static final int[] QI =
	{
		//ANTIQUE_BROOCH,
		//SEALED_BOX,
		7256,
		7257,
		7258,
		7259,
		//GRAVE_PASS,
		//ENTRANCE_PASS
	};
	// Misc
	private static final int MIN_LEVEL = 74;
	private static final int MAX_LEVEL = 80;
	
	public Q00620_FourGoblets()
	{
		super(620);
		addStartNpc(NAMELESS_SPIRIT);
		
		for (int i : NPCS)
		{
			addTalkId(i);
		}
		
		for (int j : QI)
		{
			registerQuestItems(j);
		}
		
		for (int id = 18120; id <= 18256; id++)
		{
			addKillId(id, BOSS_1, BOSS_2, BOSS_3, BOSS_4);
		}
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "31453-12.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "accept":
			{
				if ((qs.getPlayer().getLevel() >= 74) && ((qs.getPlayer().getLevel() <= 80)))
				{
					qs.startQuest();
					htmltext = "31453-13.htm";
					giveItems(player, ENTRANCE_PASS, 1);
				}
				else
				{
					htmltext = "31453-12.htm";
				}
				break;
			}
			case "11":
			{
				if (getQuestItemsCount(player, SEALED_BOX) >= 1)
				{
					htmltext = "31454-13.htm";
					takeItems(player, SEALED_BOX, 1);
					int reward = 0;
					final int rnd = Rnd.get(5);
					if (rnd == 0)
					{
						giveItems(player, 57, 10000);
						reward = 1;
					}
					else if (rnd == 1)
					{
						if (Rnd.get(1000) < 848)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 43)
							{
								giveItems(player, 1884, 42);
							}
							else if (i < 66)
							{
								giveItems(player, 1895, 36);
							}
							else if (i < 184)
							{
								giveItems(player, 1876, 4);
							}
							else if (i < 250)
							{
								giveItems(player, 1881, 6);
							}
							else if (i < 287)
							{
								giveItems(player, 5549, 8);
							}
							else if (i < 484)
							{
								giveItems(player, 1874, 1);
							}
							else if (i < 681)
							{
								giveItems(player, 1889, 1);
							}
							else if (i < 799)
							{
								giveItems(player, 1877, 1);
							}
							else if (i < 902)
							{
								giveItems(player, 1894, 1);
							}
							else
							{
								giveItems(player, 4043, 1);
							}
						}
						else if (Rnd.get(1000) < 323)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 335)
							{
								giveItems(player, 1888, 1);
							}
							else if (i < 556)
							{
								giveItems(player, 4040, 1);
							}
							else if (i < 725)
							{
								giveItems(player, 1890, 1);
							}
							else if (i < 872)
							{
								giveItems(player, 5550, 1);
							}
							else if (i < 962)
							{
								giveItems(player, 1893, 1);
							}
							else if (i < 986)
							{
								giveItems(player, 4046, 1);
							}
							else
							{
								giveItems(player, 4048, 1);
							}
						}
					}
					else if (rnd == 2)
					{
						if (Rnd.get(1000) < 847)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 148)
							{
								giveItems(player, 1878, 8);
							}
							else if (i < 175)
							{
								giveItems(player, 1882, 24);
							}
							else if (i < 273)
							{
								giveItems(player, 1879, 4);
							}
							else if (i < 322)
							{
								giveItems(player, 1880, 6);
							}
							else if (i < 357)
							{
								giveItems(player, 1885, 6);
							}
							else if (i < 554)
							{
								giveItems(player, 1875, 1);
							}
							else if (i < 685)
							{
								giveItems(player, 1883, 1);
							}
							else if (i < 803)
							{
								giveItems(player, 5220, 1);
							}
							else if (i < 901)
							{
								giveItems(player, 4039, 1);
							}
							else
							{
								giveItems(player, 4044, 1);
							}
						}
						else if (Rnd.get(1000) < 251)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 350)
							{
								giveItems(player, 1887, 1);
							}
							else if (i < 587)
							{
								giveItems(player, 4042, 1);
							}
							else if (i < 798)
							{
								giveItems(player, 1886, 1);
							}
							else if (i < 922)
							{
								giveItems(player, 4041, 1);
							}
							else if (i < 966)
							{
								giveItems(player, 1892, 1);
							}
							else if (i < 996)
							{
								giveItems(player, 1891, 1);
							}
							else
							{
								giveItems(player, 4047, 1);
							}
						}
					}
					else if (rnd == 3)
					{
						if (Rnd.get(1000) < 31)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 223)
							{
								giveItems(player, 730, 1);
							}
							else if (i < 893)
							{
								giveItems(player, 948, 1);
							}
							else
							{
								giveItems(player, 960, 1);
							}
						}
						else if (Rnd.get(1000) < 50)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 202)
							{
								giveItems(player, 729, 1);
							}
							else if (i < 928)
							{
								giveItems(player, 947, 1);
							}
							else
							{
								giveItems(player, 959, 1);
							}
						}
					}
					else if (rnd == 4)
					{
						if (Rnd.get(1000) < 329)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 88)
							{
								giveItems(player, 6698, 1);
							}
							else if (i < 185)
							{
								giveItems(player, 6699, 1);
							}
							else if (i < 238)
							{
								giveItems(player, 6700, 1);
							}
							else if (i < 262)
							{
								giveItems(player, 6701, 1);
							}
							else if (i < 292)
							{
								giveItems(player, 6702, 1);
							}
							else if (i < 356)
							{
								giveItems(player, 6703, 1);
							}
							else if (i < 420)
							{
								giveItems(player, 6704, 1);
							}
							else if (i < 482)
							{
								giveItems(player, 6705, 1);
							}
							else if (i < 554)
							{
								giveItems(player, 6706, 1);
							}
							else if (i < 576)
							{
								giveItems(player, 6707, 1);
							}
							else if (i < 640)
							{
								giveItems(player, 6708, 1);
							}
							else if (i < 704)
							{
								giveItems(player, 6709, 1);
							}
							else if (i < 777)
							{
								giveItems(player, 6710, 1);
							}
							else if (i < 799)
							{
								giveItems(player, 6711, 1);
							}
							else if (i < 863)
							{
								giveItems(player, 6712, 1);
							}
							else if (i < 927)
							{
								giveItems(player, 6713, 1);
							}
							else
							{
								giveItems(player, 6714, 1);
							}
						}
						else if (Rnd.get(1000) < 54)
						{
							reward = 1;
							final int i = Rnd.get(1000);
							if (i < 100)
							{
								giveItems(player, 6688, 1);
							}
							else if (i < 198)
							{
								giveItems(player, 6689, 1);
							}
							else if (i < 298)
							{
								giveItems(player, 6690, 1);
							}
							else if (i < 398)
							{
								giveItems(player, 6691, 1);
							}
							else if (i < 499)
							{
								giveItems(player, 7579, 1);
							}
							else if (i < 601)
							{
								giveItems(player, 6693, 1);
							}
							else if (i < 703)
							{
								giveItems(player, 6694, 1);
							}
							else if (i < 801)
							{
								giveItems(player, 6695, 1);
							}
							else if (i < 902)
							{
								giveItems(player, 6696, 1);
							}
							else
							{
								giveItems(player, 6697, 1);
							}
						}
					}
					else if (reward == 0)
					{
						if (Rnd.nextBoolean())
						{
							htmltext = "31454-14.htm";
						}
						else
						{
							htmltext = "31454-15.htm";
						}
					}
				}
				break;
			}
			case "12":
			{
				if ((getQuestItemsCount(player, GOBLETS[0]) >= 1) && (getQuestItemsCount(player, GOBLETS[1]) >= 1) && (getQuestItemsCount(player, GOBLETS[2]) >= 1) && (getQuestItemsCount(player, GOBLETS[3]) >= 1))
				{
					takeItems(player, GOBLETS[0], -1);
					takeItems(player, GOBLETS[1], -1);
					takeItems(player, GOBLETS[2], -1);
					takeItems(player, GOBLETS[3], -1);
					if (getQuestItemsCount(player, ANTIQUE_BROOCH) < 1)
					{
						giveItems(player, ANTIQUE_BROOCH, 1);
					}
					qs.setCond(2, true);
					htmltext = "31453-16.htm";
				}
				else
				{
					htmltext = "31453-14.htm";
				}
				break;
			}
			case "13":
			{
				qs.exitQuest(true, true);
				htmltext = "31453-18.htm";
				break;
			}
			case "14":
			{
				htmltext = "31453-13.htm";
				if (qs.getCond() == 2)
				{
					htmltext = "31453-19.htm";
				}
				break;
			}
			case "15":
			{
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1)
				{
					qs.getPlayer().teleToLocation(178298, -84574, -7216);
					htmltext = null;
				}
				else if (getQuestItemsCount(player, GRAVE_PASS) >= 1)
				{
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(178298, -84574, -7216);
					htmltext = null;
				}
				else
				{
					htmltext = "31919-0.htm";
				}
				break;
			}
			case "16":
			{
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1)
				{
					qs.getPlayer().teleToLocation(186942, -75602, -2834);
					htmltext = null;
				}
				else if (getQuestItemsCount(player, GRAVE_PASS) >= 1)
				{
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(186942, -75602, -2834);
					htmltext = null;
				}
				else
				{
					htmltext = "31920-0.htm";
				}
				break;
			}
			case "17":
			{
				if (getQuestItemsCount(player, ANTIQUE_BROOCH) >= 1)
				{
					qs.getPlayer().teleToLocation(169590, -90218, -2914);
				}
				else
				{
					takeItems(player, GRAVE_PASS, 1);
					qs.getPlayer().teleToLocation(169590, -90218, -2914);
					htmltext = "31452-6.htm";
				}
				break;
			}
			case "18":
			{
				if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) < 3)
				{
					htmltext = "31452-3.htm";
				}
				else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) == 3)
				{
					htmltext = "31452-4.htm";
				}
				else if ((getQuestItemsCount(player, GOBLETS[0]) + getQuestItemsCount(player, GOBLETS[1]) + getQuestItemsCount(player, GOBLETS[2]) + getQuestItemsCount(player, GOBLETS[3])) >= 4)
				{
					htmltext = "31452-5.htm";
				}
				break;
			}
			case "19":
			{
				htmltext = "31919-3.htm";
				takeItems(player, SEALED_BOX, 1);
				int reward = 0;
				final int rnd = Rnd.get(5);
				if (rnd == 0)
				{
					giveItems(player, 57, 10000);
					reward = 1;
				}
				else if (rnd == 1)
				{
					if (Rnd.get(1000) < 848)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 43)
						{
							giveItems(player, 1884, 42);
						}
						else if (i < 66)
						{
							giveItems(player, 1895, 36);
						}
						else if (i < 184)
						{
							giveItems(player, 1876, 4);
						}
						else if (i < 250)
						{
							giveItems(player, 1881, 6);
						}
						else if (i < 287)
						{
							giveItems(player, 5549, 8);
						}
						else if (i < 484)
						{
							giveItems(player, 1874, 1);
						}
						else if (i < 681)
						{
							giveItems(player, 1889, 1);
						}
						else if (i < 799)
						{
							giveItems(player, 1877, 1);
						}
						else if (i < 902)
						{
							giveItems(player, 1894, 1);
						}
						else
						{
							giveItems(player, 4043, 1);
						}
					}
					else if (Rnd.get(1000) < 323)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 335)
						{
							giveItems(player, 1888, 1);
						}
						else if (i < 556)
						{
							giveItems(player, 4040, 1);
						}
						else if (i < 725)
						{
							giveItems(player, 1890, 1);
						}
						else if (i < 872)
						{
							giveItems(player, 5550, 1);
						}
						else if (i < 962)
						{
							giveItems(player, 1893, 1);
						}
						else if (i < 986)
						{
							giveItems(player, 4046, 1);
						}
						else
						{
							giveItems(player, 4048, 1);
						}
					}
				}
				else if (rnd == 2)
				{
					if (Rnd.get(1000) < 847)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 148)
						{
							giveItems(player, 1878, 8);
						}
						else if (i < 175)
						{
							giveItems(player, 1882, 24);
						}
						else if (i < 273)
						{
							giveItems(player, 1879, 4);
						}
						else if (i < 322)
						{
							giveItems(player, 1880, 6);
						}
						else if (i < 357)
						{
							giveItems(player, 1885, 6);
						}
						else if (i < 554)
						{
							giveItems(player, 1875, 1);
						}
						else if (i < 685)
						{
							giveItems(player, 1883, 1);
						}
						else if (i < 803)
						{
							giveItems(player, 5220, 1);
						}
						else if (i < 901)
						{
							giveItems(player, 4039, 1);
						}
						else
						{
							giveItems(player, 4044, 1);
						}
					}
					else if (Rnd.get(1000) < 251)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 350)
						{
							giveItems(player, 1887, 1);
						}
						else if (i < 587)
						{
							giveItems(player, 4042, 1);
						}
						else if (i < 798)
						{
							giveItems(player, 1886, 1);
						}
						else if (i < 922)
						{
							giveItems(player, 4041, 1);
						}
						else if (i < 966)
						{
							giveItems(player, 1892, 1);
						}
						else if (i < 996)
						{
							giveItems(player, 1891, 1);
						}
						else
						{
							giveItems(player, 4047, 1);
						}
					}
				}
				else if (rnd == 3)
				{
					if (Rnd.get(1000) < 31)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 223)
						{
							giveItems(player, 730, 1);
						}
						else if (i < 893)
						{
							giveItems(player, 948, 1);
						}
						else
						{
							giveItems(player, 960, 1);
						}
					}
					else if (Rnd.get(1000) < 5)
					{
						reward = 1;
					}
					final int i = Rnd.get(1000);
					if (i < 202)
					{
						giveItems(player, 729, 1);
					}
					else if (i < 928)
					{
						giveItems(player, 947, 1);
					}
					else
					{
						giveItems(player, 959, 1);
					}
				}
				else if (rnd == 4)
				{
					if (Rnd.get(1000) < 329)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 88)
						{
							giveItems(player, 6698, 1);
						}
						else if (i < 185)
						{
							giveItems(player, 6699, 1);
						}
						else if (i < 238)
						{
							giveItems(player, 6700, 1);
						}
						else if (i < 262)
						{
							giveItems(player, 6701, 1);
						}
						else if (i < 292)
						{
							giveItems(player, 6702, 1);
						}
						else if (i < 356)
						{
							giveItems(player, 6703, 1);
						}
						else if (i < 420)
						{
							giveItems(player, 6704, 1);
						}
						else if (i < 482)
						{
							giveItems(player, 6705, 1);
						}
						else if (i < 554)
						{
							giveItems(player, 6706, 1);
						}
						else if (i < 576)
						{
							giveItems(player, 6707, 1);
						}
						else if (i < 640)
						{
							giveItems(player, 6708, 1);
						}
						else if (i < 704)
						{
							giveItems(player, 6709, 1);
						}
						else if (i < 777)
						{
							giveItems(player, 6710, 1);
						}
						else if (i < 799)
						{
							giveItems(player, 6711, 1);
						}
						else if (i < 863)
						{
							giveItems(player, 6712, 1);
						}
						else if (i < 927)
						{
							giveItems(player, 6713, 1);
						}
						else
						{
							giveItems(player, 6714, 1);
						}
					}
					else if (Rnd.get(1000) < 54)
					{
						reward = 1;
						final int i = Rnd.get(1000);
						if (i < 100)
						{
							giveItems(player, 6688, 1);
						}
						else if (i < 198)
						{
							giveItems(player, 6689, 1);
						}
						else if (i < 298)
						{
							giveItems(player, 6690, 1);
						}
						else if (i < 398)
						{
							giveItems(player, 6691, 1);
						}
						else if (i < 499)
						{
							giveItems(player, 7579, 1);
						}
						else if (i < 601)
						{
							giveItems(player, 6693, 1);
						}
						else if (i < 703)
						{
							giveItems(player, 6694, 1);
						}
						else if (i < 801)
						{
							giveItems(player, 6695, 1);
						}
						else if (i < 902)
						{
							giveItems(player, 6696, 1);
						}
						else
						{
							giveItems(player, 6697, 1);
						}
					}
				}
				if (reward == 0)
				{
					if (Rnd.nextBoolean())
					{
						htmltext = "31919-4.htm";
					}
					else
					{
						htmltext = "31919-5.htm";
					}
				}
				break;
			}
			case "6881":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6883":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6885":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6887":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "7580":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6891":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6893":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6895":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6897":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
			case "6899":
			{
				takeItems(player, RELIC, 1000);
				giveItems(player, qs.getInt(event), 1);
				htmltext = "31454-17.htm";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (npc.getId())
		{
			case NAMELESS_SPIRIT:
			{
				if (qs.isCreated())
				{
					if ((qs.getPlayer().getLevel() >= 74) && ((qs.getPlayer().getLevel() <= 80)))
					{
						htmltext = "31453-1.htm";
					}
					else
					{
						htmltext = "31453-12.htm";
					}
				}
				else if (qs.getCond() == 1)
				{
					if ((getQuestItemsCount(talker, GOBLETS[0]) >= 1) && (getQuestItemsCount(talker, GOBLETS[1]) >= 1) && (getQuestItemsCount(talker, GOBLETS[2]) >= 1) && (getQuestItemsCount(talker, GOBLETS[3]) >= 1))
					{
						htmltext = "31453-15.htm";
					}
					else
					{
						htmltext = "31453-14.htm";
					}
				}
				else if (qs.getCond() == 2)
				{
					htmltext = "31453-17.htm";
				}
				break;
			}
			case GHOST_OF_WIGOTH_1:
			{
				if (qs.getCond() == 1)
				{
					if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) == 1)
					{
						htmltext = "31452-01.html";
					}
					else if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) > 1)
					{
						htmltext = "31452-02.html";
					}
				}
				else if (qs.getCond() == 2)
				{
					htmltext = "31452-02.html";
				}
				break;
			}
			case GHOST_OF_WIGOTH_2:
			{
				if (getQuestItemsCount(talker, RELIC) >= 1000)
				{
					if (getQuestItemsCount(talker, SEALED_BOX) >= 1)
					{
						if ((getQuestItemsCount(talker, GOBLETS[0]) >= 1) && (getQuestItemsCount(talker, GOBLETS[1]) >= 1) && (getQuestItemsCount(talker, GOBLETS[2]) >= 1) && (getQuestItemsCount(talker, GOBLETS[3]) >= 1))
						{
							htmltext = "31454-4.htm";
						}
						else if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) > 1)
						{
							htmltext = "31454-8.htm";
						}
						else
						{
							htmltext = "31454-12.htm";
						}
					}
					else if ((getQuestItemsCount(talker, GOBLETS[0]) >= 1) && (getQuestItemsCount(talker, GOBLETS[1]) >= 1) && (getQuestItemsCount(talker, GOBLETS[2]) >= 1) && ((getQuestItemsCount(talker, GOBLETS[3])) >= 1))
					{
						htmltext = "31454-3.htm";
					}
					else if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) > 1)
					{
						htmltext = "31454-7.htm";
					}
					else
					{
						htmltext = "31454-11.htm";
					}
				}
				else if (getQuestItemsCount(talker, SEALED_BOX) >= 1)
				{
					if ((getQuestItemsCount(talker, GOBLETS[0]) >= 1) && (getQuestItemsCount(talker, GOBLETS[1]) >= 1) && (getQuestItemsCount(talker, GOBLETS[2]) >= 1) && (getQuestItemsCount(talker, GOBLETS[3]) >= 1))
					{
						htmltext = "31454-2.htm";
					}
					else if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) > 1)
					{
						htmltext = "31454-6.htm";
					}
					else
					{
						htmltext = "31454-10.htm";
					}
				}
				else if ((getQuestItemsCount(talker, GOBLETS[0]) >= 1) && (getQuestItemsCount(talker, GOBLETS[1]) >= 1) && (getQuestItemsCount(talker, GOBLETS[2]) >= 1) && (getQuestItemsCount(talker, GOBLETS[3]) >= 1))
				{
					htmltext = "31454-1.htm";
				}
				else if ((getQuestItemsCount(talker, GOBLETS[0]) + getQuestItemsCount(talker, GOBLETS[1]) + getQuestItemsCount(talker, GOBLETS[2]) + getQuestItemsCount(talker, GOBLETS[3])) > 1)
				{
					htmltext = "31454-5.htm";
				}
				else
				{
					htmltext = "31454-9.htm";
				}
				break;
			}
			case CONQ_SM:
			{
				htmltext = "31921-E.htm";
				break;
			}
			case EMPER_SM:
			{
				htmltext = "31922-E.htm";
				break;
			}
			case SAGES_SM:
			{
				htmltext = "31923-E.htm";
				break;
			}
			case JUDGE_SM:
			{
				htmltext = "31924-E.htm";
				break;
			}
			case GHOST_CHAMBERLAIN_1:
			{
				htmltext = "31919-1.htm";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = killer.getQuestState(getName());
		final Player partyMember = getRandomPartyMember(killer, 3);
		final int npcId = npc.getId();
		if ((qs != null) && (qs.getCond() > 0) && (npcId >= 18120) && (npcId <= 18256))
		{
			if (Rnd.get(100) < 15)
			{
				giveItems(killer, SEALED_BOX, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (getQuestItemsCount(killer, GRAVE_PASS) < 1)
			{
				giveItems(killer, GRAVE_PASS, 1);
			}
			if (getQuestItemsCount(killer, RELIC) < 1000)
			{
				giveItems(killer, RELIC, 1);
			}
		}
		switch (npc.getId())
		{
			case BOSS_1:
			{
				if (partyMember == null)
				{
					return null;
				}
				if (!MathUtil.isInsideRadius3D(npc, partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[0]) < 1))
				{
					giveItems(partyMember, GOBLETS[0], 1);
				}
			}
				break;
			case BOSS_2:
			{
				if (partyMember == null)
				{
					return null;
				}
				if (!MathUtil.isInsideRadius3D(npc,partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[1]) < 1))
				{
					giveItems(partyMember, GOBLETS[1], 1);
				}
			}
				break;
			case BOSS_3:
			{
				if (partyMember == null)
				{
					return null;
				}
				if (!MathUtil.isInsideRadius3D(npc,partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[2]) < 1))
				{
					giveItems(partyMember, GOBLETS[2], 1);
				}
			}
				break;
			case BOSS_4:
			{
				if (partyMember == null)
				{
					return null;
				}
				if (!MathUtil.isInsideRadius3D(npc,partyMember, 1000) && (getQuestItemsCount(partyMember, GOBLETS[3]) < 1))
				{
					giveItems(partyMember, GOBLETS[3], 1);
				}
			}
				break;
		}
		return super.onKill(npc, killer, isSummon);
	}
}
