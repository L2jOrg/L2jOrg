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
package quests.Q00358_IllegitimateChildOfTheGoddess;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

/**
 * Illegitimate Child of the Goddess (358)
 * @author Adry_85
 */
public final class Q00358_IllegitimateChildOfTheGoddess extends Quest
{
	// NPC
	private static final int OLTRAN = 30862;
	// Item
	private static final int SNAKE_SCALE = 5868;
	// Misc
	private static final int MIN_LEVEL = 63;
	private static final int SNAKE_SCALE_COUNT = 108;
	// Rewards
	private static final int[] REWARDS = new int[]
	{
		4975, // Recipe: BlackOrc Neckalce
		4973, // Recipe: BlackOrc Earring
		4974, // Recipe: BlackOrc Ring
		4939, // Recipe: Adam Neckalce
		4937, // Recipe: Adam Earring
		4938, // Recipe: Adam Ring
		4936, // Recipe: Avadon Shield
		4980, // Recipe: Doom Shield
	};
	// Mobs
	private static final Map<Integer, Double> MOBS = new HashMap<>();
	static
	{
		MOBS.put(20672, 0.71); // trives
		MOBS.put(20673, 0.74); // falibati
	}
	
	public Q00358_IllegitimateChildOfTheGoddess()
	{
		super(358);
		addStartNpc(OLTRAN);
		addTalkId(OLTRAN);
		addKillId(MOBS.keySet());
		registerQuestItems(SNAKE_SCALE);
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
			case "30862-02.htm":
			case "30862-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30862-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState st = getRandomPartyMemberState(player, 1, 3, npc);
		if ((st != null) && giveItemRandomly(player, npc, SNAKE_SCALE, 1, SNAKE_SCALE_COUNT, MOBS.get(npc.getId()), true))
		{
			st.setCond(2, true);
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
			htmltext = ((player.getLevel() >= MIN_LEVEL) ? "30862-01.htm" : "30862-05.html");
		}
		else if (st.isStarted())
		{
			if (getQuestItemsCount(player, SNAKE_SCALE) < SNAKE_SCALE_COUNT)
			{
				htmltext = "30862-06.html";
			}
			else
			{
				rewardItems(player, REWARDS[getRandom(REWARDS.length)], 1);
				st.exitQuest(true, true);
				htmltext = "30862-07.html";
			}
		}
		return htmltext;
	}
}
