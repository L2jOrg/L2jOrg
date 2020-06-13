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
package quests.Q00329_CuriosityOfADwarf;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.util.GameUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Curiosity Of A Dwarf (329)
 * @author ivantotov
 */
public final class Q00329_CuriosityOfADwarf extends Quest
{
	// NPC
	private static final int TRADER_ROLENTO = 30437;
	// Items
	private static final int GOLEM_HEARTSTONE = 1346;
	private static final int BROKEN_HEARTSTONE = 1365;
	// Misc
	private static final int MIN_LEVEL = 33;
	// Monsters
	private static final Map<Integer, List<ItemHolder>> MONSTER_DROPS = new HashMap<>();
	static
	{
		MONSTER_DROPS.put(20083, Arrays.asList(new ItemHolder(GOLEM_HEARTSTONE, 3), new ItemHolder(BROKEN_HEARTSTONE, 54))); // Granitic Golem
		MONSTER_DROPS.put(20085, Arrays.asList(new ItemHolder(GOLEM_HEARTSTONE, 3), new ItemHolder(BROKEN_HEARTSTONE, 58))); // Puncher
	}
	
	public Q00329_CuriosityOfADwarf()
	{
		super(329);
		addStartNpc(TRADER_ROLENTO);
		addTalkId(TRADER_ROLENTO);
		addKillId(MONSTER_DROPS.keySet());
		registerQuestItems(GOLEM_HEARTSTONE, BROKEN_HEARTSTONE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30437-03.htm":
			{
				if (st.isCreated())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30437-06.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30437-07.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			final int rnd = getRandom(100);
			for (ItemHolder drop : MONSTER_DROPS.get(npc.getId()))
			{
				if (rnd < drop.getCount())
				{
					giveItemRandomly(killer, npc, drop.getId(), 1, 0, 1.0, true);
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
				htmltext = player.getLevel() >= MIN_LEVEL ? "30437-02.htm" : "30437-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, getRegisteredItemIds()))
				{
					final long broken = getQuestItemsCount(player, BROKEN_HEARTSTONE);
					final long golem = getQuestItemsCount(player, GOLEM_HEARTSTONE);
					giveAdena(player, ((broken * 5) + (golem * 40) + ((broken + golem) >= 700 ? 700 : 1000)), true);
					takeItems(player, -1, getRegisteredItemIds());
					htmltext = "30437-05.html";
				}
				else
				{
					htmltext = "30437-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
}