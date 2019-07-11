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
package quests.Q00273_InvadersOfTheHolyLand;

import java.util.HashMap;
import java.util.Map;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Invaders of the Holy Land (273)
 * @author xban1x
 */
public final class Q00273_InvadersOfTheHolyLand extends Quest
{
	// NPC
	private static final int VARKEES = 30566;
	// Items
	private static final int BLACK_SOULSTONE = 1475;
	private static final int RED_SOULSTONE = 1476;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(20311, 90); // Rakeclaw Imp
		MONSTERS.put(20312, 87); // Rakeclaw Imp Hunter
		MONSTERS.put(20313, 77); // Rakeclaw Imp Chieftain
	}
	// Misc
	private static final int MIN_LVL = 6;
	
	public Q00273_InvadersOfTheHolyLand()
	{
		super(273);
		addStartNpc(VARKEES);
		addTalkId(VARKEES);
		addKillId(MONSTERS.keySet());
		registerQuestItems(BLACK_SOULSTONE, RED_SOULSTONE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null)
		{
			switch (event)
			{
				case "30566-04.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
				case "30566-08.html":
				{
					st.exitQuest(true, true);
					htmltext = event;
					break;
				}
				case "30566-09.html":
				{
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if (st != null)
		{
			if (getRandom(100) <= MONSTERS.get(npc.getId()))
			{
				giveItems(killer, BLACK_SOULSTONE, 1);
			}
			else
			{
				giveItems(killer, RED_SOULSTONE, 1);
			}
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = null;
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? "30566-03.htm" : "30566-02.htm" : "30566-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, BLACK_SOULSTONE, RED_SOULSTONE))
				{
					final long black = getQuestItemsCount(player, BLACK_SOULSTONE);
					final long red = getQuestItemsCount(player, RED_SOULSTONE);
					giveAdena(player, (red * 5) + (black * 3) + (((red + black) >= 10) ? 1000 : 0), true);
					takeItems(player, -1, BLACK_SOULSTONE, RED_SOULSTONE);
					// Q00281_HeadForTheHills.giveNewbieReward(player);
					htmltext = (red > 0) ? "30566-07.html" : "30566-06.html";
				}
				else
				{
					htmltext = "30566-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
