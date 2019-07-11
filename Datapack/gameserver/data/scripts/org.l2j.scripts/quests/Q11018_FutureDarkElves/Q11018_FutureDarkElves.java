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
package quests.Q11018_FutureDarkElves;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;

import quests.Q11017_PrepareForTrade3.Q11017_PrepareForTrade3;

/**
 * Future: Dark Elves (11018)
 * @author Stayway
 */
public class Q11018_FutureDarkElves extends Quest
{
	// NPCs
	private static final int VOLLODOS = 30137;
	private static final int VIRGIL = 30329;
	private static final int TRISKEL = 30416;
	private static final int VARIKA = 30421;
	private static final int SIDRA = 30330;
	
	// Items
	private static final int FIRST_CLASS_BUFF_SCROLL = 29011;
	private static final int IMPROVED_SOE = 49087;
	
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q11018_FutureDarkElves()
	{
		super(11018);
		addStartNpc(VOLLODOS);
		addTalkId(VIRGIL, VOLLODOS, TRISKEL, VARIKA, SIDRA);
		addCondMinLevel(MIN_LEVEL, "no-level.html"); // Custom
		addCondRace(Race.DARK_ELF, "no-race.html"); // Custom
		addCondCompletedQuest(Q11017_PrepareForTrade3.class.getSimpleName(), "30137-04.html");
		setQuestNameNpcStringId(NpcStringId.LV_19_FUTURE_DARK_ELVES);
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
			case "30137-02.htm":
			case "30137-02a.htm":
			case "f_PalusKnight.html":
			case "f_assassin.html":
			case "m_wizard.html":
			case "m_shillien.html":
			{
				htmltext = event;
				break;
			}
			case "a_PalusKnight.html":
			{
				qs.startQuest();
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "a_assassin.html": // Custom html
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
			case "a_shillien.html": // Custom html
			{
				qs.startQuest();
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30329-02.html":
			case "30416-02.html":
			case "30421-02.html":
			case "30330-02.html":
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
				if ((npc.getId() == VOLLODOS) && (talker.getClassId() == ClassId.DARK_FIGHTER))
				{
					htmltext = "30137-01.html";
				}
				else if (talker.getClassId() == ClassId.DARK_MAGE)
				{
					htmltext = "30137-01a.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == VOLLODOS)
				{
					if (qs.getCond() >= 1)
					{
						htmltext = "30137-03.html";
					}
					break;
				}
				if ((npc.getId() == VIRGIL) && (talker.getClassId() != ClassId.PALUS_KNIGHT))
				{
					if (qs.isCond(2))
					{
						htmltext = "30329-01.html";
					}
					break;
				}
				if ((npc.getId() == TRISKEL) && (talker.getClassId() != ClassId.ASSASSIN))
				{
					if (qs.isCond(3))
					{
						htmltext = "30416-01.html"; // Custom Html
					}
					break;
				}
				if ((npc.getId() == VARIKA) && (talker.getClassId() != ClassId.DARK_WIZARD))
				{
					if (qs.isCond(4))
					{
						htmltext = "30421-01.html";
					}
					break;
				}
				if ((npc.getId() == SIDRA) && (talker.getClassId() != ClassId.DARK_WIZARD))
				{
					if (qs.isCond(5))
					{
						htmltext = "30330-01.html"; // Custom html
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