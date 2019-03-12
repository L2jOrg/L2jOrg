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
package quests.Q00258_BringWolfPelts;

import java.util.HashMap;
import java.util.Map;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Bring Wolf Pelts (258)
 * @author xban1x
 */
public final class Q00258_BringWolfPelts extends Quest
{
	// Npc
	private static final int LECTOR = 30001;
	// Item
	private static final int WOLF_PELT = 702;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20120, // Wolf
		20442, // Elder Wolf
	};
	// Rewards
	private static final Map<Integer, Integer> REWARDS = new HashMap<>();
	static
	{
		REWARDS.put(41, 1); // Cloth Cap
		REWARDS.put(42, 6); // Leather Cap
		REWARDS.put(462, 9); // Stockings
	}
	// Misc
	private static final int MIN_LVL = 3;
	private static final int WOLF_PELT_COUNT = 40;
	
	public Q00258_BringWolfPelts()
	{
		super(258);
		addStartNpc(LECTOR);
		addTalkId(LECTOR);
		addKillId(MONSTERS);
		registerQuestItems(WOLF_PELT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30001-03.html"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1))
		{
			giveItems(killer, WOLF_PELT, 1);
			if (getQuestItemsCount(killer, WOLF_PELT) >= WOLF_PELT_COUNT)
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LVL) ? "30001-02.htm" : "30001-01.html";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30001-04.html";
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, WOLF_PELT) >= WOLF_PELT_COUNT)
						{
							final int chance = getRandom(16);
							for (Map.Entry<Integer, Integer> reward : REWARDS.entrySet())
							{
								if (chance < reward.getValue())
								{
									giveItems(player, reward.getKey(), 1);
									break;
								}
							}
							st.exitQuest(true, true);
							htmltext = "30001-05.html";
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
