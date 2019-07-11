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
package quests.Q11012_FutureElves;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;

import quests.Q11011_NewPotionDevelopment3.Q11011_NewPotionDevelopment3;

/**
 * Future: Future Elves (11012)
 * @author Stayway
 */
public class Q11012_FutureElves extends Quest
{
	// NPCs
	private static final int HERBIEL = 30150;
	private static final int SORIUS = 30327;
	private static final int REISA = 30328;
	private static final int ROSELLA = 30414;
	private static final int MANUEL = 30293;
	// Items
	private static final int FIRST_CLASS_BUFF_SCROLL = 29011;
	private static final int IMPROVED_SOE = 49087;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q11012_FutureElves()
	{
		super(11012);
		addStartNpc(HERBIEL);
		addTalkId(HERBIEL, SORIUS, REISA, ROSELLA, MANUEL);
		addCondMinLevel(MIN_LEVEL, "no-level.html"); // Custom
		addCondRace(Race.ELF, "no-race.html"); // Custom
		addCondCompletedQuest(Q11011_NewPotionDevelopment3.class.getSimpleName(), "30150-04.html");
		setQuestNameNpcStringId(NpcStringId.LV_19_FUTURE_ELVES);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30150-02.htm":
			case "30150-02a.htm":
			case "f_knight.html":
			case "f_scout.html":
			case "m_wizard.html":
			case "m_oracle.html":
			{
				htmltext = event;
				break;
			}
			case "a_knight.html":
			{
				qs.startQuest();
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "a_scout.html":
			{
				qs.startQuest();
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "a_wizard.html":
			{
				qs.startQuest();
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "a_oracle.html":
			{
				qs.startQuest();
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30327-02.html":
			case "30328-02.html": // Custom html
			case "30414-02.html":
			case "30293-02.html": // Custom html
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
	public String onTalk(L2Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if ((npc.getId() == HERBIEL) && (talker.getClassId() == ClassId.ELVEN_FIGHTER))
				{
					htmltext = "30150-01.html";
				}
				else if (talker.getClassId() == ClassId.ELVEN_MAGE)
				{
					htmltext = "30150-01a.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == HERBIEL)
				{
					if (qs.getCond() >= 1)
					{
						htmltext = "30150-03.html";
					}
					break;
				}
				if ((npc.getId() == SORIUS) && (talker.getClassId() != ClassId.ELVEN_KNIGHT))
				{
					if (qs.isCond(2))
					{
						htmltext = "30327-01.html"; // Custom html
					}
					break;
				}
				if ((npc.getId() == REISA) && (talker.getClassId() != ClassId.ELVEN_SCOUT))
				{
					if (qs.isCond(3))
					{
						htmltext = "30328-01.html";
					}
					break;
				}
				if ((npc.getId() == ROSELLA) && (talker.getClassId() != ClassId.ELVEN_WIZARD))
				{
					if (qs.isCond(4))
					{
						htmltext = "30414-01.html";
					}
					break;
				}
				if ((npc.getId() == MANUEL) && (talker.getClassId() != ClassId.ORACLE))
				{
					if (qs.isCond(5))
					{
						htmltext = "30293-01.html"; // Custom html
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