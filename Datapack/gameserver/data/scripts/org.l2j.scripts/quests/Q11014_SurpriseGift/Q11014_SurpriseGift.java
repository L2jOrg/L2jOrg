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
package quests.Q11014_SurpriseGift;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Surprise Gift (11014)
 * @author Stayway
 */
public class Q11014_SurpriseGift extends Quest
{
	// NPCs
	private static final int TALOS = 30141;
	private static final int PAYNE = 30136;
	// Items
	private static final int ZOMBIE_FOREST_ELF_TOOTH = 90244;
	private static final int BAT_SKIN = 90245;
	private static final int STONE_GIANTS_SHINY_ROCK = 90246;
	private static final int OLD_BONE_FRAGMENT = 90247;
	private static final int ARMOR_DESIGN = 90243;
	// Rewards
	private static final int WARRIORS_ARMOR = 90306;
	private static final int WARRIORS_GAITERS = 90307;
	private static final int MEDIUMS_TUNIC = 90308;
	private static final int MEDIUMS_STOCKINGS = 90309;
	private static final int RING_NOVICE = 29497;
	// Monsters
	private static final int ZOMBIE_FOREST_ELF = 20015;
	private static final int ZOMBIE_FOREST_ELF_RESEARCHER = 20020;
	private static final int RED_EYE_BAT = 20392;
	private static final int FESTERING_BAT = 20433;
	private static final int GIANT_STONE_SOLDIER = 20379;
	private static final int GIANT_STONE_GUARDIAN = 20380;
	private static final int DARK_HORROR = 20105;
	// Misc
	private static final int MIN_LVL = 11;
	private static final int MAX_LVL = 20;
	
	public Q11014_SurpriseGift()
	{
		super(11014);
		addStartNpc(TALOS);
		addTalkId(PAYNE, TALOS);
		addKillId(ZOMBIE_FOREST_ELF, ZOMBIE_FOREST_ELF_RESEARCHER, FESTERING_BAT, RED_EYE_BAT, GIANT_STONE_GUARDIAN, GIANT_STONE_SOLDIER, DARK_HORROR);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.DARK_ELF, "no-race.html"); // Custom
		registerQuestItems(ARMOR_DESIGN, ZOMBIE_FOREST_ELF_TOOTH, BAT_SKIN, STONE_GIANTS_SHINY_ROCK, OLD_BONE_FRAGMENT);
		setQuestNameNpcStringId(NpcStringId.LV_11_20_SURPRISE_GIFT);
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
			case "30141-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(6))
				{
					takeItems(player, ARMOR_DESIGN, 1);
					takeItems(player, ZOMBIE_FOREST_ELF_TOOTH, 10);
					takeItems(player, BAT_SKIN, 10);
					takeItems(player, STONE_GIANTS_SHINY_ROCK, 10);
					takeItems(player, OLD_BONE_FRAGMENT, 20);
					giveItems(player, WARRIORS_ARMOR, 1);
					giveItems(player, WARRIORS_GAITERS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30136-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(6))
				{
					takeItems(player, ARMOR_DESIGN, 1);
					takeItems(player, ZOMBIE_FOREST_ELF_TOOTH, 10);
					takeItems(player, BAT_SKIN, 10);
					takeItems(player, STONE_GIANTS_SHINY_ROCK, 10);
					takeItems(player, OLD_BONE_FRAGMENT, 20);
					giveItems(player, MEDIUMS_TUNIC, 1);
					giveItems(player, MEDIUMS_STOCKINGS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30136-04.html";
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
				if (npc.getId() == TALOS)
				{
					htmltext = "30141-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == TALOS)
				{
					if (qs.isCond(1))
					{
						htmltext = "30141-02a.html";
					}
					break;
				}
				else if (npc.getId() == PAYNE)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30136-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_ZOMBIE_FOREST_ELVES_AND_ZOMBIE_FOREST_ELF_RESEARCHERS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, ARMOR_DESIGN, 1);
							break;
						}
						case 2:
						{
							htmltext = "30136-01a.html";
							break;
						}
						case 6:
						{
							htmltext = "30136-02.html";
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
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case ZOMBIE_FOREST_ELF:
				case ZOMBIE_FOREST_ELF_RESEARCHER:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, ZOMBIE_FOREST_ELF_TOOTH) < 10))
					{
						if (getRandom(100) < 85)
						{
							giveItems(killer, ZOMBIE_FOREST_ELF_TOOTH, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ZOMBIE_FOREST_ELF_TOOTH) >= 10)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_ZOMBIE_FOREST_ELVES_AND_ZOMBIE_FOREST_ELF_RESEARCHERS_N_GO_HUNTING_AND_KILL_FESTERING_BATS_AND_RED_EYE_BATS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case FESTERING_BAT:
				case RED_EYE_BAT:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, BAT_SKIN) < 10))
					{
						if (getRandom(100) < 85)
						{
							giveItems(killer, BAT_SKIN, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, BAT_SKIN) >= 10)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_FESTERING_BATS_AND_RED_EYE_BATS_N_GO_HUNTING_AND_KILL_STONE_GIANT_SOLDIERS_AND_STONE_GIANT_GUARDIANS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case GIANT_STONE_SOLDIER:
				case GIANT_STONE_GUARDIAN:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, STONE_GIANTS_SHINY_ROCK) < 10))
					{
						if (getRandom(100) < 85)
						{
							giveItems(killer, STONE_GIANTS_SHINY_ROCK, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, STONE_GIANTS_SHINY_ROCK) >= 10))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_STONE_GIANT_SOLDIERS_AND_STONE_GIANT_GUARDIANS_N_GO_HUNTING_AND_KILL_DARK_HORRORS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
							}
						}
					}
					break;
				}
				case DARK_HORROR:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, OLD_BONE_FRAGMENT) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, OLD_BONE_FRAGMENT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, OLD_BONE_FRAGMENT) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_DARK_HORRORS_NRETURN_TO_ARMOR_MERCHANT_PAYNE, ExShowScreenMessage.TOP_CENTER, 10000);
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