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
package quests.Q00317_CatchTheWind;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Catch The Wind (317)
 * @author ivantotov
 */
public final class Q00317_CatchTheWind extends Quest
{
	// NPC
	private static final int RIZRAELL = 30361;
	// Item
	private static final int WIND_SHARD = 1078;
	// Misc
	private static final int MIN_LEVEL = 18;
	private static final double DROP_CHANCE = 0.5;
	// Monsters
	private static final int[] MONSTERS =
	{
		20036, // Lirein
		20044, // Lirein Elder
	};
	
	public Q00317_CatchTheWind()
	{
		super(317);
		addStartNpc(RIZRAELL);
		addTalkId(RIZRAELL);
		addKillId(MONSTERS);
		registerQuestItems(WIND_SHARD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30361-04.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30361-08.html":
			case "30361-09.html":
			{
				final long shardCount = getQuestItemsCount(player, WIND_SHARD);
				if (shardCount > 0)
				{
					giveAdena(player, ((shardCount * 10) + (shardCount >= 10 ? 2988 : 0)), true);
					takeItems(player, WIND_SHARD, -1);
				}
				
				if (event.equals("30361-08.html"))
				{
					qs.exitQuest(true, true);
				}
				
				htmltext = event;
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
			giveItemRandomly(qs.getPlayer(), npc, WIND_SHARD, 1, 0, DROP_CHANCE, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			htmltext = ((player.getLevel() >= MIN_LEVEL) ? "30361-03.htm" : "30361-02.htm");
		}
		else if (qs.isStarted())
		{
			htmltext = (hasQuestItems(player, WIND_SHARD) ? "30361-07.html" : "30361-05.html");
		}
		return htmltext;
	}
}