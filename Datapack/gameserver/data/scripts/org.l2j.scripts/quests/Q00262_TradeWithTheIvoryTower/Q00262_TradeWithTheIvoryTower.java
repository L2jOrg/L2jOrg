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
package quests.Q00262_TradeWithTheIvoryTower;

import java.util.HashMap;
import java.util.Map;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Trade With The Ivory Tower (262)
 * @author ivantotov
 */
public final class Q00262_TradeWithTheIvoryTower extends Quest
{
	// NPCs
	private static final int VOLLODOS = 30137;
	// Items
	private static final int SPORE_SAC = 707;
	// Misc
	private static final int MIN_LEVEL = 8;
	private static final int REQUIRED_ITEM_COUNT = 10;
	// Monsters
	private static final Map<Integer, Integer> MOBS_SAC = new HashMap<>();
	static
	{
		MOBS_SAC.put(20007, 3); // Green Fungus
		MOBS_SAC.put(20400, 4); // Blood Fungus
	}
	
	public Q00262_TradeWithTheIvoryTower()
	{
		super(262);
		addStartNpc(VOLLODOS);
		addTalkId(VOLLODOS);
		addKillId(MOBS_SAC.keySet());
		registerQuestItems(SPORE_SAC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30137-03.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, Player player, boolean isSummon)
	{
		final Player partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null)
		{
			return super.onKill(npc, player, isSummon);
		}
		
		final QuestState st = getQuestState(partyMember, false);
		float chance = (MOBS_SAC.get(npc.getId()) * Config.RATE_QUEST_DROP);
		if (getRandom(10) < chance)
		{
			rewardItems(partyMember, SPORE_SAC, 1);
			if (getQuestItemsCount(partyMember, SPORE_SAC) >= REQUIRED_ITEM_COUNT)
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = player.getLevel() >= MIN_LEVEL ? "30137-02.htm" : "30137-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						if (getQuestItemsCount(player, SPORE_SAC) < REQUIRED_ITEM_COUNT)
						{
							htmltext = "30137-04.html";
						}
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, SPORE_SAC) >= REQUIRED_ITEM_COUNT)
						{
							htmltext = "30137-05.html";
							giveAdena(player, 300, true);
							st.exitQuest(true, true);
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