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
package quests.Q00292_BrigandsSweep;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Brigands Sweep (292)
 * @author xban1x
 */
public final class Q00292_BrigandsSweep extends Quest
{
	// NPC's
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;
	// Items
	private static final int GOBLIN_NECKLACE = 1483;
	private static final int GOBLIN_PENDANT = 1484;
	private static final int GOBLIN_LORD_PENDANT = 1485;
	private static final int SUSPICIOUS_MEMO = 1486;
	private static final int SUSPICIOUS_CONTRACT = 1487;
	// Monsters
	private static final Map<Integer, Integer> MOB_ITEM_DROP = new HashMap<>();
	static
	{
		MOB_ITEM_DROP.put(20322, GOBLIN_NECKLACE); // Goblin Brigand
		MOB_ITEM_DROP.put(20323, GOBLIN_PENDANT); // Goblin Brigand Leader
		MOB_ITEM_DROP.put(20324, GOBLIN_NECKLACE); // Goblin Brigand Lieutenant
		MOB_ITEM_DROP.put(20327, GOBLIN_NECKLACE); // Goblin Snooper
		MOB_ITEM_DROP.put(20528, GOBLIN_LORD_PENDANT); // Goblin Lord
	}
	// Misc
	private static final int MIN_LVL = 5;
	
	public Q00292_BrigandsSweep()
	{
		super(292);
		addStartNpc(SPIRON);
		addTalkId(SPIRON, BALANKI);
		addKillId(MOB_ITEM_DROP.keySet());
		registerQuestItems(GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT, SUSPICIOUS_MEMO, SUSPICIOUS_CONTRACT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		String html = null;
		if (qs == null)
		{
			return html;
		}
		
		switch (event)
		{
			case "30532-03.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					html = event;
				}
				break;
			}
			case "30532-06.html":
			{
				if (qs.isStarted())
				{
					qs.exitQuest(true, true);
					html = event;
				}
				break;
			}
			case "30532-07.html":
			{
				if (qs.isStarted())
				{
					html = event;
				}
				break;
			}
		}
		return html;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			final int chance = getRandom(10);
			if (chance > 5)
			{
				giveItemRandomly(killer, npc, MOB_ITEM_DROP.get(npc.getId()), 1, 0, 1.0, true);
			}
			else if (qs.isCond(1) && (chance > 4) && !hasQuestItems(killer, SUSPICIOUS_CONTRACT))
			{
				final long memos = getQuestItemsCount(killer, SUSPICIOUS_MEMO);
				if (memos < 3)
				{
					if (giveItemRandomly(killer, npc, SUSPICIOUS_MEMO, 1, 3, 1.0, false))
					{
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						giveItems(killer, SUSPICIOUS_CONTRACT, 1);
						takeItems(killer, SUSPICIOUS_MEMO, -1);
						qs.setCond(2, true);
					}
					else
					{
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		switch (npc.getId())
		{
			case SPIRON:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						html = (talker.getRace() == Race.DWARF) ? (talker.getLevel() >= MIN_LVL) ? "30532-02.htm" : "30532-01.htm" : "30532-00.htm";
						break;
					}
					case State.STARTED:
					{
						if (!hasAtLeastOneQuestItem(talker, getRegisteredItemIds()))
						{
							html = "30532-04.html";
						}
						else
						{
							final long necklaces = getQuestItemsCount(talker, GOBLIN_NECKLACE);
							final long pendants = getQuestItemsCount(talker, GOBLIN_PENDANT);
							final long lordPendants = getQuestItemsCount(talker, GOBLIN_LORD_PENDANT);
							final long sum = necklaces + pendants + lordPendants;
							if (sum > 0)
							{
								giveAdena(talker, (necklaces * 6) + (pendants * 8) + (lordPendants * 10) + (sum >= 10 ? 1000 : 0), true);
								takeItems(talker, -1, GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT);
							}
							if ((sum > 0) && !hasAtLeastOneQuestItem(talker, SUSPICIOUS_MEMO, SUSPICIOUS_CONTRACT))
							{
								html = "30532-05.html";
							}
							else
							{
								final long memos = getQuestItemsCount(talker, SUSPICIOUS_MEMO);
								if ((memos == 0) && hasQuestItems(talker, SUSPICIOUS_CONTRACT))
								{
									giveAdena(talker, 100, true);
									takeItems(talker, -1, SUSPICIOUS_CONTRACT); // Retail like, reward is given in 2 pieces if both conditions are meet.
									html = "30532-10.html";
								}
								else
								{
									if (memos == 1)
									{
										html = "30532-08.html";
									}
									else if (memos >= 2)
									{
										html = "30532-09.html";
									}
								}
							}
						}
					}
				}
				break;
			}
			case BALANKI:
			{
				if (qs.isStarted())
				{
					if (hasQuestItems(talker, SUSPICIOUS_CONTRACT))
					{
						giveAdena(talker, 620, true);
						takeItems(talker, 1487, -1);
						html = "30533-02.html";
					}
					else
					{
						html = "30533-01.html";
					}
				}
				break;
			}
		}
		return html;
	}
}
