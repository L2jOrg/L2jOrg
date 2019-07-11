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
package quests.Q10994_FutureOrcs;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;

import quests.Q11023_RedGemNecklace3.Q11023_RedGemNecklace3;

/**
 * Future: Future Orcs (10994)
 * @author Stayway
 */
public class Q10994_FutureOrcs extends Quest
{
	// NPCs
	private static final int USKA = 30560;
	private static final int KARUKIA = 30570;
	private static final int GANTAKAI = 30587;
	private static final int HESTUI = 30585;
	// Items
	private static final int FIRST_CLASS_BUFF_SCROLL = 29011;
	private static final int IMPROVED_SOE = 49087;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q10994_FutureOrcs()
	{
		super(10994);
		addStartNpc(USKA);
		addTalkId(USKA, KARUKIA, GANTAKAI, HESTUI);
		addCondMinLevel(MIN_LEVEL, "no-level.html"); // Custom
		addCondRace(Race.ORC, "no-race.html"); // Custom
		addCondCompletedQuest(Q11023_RedGemNecklace3.class.getSimpleName(), "30560-04.html");
		setQuestNameNpcStringId(NpcStringId.LV_19_FUTURE_ORCS);
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
			case "30560-02.htm":
			case "30560-02a.htm":
			case "f_raider.html":
			case "f_monk.html":
			case "m_shaman.html":
			{
				htmltext = event;
				break;
			}
			case "a_raider.html":
			{
				qs.startQuest();
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "a_monk.html":
			{
				qs.startQuest();
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "a_shaman.html":
			{
				qs.startQuest();
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "30570-02.html": // Custom html
			case "30587-02.html":
			case "30585-02.html":
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
				if ((npc.getId() == USKA) && (talker.getClassId() == ClassId.ORC_FIGHTER))
				{
					htmltext = "30560-01.html";
				}
				else if (talker.getClassId() == ClassId.ORC_MAGE)
				{
					htmltext = "30560-01a.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == USKA)
				{
					if (qs.getCond() >= 1)
					{
						htmltext = "30560-03.html";
					}
					break;
				}
				if ((npc.getId() == KARUKIA) && (talker.getClassId() != ClassId.ORC_RAIDER))
				{
					if (qs.isCond(2))
					{
						htmltext = "30570-01.html"; // Custom html
					}
					break;
				}
				if ((npc.getId() == GANTAKAI) && (talker.getClassId() != ClassId.ORC_MONK))
				{
					if (qs.isCond(3))
					{
						htmltext = "30587-01.html";
					}
					break;
				}
				if ((npc.getId() == HESTUI) && (talker.getClassId() != ClassId.ORC_SHAMAN))
				{
					if (qs.isCond(4))
					{
						htmltext = "30585-01.html";
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