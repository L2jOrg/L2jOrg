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
package quests.Q00369_CollectorOfJewels;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.QuestItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

/**
 * Collector of Jewels (369)
 * @author Adry_85
 */
public final class Q00369_CollectorOfJewels extends Quest
{
	// NPC
	private static final int NELL = 30376;
	// Items
	private static final int FLARE_SHARD = 5882;
	private static final int FREEZING_SHARD = 5883;
	// Misc
	private static final int MIN_LEVEL = 25;
	// Mobs
	private static final Map<Integer, QuestItemHolder> MOBS_DROP_CHANCES = new HashMap<>();
	static
	{
		MOBS_DROP_CHANCES.put(20609, new QuestItemHolder(FLARE_SHARD, 75, 1)); // salamander_lakin
		MOBS_DROP_CHANCES.put(20612, new QuestItemHolder(FLARE_SHARD, 91, 1)); // salamander_rowin
		MOBS_DROP_CHANCES.put(20749, new QuestItemHolder(FLARE_SHARD, 100, 2)); // death_fire
		MOBS_DROP_CHANCES.put(20616, new QuestItemHolder(FREEZING_SHARD, 81, 1)); // undine_lakin
		MOBS_DROP_CHANCES.put(20619, new QuestItemHolder(FREEZING_SHARD, 87, 1)); // undine_rowin
		MOBS_DROP_CHANCES.put(20747, new QuestItemHolder(FREEZING_SHARD, 100, 2)); // roxide
	}
	
	public Q00369_CollectorOfJewels()
	{
		super(369);
		addStartNpc(NELL);
		addTalkId(NELL);
		addKillId(MOBS_DROP_CHANCES.keySet());
		registerQuestItems(FLARE_SHARD, FREEZING_SHARD);
	}
	
	@Override
	public boolean checkPartyMember(Player member, Npc npc)
	{
		final QuestState st = getQuestState(member, false);
		return ((st != null) && (st.isMemoState(1) || st.isMemoState(3)));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30376-02.htm":
			{
				st.startQuest();
				st.setMemoState(1);
				htmltext = event;
				break;
			}
			case "30376-05.html":
			{
				htmltext = event;
				break;
			}
			case "30376-06.html":
			{
				if (st.isMemoState(2))
				{
					st.setMemoState(3);
					st.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30376-07.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestItemHolder item = MOBS_DROP_CHANCES.get(npc.getId());
		if (getRandom(100) < item.getChance())
		{
			Player luckyPlayer = getRandomPartyMember(player, npc);
			if (luckyPlayer != null)
			{
				final QuestState st = getQuestState(luckyPlayer, false);
				final int itemCount = (st.isMemoState(1) ? 50 : 200);
				final int cond = (st.isMemoState(1) ? 2 : 4);
				if (giveItemRandomly(luckyPlayer, npc, item.getId(), item.getCount(), itemCount, 1.0, true) //
					&& (getQuestItemsCount(luckyPlayer, FLARE_SHARD, FREEZING_SHARD) >= (itemCount * 2)))
				{
					st.setCond(cond);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st.isCreated())
		{
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30376-01.htm" : "30376-03.html";
		}
		else if (st.isStarted())
		{
			switch (st.getMemoState())
			{
				case 1:
				{
					if (getQuestItemsCount(player, FLARE_SHARD, FREEZING_SHARD) >= 100)
					{
						giveAdena(player, 3000, true);
						takeItems(player, -1, FLARE_SHARD, FREEZING_SHARD);
						st.setMemoState(2);
						htmltext = "30376-04.html";
					}
					else
					{
						htmltext = "30376-08.html";
					}
					break;
				}
				case 2:
				{
					htmltext = "30376-09.html";
					break;
				}
				case 3:
				{
					if (getQuestItemsCount(player, FLARE_SHARD, FREEZING_SHARD) >= 400)
					{
						giveAdena(player, 12000, true);
						takeItems(player, -1, FLARE_SHARD, FREEZING_SHARD);
						st.exitQuest(true, true);
						htmltext = "30376-10.html";
					}
					else
					{
						htmltext = "30376-11.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
