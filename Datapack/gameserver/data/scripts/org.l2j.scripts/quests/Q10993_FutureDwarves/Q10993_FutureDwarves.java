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
package quests.Q10993_FutureDwarves;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;

import quests.Q10999_LoserPriest3.Q10999_LoserPriest3;

/**
 * Future: Dwarves (10993)
 * @author Stayway
 */
public class Q10993_FutureDwarves extends Quest
{
	// NPCs
	private static final int GERALD = 30650;
	private static final int PIPPI = 30524;
	private static final int SILVERA = 30527;
	
	// Items
	private static final int FIRST_CLASS_BUFF_SCROLL = 29011;
	private static final int IMPROVED_SOE = 49087;
	
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q10993_FutureDwarves()
	{
		super(10993);
		addStartNpc(GERALD);
		addTalkId(PIPPI, GERALD, SILVERA);
		addCondMinLevel(MIN_LEVEL, "no-level.html"); // Custom
		addCondRace(Race.DWARF, "no-race.html"); // Custom
		addCondCompletedQuest(Q10999_LoserPriest3.class.getSimpleName(), "30650-04.html");
		setQuestNameNpcStringId(NpcStringId.LV_19_FUTURE_DWARVES);
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
			case "30650-02.htm":
			case "f_scavenger.html":
			case "f_artisan.html":
			{
				htmltext = event;
				break;
			}
			case "a_scavenger.html":
			{
				qs.startQuest();
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "a_artisan.html": // Custom html
			{
				qs.startQuest();
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30524-02.html":
			case "30527-02.html":
			{
				if (qs.getCond() > 1)
				{
					giveItems(player, FIRST_CLASS_BUFF_SCROLL, 5);
					giveItems(player, IMPROVED_SOE, 1);
					qs.exitQuest(false, true);
					htmltext = event;
				}
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
				if ((npc.getId() == GERALD))
				{
					htmltext = "30650-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == GERALD)
				{
					if (qs.getCond() >= 1)
					{
						htmltext = "30650-03.html";
					}
					break;
				}
				if ((npc.getId() == PIPPI) && (talker.getClassId() != ClassId.SCAVENGER))
				{
					if (qs.isCond(2))
					{
						htmltext = "30524-01.html";
					}
					break;
				}
				if ((npc.getId() == SILVERA) && (talker.getClassId() != ClassId.ARTISAN))
				{
					if (qs.isCond(3))
					{
						htmltext = "30527-01.html"; // Custom Html
					}
					break;
				}
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
}