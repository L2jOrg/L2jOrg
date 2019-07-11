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
package quests.Q00274_SkirmishWithTheWerewolves;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * Skirmish with the Werewolves (274)
 * @author xban1x
 */
public final class Q00274_SkirmishWithTheWerewolves extends Quest
{
	// NPC
	private static final int BRUKURSE = 30569;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20363, // Maraku Werewolf
		20364, // Maraku Werewolf Chieftain
	};
	// Items
	private static final int NECKLACE_OF_COURAGE = 1506;
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int WEREWOLF_HEAD = 1477;
	private static final int WEREWOLF_TOTEM = 1501;
	// Misc
	private static final int MIN_LVL = 9;
	
	public Q00274_SkirmishWithTheWerewolves()
	{
		super(274);
		addStartNpc(BRUKURSE);
		addTalkId(BRUKURSE);
		addKillId(MONSTERS);
		registerQuestItems(WEREWOLF_HEAD, WEREWOLF_TOTEM);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30569-04.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1))
		{
			giveItems(killer, WEREWOLF_HEAD, 1);
			if (getRandom(100) <= 5)
			{
				giveItems(killer, WEREWOLF_TOTEM, 1);
			}
			if (getQuestItemsCount(killer, WEREWOLF_HEAD) >= 40)
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
				if (hasAtLeastOneQuestItem(player, NECKLACE_OF_VALOR, NECKLACE_OF_COURAGE))
				{
					htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? "30569-03.htm" : "30569-02.html" : "30569-01.html";
				}
				else
				{
					htmltext = "30569-08.html";
				}
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30569-05.html";
						break;
					}
					case 2:
					{
						final long heads = getQuestItemsCount(player, WEREWOLF_HEAD);
						if (heads >= 40)
						{
							final long totems = getQuestItemsCount(player, WEREWOLF_TOTEM);
							giveAdena(player, 200, true);
							st.exitQuest(true, true);
							htmltext = (totems > 0) ? "30569-07.html" : "30569-06.html";
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
