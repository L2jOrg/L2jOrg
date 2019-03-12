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
package quests.Q00374_WhisperOfDreamsPart1;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Whisper Of Dreams Part1 (374)
 * @author Stayway
 */
public class Q00374_WhisperOfDreamsPart1 extends Quest
{
	// NPCs
	private static final int VANUTU = 30938;
	private static final int GALMAN = 31044;
	private static final int CAVE_BEAST = 20620;
	private static final int DEATH_WAVE = 20621;
	// Items
	private static final ItemHolder CAVE_BEAST_TOOTH = new ItemHolder(5884, 360);
	private static final ItemHolder DEATH_WAVE_LIGHT = new ItemHolder(5885, 360);
	private static final ItemHolder SEALED_MYSTERIOUS_STONE = new ItemHolder(5886, 1);
	private static final int MYSTERIOUS_STONE = 5887;
	// Rewards
	private static final int SCROLL_PART_EA = 49475;
	private static final int REFINED_SCROLL_PART_EA = 49478;
	private static final int ENCHANT_ARMOR_B = 948;
	private static final int IMPROVED_ENCHANT_ARMOR_B = 29743;
	// Misc
	private static final int MIN_LEVEL = 56;
	private static final int MAX_LEVEL = 66;
	
	public Q00374_WhisperOfDreamsPart1()
	{
		super(374);
		addStartNpc(VANUTU);
		addTalkId(VANUTU, GALMAN);
		addKillId(CAVE_BEAST, DEATH_WAVE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30938-02.html");
		registerQuestItems(SEALED_MYSTERIOUS_STONE.getId());
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
				case "30938-01.htm":
				{
					qs.startQuest();
					htmltext = event;
					break;
				}
				case "30938-06.html":
				{
					if (qs.isCond(2))
					{
						qs.setCond(3, true);
						htmltext = event;
					}
					break;
				}
				case "reward1":
				{
					if (hasAllItems(player, true, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH))
					{
						if (qs.isCond(2))
						{
							giveItems(player, SCROLL_PART_EA, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-05.html";
						}
						else if (qs.isCond(4))
						{
							giveItems(player, SCROLL_PART_EA, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-08.html";
						}
					}
					break;
				}
				case "reward2":
				{
					if (hasAllItems(player, true, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH))
					{
						if (qs.isCond(2))
						{
							giveItems(player, REFINED_SCROLL_PART_EA, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-05.html";
						}
						else if (qs.isCond(4))
						{
							giveItems(player, REFINED_SCROLL_PART_EA, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-08.html";
						}
					}
					break;
				}
				case "reward3":
				{
					if (hasAllItems(player, true, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH))
					{
						if (qs.isCond(2))
						{
							giveItems(player, ENCHANT_ARMOR_B, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-05.html";
						}
						else if (qs.isCond(4))
						{
							giveItems(player, ENCHANT_ARMOR_B, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-08.html";
						}
					}
					break;
				}
				case "reward4":
				{
					if (hasAllItems(player, true, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH))
					{
						if (qs.isCond(2))
						{
							giveItems(player, IMPROVED_ENCHANT_ARMOR_B, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-05.html";
						}
						else if (qs.isCond(4))
						{
							giveItems(player, IMPROVED_ENCHANT_ARMOR_B, 1);
							takeAllItems(player, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH);
							giveAdena(player, 9000, true);
							htmltext = "30938-08.html";
						}
					}
					break;
				}
				case "31044-01.html":
				{
					if (qs.isCond(4))
					{
						giveItems(player, MYSTERIOUS_STONE, 1);
						takeAllItems(player, SEALED_MYSTERIOUS_STONE);
						qs.exitQuest(true, true);
						htmltext = event;
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
		switch (npc.getId())
		{
			case VANUTU:
			{
				if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				else if (qs.isCreated())
				{
					htmltext = "30938.htm";
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30938-03.html";
							break;
						}
						case 2:
						{
							htmltext = "30938-04.html";
							break;
						}
						case 3:
						{
							htmltext = "30938-07.html";
							break;
						}
						case 4:
						{
							htmltext = "30938-08.html";
							break;
						}
					}
				}
				break;
			}
			case GALMAN:
			{
				if (qs.isCond(4))
				{
					htmltext = "31044.html";
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
				case CAVE_BEAST:
				{
					if (qs.getCond() < 4)
					{
						giveItemRandomly(qs.getPlayer(), npc, CAVE_BEAST_TOOTH.getId(), 1, CAVE_BEAST_TOOTH.getCount(), 0.9, true);
						if (qs.isCond(3))
						{
							giveItemRandomly(qs.getPlayer(), npc, SEALED_MYSTERIOUS_STONE.getId(), 1, SEALED_MYSTERIOUS_STONE.getCount(), 0.2, true);
						}
					}
					break;
				}
				case DEATH_WAVE:
				{
					if (qs.getCond() < 4)
					{
						giveItemRandomly(qs.getPlayer(), npc, DEATH_WAVE_LIGHT.getId(), 1, DEATH_WAVE_LIGHT.getCount(), 0.9, true);
						if (qs.isCond(3))
						{
							giveItemRandomly(qs.getPlayer(), npc, SEALED_MYSTERIOUS_STONE.getId(), 1, SEALED_MYSTERIOUS_STONE.getCount(), 0.2, true);
						}
					}
					break;
				}
			}
			if (qs.isCond(1) && (hasAllItems(qs.getPlayer(), true, DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH)))
			{
				qs.setCond(2, true);
			}
			if (qs.isCond(3) && (hasAllItems(qs.getPlayer(), true, SEALED_MYSTERIOUS_STONE)))
			{
				qs.setCond(4, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
