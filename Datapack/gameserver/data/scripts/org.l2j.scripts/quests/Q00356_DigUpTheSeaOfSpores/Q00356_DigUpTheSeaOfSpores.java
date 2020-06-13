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
package quests.Q00356_DigUpTheSeaOfSpores;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Dig Up the Sea of Spores! (356)
 * @author Adry_85
 */
public final class Q00356_DigUpTheSeaOfSpores extends Quest
{
	// NPC
	private static final int GAUEN = 30717;
	// Items
	private static final int CARNIVORE_SPORE = 5865;
	private static final int HERBIVOROUS_SPORE = 5866;
	// Misc
	private static final int MIN_LEVEL = 43;
	// Monsters
	private static final int ROTTING_TREE = 20558;
	private static final int SPORE_ZOMBIE = 20562;
	private static final Map<Integer, Double> MONSTER_DROP_CHANCES = new HashMap<>();
	static
	{
		MONSTER_DROP_CHANCES.put(ROTTING_TREE, 0.73);
		MONSTER_DROP_CHANCES.put(SPORE_ZOMBIE, 0.94);
	}
	
	public Q00356_DigUpTheSeaOfSpores()
	{
		super(356);
		addStartNpc(GAUEN);
		addTalkId(GAUEN);
		addKillId(ROTTING_TREE, SPORE_ZOMBIE);
		registerQuestItems(HERBIVOROUS_SPORE, CARNIVORE_SPORE);
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
			case "30717-02.htm":
			case "30717-03.htm":
			case "30717-04.htm":
			case "30717-10.html":
			case "30717-18.html":
			{
				htmltext = event;
				break;
			}
			case "30717-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30717-09.html":
			{
				takeItems(player, CARNIVORE_SPORE, -1);
				takeItems(player, HERBIVOROUS_SPORE, -1);
				htmltext = event;
				break;
			}
			case "30717-11.html":
			{
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30717-14.html":
			{
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "FINISH":
			{
				final int value = Rnd.get(100);
				int adena = 0;
				if (value < 20)
				{
					adena = 3000;
					htmltext = "30717-15.html";
				}
				else if (value < 70)
				{
					adena = 1300;
					htmltext = "30717-16.html";
				}
				else
				{
					adena = 1300;
					htmltext = "30717-17.html";
				}
				giveAdena(player, adena, true);
				qs.exitQuest(true, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs == null) || !GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return null;
		}
		
		final int dropItem = ((npc.getId() == ROTTING_TREE) ? HERBIVOROUS_SPORE : CARNIVORE_SPORE);
		final int otherItem = ((dropItem == HERBIVOROUS_SPORE) ? CARNIVORE_SPORE : HERBIVOROUS_SPORE);
		
		if (giveItemRandomly(qs.getPlayer(), npc, dropItem, 1, 100, MONSTER_DROP_CHANCES.get(npc.getId()), true))
		{
			if (getQuestItemsCount(killer, otherItem) >= 100)
			{
				qs.setCond(3);
			}
			else
			{
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30717-01.htm" : "30717-06.htm";
		}
		else if (qs.isStarted())
		{
			final boolean hasAllHerbSpores = (getQuestItemsCount(player, HERBIVOROUS_SPORE) >= 100);
			final boolean hasAllCarnSpores = (getQuestItemsCount(player, CARNIVORE_SPORE) >= 100);
			
			if (hasAllHerbSpores && hasAllCarnSpores)
			{
				htmltext = "30717-13.html";
			}
			else if (hasAllCarnSpores)
			{
				htmltext = "30717-12.html";
			}
			else if (hasAllHerbSpores)
			{
				htmltext = "30717-08.html";
			}
			else
			{
				htmltext = "30717-07.html";
			}
		}
		return htmltext;
	}
}
