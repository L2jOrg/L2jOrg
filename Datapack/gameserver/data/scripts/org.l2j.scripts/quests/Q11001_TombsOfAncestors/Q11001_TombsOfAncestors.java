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
package quests.Q11001_TombsOfAncestors;

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
 * Tombs of Ancestors (11001)
 * @author Stayway
 */
public class Q11001_TombsOfAncestors extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30598;
	private static final int ALTRAN = 30283;
	// Items
	private static final int WOLF_PELT = 90200;
	private static final int ORC_AMULET = 90201;
	private static final int WEREWOLFS_FANG = 90202;
	private static final int BROKEN_SWORD = 90203;
	private static final int HUNTERS_MEMO = 90199;
	// Rewards
	private static final int SWORD_OF_SOLIDARITY = 49043;
	private static final int WAND_OF_ADEPT = 49044;
	private static final int RING_NOVICE = 29497;
	private static final int NECKLACE_NOVICE = 49039;
	// Monsters
	private static final int WOLF = 20120;
	private static final int ELDER_WOLF = 20442;
	private static final int ORC = 20130;
	private static final int ORC_SOLDIER = 20131;
	private static final int ORC_ARCHER = 20006;
	private static final int ORC_WARRIOR = 20093;
	private static final int WEREWOLVES = 20132;
	// Misc
	private static final int MIN_LVL = 2;
	private static final int MAX_LVL = 20;
	
	public Q11001_TombsOfAncestors()
	{
		super(11001);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, ALTRAN);
		addKillId(WOLF, ELDER_WOLF, ORC, ORC_SOLDIER, ORC_ARCHER, ORC_WARRIOR, WEREWOLVES);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html");
		addCondRace(Race.HUMAN, "no-race.html");
		registerQuestItems(HUNTERS_MEMO, WOLF_PELT, ORC_AMULET, WEREWOLFS_FANG, BROKEN_SWORD);
		setQuestNameNpcStringId(NpcStringId.LV_2_20_TOMBS_OF_ANCESTORS);
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
			case "30598-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(5))
				{
					takeItems(player, HUNTERS_MEMO, 1);
					takeItems(player, WOLF_PELT, 10);
					takeItems(player, ORC_AMULET, 10);
					takeItems(player, WEREWOLFS_FANG, 10);
					giveItems(player, SWORD_OF_SOLIDARITY, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30283-03.html"; // Need other html
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(5))
				{
					takeItems(player, HUNTERS_MEMO, 1);
					takeItems(player, WOLF_PELT, 10);
					takeItems(player, ORC_AMULET, 10);
					takeItems(player, WEREWOLFS_FANG, 10);
					giveItems(player, WAND_OF_ADEPT, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30283-03.html";
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
				if (npc.getId() == NEWBIE_GUIDE)
				{
					htmltext = "30598-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					if (qs.isCond(1))
					{
						htmltext = "30598-02a.html";
					}
					break;
				}
				else if (npc.getId() == ALTRAN)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30283-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.NOW_YOU_KNOW_WHAT_ALTRAN_WANTS_NGO_HUNTING_AND_KILL_WOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, HUNTERS_MEMO, 1);
							break;
						}
						case 2:
						{
							htmltext = "30283-01a.html";
							break;
						}
						case 5:
						{
							htmltext = "30283-02.html";
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
				case WOLF:
				case ELDER_WOLF:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, WOLF_PELT) < 10))
					{
						if (getRandom(100) < 93)
						{
							giveItems(killer, WOLF_PELT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, WOLF_PELT) >= 10)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_WOLVES_N_GO_HUNTING_AND_KILL_ORCS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case ORC:
				case ORC_SOLDIER:
				case ORC_ARCHER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, ORC_AMULET) < 10))
					{
						if (getRandom(100) < 93)
						{
							giveItems(killer, ORC_AMULET, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ORC_AMULET) >= 10)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_ORCS_NGO_HUNTING_AND_KILL_ORC_WARRIORS_AND_WEREWOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case ORC_WARRIOR:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, BROKEN_SWORD) < 10))
					{
						if (getRandom(100) < 89)
						{
							giveItems(killer, BROKEN_SWORD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, BROKEN_SWORD) >= 10) && (getQuestItemsCount(killer, WEREWOLFS_FANG) >= 10))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_FULFILLED_ALL_ALTRAN_S_REQUESTS_N_RETURN_TO_ALTRAN, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
							}
						}
					}
					break;
				}
				case WEREWOLVES:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, WEREWOLFS_FANG) < 10))
					{
						if (getRandom(100) < 100)
						{
							giveItems(killer, WEREWOLFS_FANG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, WEREWOLFS_FANG) >= 10) && (getQuestItemsCount(killer, BROKEN_SWORD) >= 10))
							{
								showOnScreenMsg(killer, NpcStringId.YOU_FULFILLED_ALL_ALTRAN_S_REQUESTS_N_RETURN_TO_ALTRAN, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
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