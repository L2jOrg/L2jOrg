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
package quests.Q00319_ScentOfDeath;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.util.GameUtils;

/**
 * Scent of Death (319)
 * @author Zoey76
 */
public class Q00319_ScentOfDeath extends Quest
{
	// NPC
	private static final int MINALESS = 30138;
	// Monsters
	private static final int MARSH_ZOMBIE = 20015;
	private static final int MARSH_ZOMBIE_LORD = 20020;
	// Item
	private static final int ZOMBIES_SKIN = 1045;
	// Misc
	private static final int MIN_LEVEL = 11;
	private static final int MIN_CHANCE = 7;
	private static final int REQUIRED_ITEM_COUNT = 5;
	
	public Q00319_ScentOfDeath()
	{
		super(319);
		addStartNpc(MINALESS);
		addTalkId(MINALESS);
		addKillId(MARSH_ZOMBIE, MARSH_ZOMBIE_LORD);
		registerQuestItems(ZOMBIES_SKIN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		if (player.getLevel() >= MIN_LEVEL)
		{
			switch (event)
			{
				case "30138-04.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, false) && (getQuestItemsCount(killer, ZOMBIES_SKIN) < REQUIRED_ITEM_COUNT))
		{
			if (getRandom(10) > MIN_CHANCE)
			{
				giveItems(killer, ZOMBIES_SKIN, 1);
				if (getQuestItemsCount(killer, ZOMBIES_SKIN) < REQUIRED_ITEM_COUNT)
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					st.setCond(2, true);
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
				htmltext = player.getLevel() >= MIN_LEVEL ? "30138-03.htm" : "30138-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30138-05.html";
						break;
					}
					case 2:
					{
						giveAdena(player, 500, false);
						takeItems(player, ZOMBIES_SKIN, -1);
						st.exitQuest(true, true);
						htmltext = "30138-06.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
