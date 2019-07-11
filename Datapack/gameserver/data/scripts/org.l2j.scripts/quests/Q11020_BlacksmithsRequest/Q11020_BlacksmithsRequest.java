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
package quests.Q11020_BlacksmithsRequest;

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
 * Blacksmith's Request (11020)
 * @author Stayway
 */
public class Q11020_BlacksmithsRequest extends Quest
{
	// NPCs
	private static final int TIKU = 30582;
	private static final int SUMARI = 30564;
	// Items
	private static final int BLACKWING_BAT_WING = 90269;
	private static final int GRAVE_ROBBERS_BELT = 90270;
	private static final int GOLEM_ORE = 90271;
	private static final int EVIL_EYE_PATROL_HIDE = 90272;
	private static final int REQUIRED_MATERIALS = 90268;
	// Rewards
	private static final int WARRIORS_ARMOR = 90306;
	private static final int WARRIORS_GAITERS = 90307;
	private static final int MEDIUMS_TUNIC = 90308;
	private static final int MEDIUMS_STOCKINGS = 90309;
	private static final int EARRING_NOVICE = 49040;
	// Monsters
	private static final int BLACKWING_BAT = 20316;
	private static final int TOMB_RAIDER_LEADER = 20320;
	private static final int GREYSTONE_GOLEM = 20333;
	private static final int EVIL_EYE_PATROL = 20428;
	// Misc
	private static final int MIN_LVL = 11;
	private static final int MAX_LVL = 20;
	
	public Q11020_BlacksmithsRequest()
	{
		super(11020);
		addStartNpc(TIKU);
		addTalkId(SUMARI, TIKU);
		addKillId(BLACKWING_BAT, TOMB_RAIDER_LEADER, GREYSTONE_GOLEM, EVIL_EYE_PATROL);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.ORC, "no-race.html"); // Custom
		registerQuestItems(REQUIRED_MATERIALS, BLACKWING_BAT_WING, GRAVE_ROBBERS_BELT, GOLEM_ORE, EVIL_EYE_PATROL_HIDE);
		setQuestNameNpcStringId(NpcStringId.LV_11_20_BLACKSMITH_S_REQUEST);
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
			case "30582-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(6))
				{
					takeItems(player, REQUIRED_MATERIALS, 1);
					takeItems(player, BLACKWING_BAT_WING, 20);
					takeItems(player, GRAVE_ROBBERS_BELT, 20);
					takeItems(player, GOLEM_ORE, 20);
					takeItems(player, EVIL_EYE_PATROL_HIDE, 20);
					giveItems(player, WARRIORS_ARMOR, 1);
					giveItems(player, WARRIORS_GAITERS, 1);
					giveItems(player, EARRING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30564-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(6))
				{
					takeItems(player, REQUIRED_MATERIALS, 1);
					takeItems(player, BLACKWING_BAT_WING, 20);
					takeItems(player, GRAVE_ROBBERS_BELT, 20);
					takeItems(player, GOLEM_ORE, 20);
					takeItems(player, EVIL_EYE_PATROL_HIDE, 20);
					giveItems(player, MEDIUMS_TUNIC, 1);
					giveItems(player, MEDIUMS_STOCKINGS, 1);
					giveItems(player, EARRING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30564-04.html";
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
				if (npc.getId() == TIKU)
				{
					htmltext = "30582-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == TIKU)
				{
					if (qs.isCond(1))
					{
						htmltext = "30582-02a.html";
					}
					break;
				}
				else if (npc.getId() == SUMARI)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30564-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_BLACKWING_BATS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, REQUIRED_MATERIALS, 1);
							break;
						}
						case 2:
						{
							htmltext = "30564-01a.html";
							break;
						}
						case 6:
						{
							htmltext = "30564-02.html";
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
				case BLACKWING_BAT:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, BLACKWING_BAT_WING) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, BLACKWING_BAT_WING, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, BLACKWING_BAT_WING) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_BLACKWING_BATS_N_GO_HUNTING_AND_KILL_GOBLIN_TOMB_RAIDER_LEADERS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case TOMB_RAIDER_LEADER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, GRAVE_ROBBERS_BELT) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, GRAVE_ROBBERS_BELT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, GRAVE_ROBBERS_BELT) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_TOMB_RAIDER_LEADERS_N_GO_HUNTING_AND_KILL_GREYSTONE_GOLEMS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case GREYSTONE_GOLEM:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, GOLEM_ORE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, GOLEM_ORE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, GOLEM_ORE) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GREYSTONE_GOLEMS_N_GO_HUNTING_AND_KILL_EVIL_EYE_PATROLS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
							}
						}
					}
					break;
				}
				case EVIL_EYE_PATROL:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, EVIL_EYE_PATROL_HIDE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, EVIL_EYE_PATROL_HIDE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, EVIL_EYE_PATROL_HIDE) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_EVIL_EYE_PATROLS_NRETURN_TO_BLACKSMITH_SUMARI, ExShowScreenMessage.TOP_CENTER, 10000);
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