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
package quests.Q10996_TemplesDecorations;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Temples Decorations (10996)
 * @author Stayway
 */
public class Q10996_TemplesDecorations extends Quest
{
	// NPCs
	private static final int ZIMENF = 30538;
	private static final int REEP = 30516;
	// Items
	private static final int BARBED_BAT_WING_SAC = 90291;
	private static final int PUMA_FUR = 90292;
	private static final int GOBLIN_JEWEL = 90293;
	private static final int GOLEM_ORE = 90294;
	private static final int TEMPLE_RECONSTRUCTION_REQUEST = 90290; // Need finish htm
	// Rewards
	private static final int WARRIORS_ARMOR = 90306;
	private static final int WARRIORS_GAITERS = 90307;
	private static final int MEDIUMS_TUNIC = 90308;
	private static final int MEDIUMS_STOCKINGS = 90309;
	private static final int RING_NOVICE = 29497;
	// Monsters
	private static final int BARBED_BATS = 20370;
	private static final int CRYSTAL_PUMA = 20510;
	private static final int GOBLIN_LORD = 20528;
	private static final int GOBLIN_BRIGAND_LEADER = 20323;
	private static final int WINSTONE_GOLEM = 20521;
	private static final int OBSIDIAN_GOLEM = 20526;
	// Misc
	private static final int MIN_LVL = 11;
	private static final int MAX_LVL = 20;
	
	public Q10996_TemplesDecorations()
	{
		super(10996);
		addStartNpc(REEP);
		addTalkId(ZIMENF, REEP);
		addKillId(BARBED_BATS, CRYSTAL_PUMA, GOBLIN_LORD, GOBLIN_BRIGAND_LEADER, WINSTONE_GOLEM, OBSIDIAN_GOLEM);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.DWARF, "no-race.html"); // Custom
		registerQuestItems(TEMPLE_RECONSTRUCTION_REQUEST, BARBED_BAT_WING_SAC, PUMA_FUR, GOBLIN_JEWEL, GOLEM_ORE);
		setQuestNameNpcStringId(NpcStringId.LV_11_20_TEMPLE_S_DECORATIONS);
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
			case "30516-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(6))
				{
					takeItems(player, TEMPLE_RECONSTRUCTION_REQUEST, 1);
					takeItems(player, BARBED_BAT_WING_SAC, 20);
					takeItems(player, PUMA_FUR, 20);
					takeItems(player, GOBLIN_JEWEL, 20);
					takeItems(player, GOLEM_ORE, 20);
					giveItems(player, WARRIORS_ARMOR, 1);
					giveItems(player, WARRIORS_GAITERS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30538-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(6))
				{
					takeItems(player, TEMPLE_RECONSTRUCTION_REQUEST, 1);
					takeItems(player, BARBED_BAT_WING_SAC, 20);
					takeItems(player, PUMA_FUR, 20);
					takeItems(player, GOBLIN_JEWEL, 20);
					takeItems(player, GOLEM_ORE, 20);
					giveItems(player, MEDIUMS_TUNIC, 1);
					giveItems(player, MEDIUMS_STOCKINGS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30538-04.html";
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
				if (npc.getId() == REEP)
				{
					htmltext = "30516-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == REEP)
				{
					if (qs.isCond(1))
					{
						htmltext = "30516-02a.html";
					}
					break;
				}
				else if (npc.getId() == ZIMENF)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30538-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_BARBED_BATS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, TEMPLE_RECONSTRUCTION_REQUEST, 1);
							break;
						}
						case 2:
						{
							htmltext = "30538-01a.html";
							break;
						}
						case 6:
						{
							htmltext = "30538-02.html";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case BARBED_BATS:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, BARBED_BAT_WING_SAC) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, BARBED_BAT_WING_SAC, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, BARBED_BAT_WING_SAC) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_BARBED_BATS_N_GO_HUNTING_AND_KILL_CRYSTAL_PUMAS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case CRYSTAL_PUMA:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, PUMA_FUR) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, PUMA_FUR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, PUMA_FUR) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_CRYSTAL_PUMAS_N_GO_HUNTING_AND_KILL_GOBLIN_LORDS_AND_GOBLIN_BRIGAND_LEADERS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case GOBLIN_LORD:
				case GOBLIN_BRIGAND_LEADER:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, GOBLIN_JEWEL) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, GOBLIN_JEWEL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, GOBLIN_JEWEL) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_LORDS_AND_GOBLIN_BRIGAND_LEADERS_N_GO_HUNTING_AND_KILL_WHINSTONE_GOLEM_AND_OBSIDIAN_GOLEMS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
							}
						}
					}
					break;
				}
				case WINSTONE_GOLEM:
				case OBSIDIAN_GOLEM:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, GOLEM_ORE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, GOLEM_ORE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, GOLEM_ORE) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_WHINSTONE_GOLEM_AND_OBSIDIAN_GOLEMS_NRETURN_TO_PRIEST_OF_THE_EARTH_ZIMENF, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(6);
							}
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}