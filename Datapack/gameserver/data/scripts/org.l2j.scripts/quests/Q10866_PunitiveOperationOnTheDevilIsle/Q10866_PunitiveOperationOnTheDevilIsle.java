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
package quests.Q10866_PunitiveOperationOnTheDevilIsle;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;

/**
 * Punitive Operation on the Devil Isle (10866)
 * @author Stayway
 */
public final class Q10866_PunitiveOperationOnTheDevilIsle extends Quest
{
	// NPCs
	private static final int RODEMAI = 30756;
	private static final int EIN = 34017;
	private static final int FETHIN = 34019;
	private static final int NIKIA = 34020;
	// Misc
	private static final int MIN_LEVEL = 70;
	
	public Q10866_PunitiveOperationOnTheDevilIsle()
	{
		super(10866);
		addStartNpc(RODEMAI);
		addTalkId(RODEMAI, EIN, FETHIN, NIKIA);
		setQuestNameNpcStringId(NpcStringId.LV_70_PUNITIVE_OPERATION_ON_THE_DEVIL_S_ISLE);
	}
	
	@Override
	public boolean checkPartyMember(Player member, Npc npc)
	{
		final QuestState qs = getQuestState(member, false);
		return ((qs != null) && qs.isStarted());
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
			case "30756-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34017-02.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34019-02.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "34020-02.html":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 150000, 4500);
					giveAdena(player, 13136, true);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30756-01.htm" : "no_lvl.html";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case RODEMAI:
				{
					if (qs.isCond(1))
					{
						htmltext = "30756-02.html";
					}
					break;
				}
				case EIN:
				{
					if (qs.isCond(1))
					{
						htmltext = "34017-01.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "34017-02.html";
					}
					break;
				}
				case FETHIN:
				{
					if (qs.isCond(2))
					{
						htmltext = "34019-01.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "34019-02.html";
					}
					break;
				}
				case NIKIA:
				{
					if (qs.isCond(3))
					{
						htmltext = "34020-01.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == RODEMAI)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}