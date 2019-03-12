/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00375_WhisperOfDreamsPart2;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Whisper Of Dreams Part2 (375)
 * @author Stayway
 */
public class Q00375_WhisperOfDreamsPart2 extends Quest
{
	// NPCs
	private static final int VANUTU = 30938;
	// Monsters
	private static final int LIMAL_KARINNESS = 20628;
	private static final int KARIK = 20629;
	// Items
	private static final int KARIK_HORN = 5888;
	private static final int LIMAL_KARINESS_BLOOD = 5889;
	private static final int MYSTERIOUS_STONE = 5887;
	// Rewards
	private static final int SCROLL_PART_EW = 49474;
	private static final int REFINED_SCROLL_PART_EW = 49476;
	private static final int ENCHANT_WEAPON_B = 947;
	private static final int IMPROVED_ENCHANT_WEAPON_B = 33808;
	// Misc
	private static final int MIN_LEVEL = 60;
	private static final int MAX_LEVEL = 74;
	
	public Q00375_WhisperOfDreamsPart2()
	{
		super(375);
		addStartNpc(VANUTU);
		addTalkId(VANUTU);
		addKillId(LIMAL_KARINNESS, KARIK);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30938-02.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs != null)
		{
			switch (event)
			{
				case "30938-03.htm":
				{
					qs.startQuest();
					htmltext = event;
					break;
				}
				case "30938-07.html":
				{
					qs.setCond(1);
					htmltext = event;
					break;
				}
				case "30938-08.html":
				{
					qs.exitQuest(true, true);
					htmltext = event;
					break;
				}
				case "reward1":
				{
					if (qs.isCond(2) && (getQuestItemsCount(player, KARIK_HORN) >= 325) && (getQuestItemsCount(player, LIMAL_KARINESS_BLOOD) >= 325))
					{
						giveItems(player, SCROLL_PART_EW, 1);
						takeItems(player, KARIK_HORN, 325);
						takeItems(player, LIMAL_KARINESS_BLOOD, 325);
						giveAdena(player, 9000, true);
						htmltext = "30938-06.html";
					}
					break;
				}
				case "reward2":
				{
					if (qs.isCond(2) && (getQuestItemsCount(player, KARIK_HORN) >= 325) && (getQuestItemsCount(player, LIMAL_KARINESS_BLOOD) >= 325))
					{
						giveItems(player, REFINED_SCROLL_PART_EW, 1);
						takeItems(player, KARIK_HORN, 325);
						takeItems(player, LIMAL_KARINESS_BLOOD, 325);
						giveAdena(player, 9000, true);
						htmltext = "30938-06.html";
					}
					break;
				}
				case "reward3":
				{
					if (qs.isCond(2) && (getQuestItemsCount(player, KARIK_HORN) >= 325) && (getQuestItemsCount(player, LIMAL_KARINESS_BLOOD) >= 325))
					{
						giveItems(player, ENCHANT_WEAPON_B, 1);
						takeItems(player, KARIK_HORN, 325);
						takeItems(player, LIMAL_KARINESS_BLOOD, 325);
						giveAdena(player, 9000, true);
						htmltext = "30938-06.html";
					}
					break;
				}
				case "reward4":
				{
					if (qs.isCond(2) && (getQuestItemsCount(player, KARIK_HORN) >= 325) && (getQuestItemsCount(player, LIMAL_KARINESS_BLOOD) >= 325))
					{
						giveItems(player, IMPROVED_ENCHANT_WEAPON_B, 1);
						takeItems(player, KARIK_HORN, 325);
						takeItems(player, LIMAL_KARINESS_BLOOD, 325);
						giveAdena(player, 9000, true);
						htmltext = "30938-06.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (getQuestItemsCount(player, MYSTERIOUS_STONE) >= 1)
				{
					takeItems(player, MYSTERIOUS_STONE, 1);
					htmltext = "30938-01.htm";
				}
				else
				{
					htmltext = "30938-05.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30938-04.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = (getQuestItemsCount(player, KARIK_HORN) >= 325) && (getQuestItemsCount(player, LIMAL_KARINESS_BLOOD) >= 325) ? "30938-05.html" : "30938-06.html";
				}
				break;
			}
		}
		return htmltext;
		
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case KARIK:
				{
					if (qs.isCond(1) && qs.isStarted())
					{
						giveItemRandomly(killer, npc, KARIK_HORN, 1, 325, 0.95, true);
					}
					break;
				}
				case LIMAL_KARINNESS:
				{
					if (qs.isCond(1) && qs.isStarted())
					{
						giveItemRandomly(killer, npc, LIMAL_KARINESS_BLOOD, 1, 325, 0.95, true);
					}
					break;
				}
			}
			if (qs.isCond(1) && (getQuestItemsCount(killer, LIMAL_KARINESS_BLOOD) >= 325) && (getQuestItemsCount(killer, KARIK_HORN) >= 325))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
