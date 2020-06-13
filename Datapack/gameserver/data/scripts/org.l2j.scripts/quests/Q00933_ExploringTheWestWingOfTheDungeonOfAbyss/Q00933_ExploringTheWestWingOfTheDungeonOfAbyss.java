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
package quests.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss;

import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * @author QuangNguyen
 */
public class Q00933_ExploringTheWestWingOfTheDungeonOfAbyss extends Quest
{
	// NPCs
	private static final int MAGRIT = 31774;
	private static final int INGRIT = 31775;
	// Monsters
	public final int MERTT = 21638;
	public final int DUHT = 21639;
	public final int PRIZT = 21640;
	public final int KOVART = 21641;
	// Items
	public final ItemHolder OSKZLA = new ItemHolder(90008, 1);
	public final ItemHolder POD = new ItemHolder(90136, 1);
	
	public Q00933_ExploringTheWestWingOfTheDungeonOfAbyss()
	{
		super(933);
		addStartNpc(MAGRIT, INGRIT);
		addTalkId(MAGRIT, INGRIT);
		addKillId(MERTT, DUHT, PRIZT, KOVART);
		registerQuestItems(OSKZLA.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31774-01.htm":
			case "31774-02.htm":
			case "31774-03.htm":
			case "31775-01.htm":
			case "31775-02.htm":
			case "31775-03.htm":
			{
				htmltext = event;
				break;
			}
			
			case "31774-04.htm":
			{
				if (player.getLevel() >= 40)
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "31775-04.htm":
			{
				if (player.getLevel() >= 40)
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "end.htm":
			{
				player.addExpAndSp(250000, 7700);
				rewardItems(player, POD);
				qs.exitQuest(QuestType.DAILY, true);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = (talker.getLevel() < 40) ? "nolvl.htm" : "31774-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == MAGRIT)
				{
					switch (qs.getCond())
					{
						case 0:
						{
							if ((qs.getPlayer().getLevel() >= 40) && (qs.getPlayer().getLevel() <= 46))
							{
								htmltext = "31774-01.htm";
							}
							else
							{
								htmltext = "31774-01a.htm";
							}
							break;
						}
						case 1:
						{
							htmltext = "31774-04.htm";
							break;
						}
						case 2:
						{
							htmltext = "31774-05.htm";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == INGRIT)
				{
					switch (qs.getCond())
					{
						case 0:
						{
							if ((qs.getPlayer().getLevel() >= 40) && (qs.getPlayer().getLevel() <= 46))
							{
								htmltext = "31775-01.htm";
								qs.startQuest();
							}
							else
							{
								htmltext = "31775-01a.htm";
							}
							break;
						}
						case 1:
						{
							htmltext = "31775-04.htm";
							break;
						}
						case 2:
						{
							htmltext = "31775-05.htm";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					if ((npc.getId() == MAGRIT) && (qs.getPlayer().getLevel() < 40))
					{
						htmltext = "31774-01.htm";
					}
					else if ((npc.getId() == INGRIT) && (qs.getPlayer().getLevel() < 40))
					{
						htmltext = "31775-01.htm";
					}
					else
					{
						htmltext = "nolvl.htm";
					}
				}
				else
				{
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs.getCond() == 1)
		{
			if (getQuestItemsCount(killer, OSKZLA.getId()) < 50)
			{
				giveItems(killer, OSKZLA);
			}
			if (getQuestItemsCount(killer, OSKZLA.getId()) >= 50)
			{
				qs.setCond(2);
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}