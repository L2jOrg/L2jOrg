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
package quests.Q00264_KeenClaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Keen Claws (264)
 * @author xban1x
 */
public final class Q00264_KeenClaws extends Quest
{
	// Npc
	private static final int PAINT = 30136;
	// Item
	private static final int WOLF_CLAW = 1367;
	// Monsters
	private static final Map<Integer, List<ItemHolder>> MONSTER_CHANCES = new HashMap<>();
	// Rewards
	private static final Map<Integer, List<ItemHolder>> REWARDS = new HashMap<>();
	// Misc
	private static final int MIN_LVL = 3;
	private static final int WOLF_CLAW_COUNT = 50;
	static
	{
		MONSTER_CHANCES.put(20003, Arrays.asList(new ItemHolder(2, 25), new ItemHolder(8, 50)));
		MONSTER_CHANCES.put(20456, Arrays.asList(new ItemHolder(1, 80), new ItemHolder(2, 100)));
		
		REWARDS.put(0, Arrays.asList(new ItemHolder(735, 1)));
		REWARDS.put(1, Arrays.asList(new ItemHolder(734, 1)));
		REWARDS.put(2, Arrays.asList(new ItemHolder(35, 1)));
	}
	
	public Q00264_KeenClaws()
	{
		super(264);
		addStartNpc(PAINT);
		addTalkId(PAINT);
		addKillId(MONSTER_CHANCES.keySet());
		registerQuestItems(WOLF_CLAW);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30136-03.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1))
		{
			final int random = getRandom(100);
			for (ItemHolder drop : MONSTER_CHANCES.get(npc.getId()))
			{
				if (random < drop.getCount())
				{
					if (giveItemRandomly(killer, WOLF_CLAW, drop.getId(), WOLF_CLAW_COUNT, 1, true))
					{
						st.setCond(2);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LVL) ? "30136-02.htm" : "30136-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30136-04.html";
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, WOLF_CLAW) >= WOLF_CLAW_COUNT)
						{
							final int chance = getRandom(17);
							for (Map.Entry<Integer, List<ItemHolder>> reward : REWARDS.entrySet())
							{
								if (chance < reward.getKey())
								{
									for (ItemHolder item : reward.getValue())
									{
										rewardItems(player, item);
									}
									if (chance == 0)
									{
										playSound(player, QuestSound.ITEMSOUND_QUEST_JACKPOT);
									}
									break;
								}
							}
							st.exitQuest(true, true);
							htmltext = "30136-05.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
