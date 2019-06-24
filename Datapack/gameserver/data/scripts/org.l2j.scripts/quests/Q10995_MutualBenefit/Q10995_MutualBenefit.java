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
package quests.Q10995_MutualBenefit;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;

/**
 * Mutual Benefit (10995)
 * @author Stayway
 */
public class Q10995_MutualBenefit extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30601;
	private static final int REEP = 30516;
	// Items
	private static final int BALCK_WOLF_TOOTH = 90284;
	private static final int GOBLINS_NAVIGATION_DEVICE = 90285;
	private static final int UTUKU_ORC_AMULET = 90286;
	private static final int GOBLIN_BRIGANDS_OLD_SWORD = 90287;
	private static final int GARUM_WEREWOLF_TAIL = 90288;
	private static final int GOBLIN_BRIGAND_LIEUTENANT_NECKLACE = 90289;
	private static final int BOUNTY_POSTER = 90283;
	// Rewards
	private static final int SILVERSMITH_HAMMER = 49053;
	private static final int RING_NOVICE = 29497;
	private static final int NECKLACE_NOVICE = 49039;
	// Monsters
	private static final int BLACK_WOLF = 20317;
	private static final int GOBLIN_SNOOPER = 20327;
	private static final int UTUKU_ORC = 20446;
	private static final int UTUKU_ORC_ARCHER = 20447;
	private static final int GOBLIN_BRIGAND = 20322;
	private static final int GARUM_WEREWOLF = 20307;
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 20324;
	// Misc
	private static final int MIN_LVL = 2;
	private static final int MAX_LVL = 20;
	
	public Q10995_MutualBenefit()
	{
		super(10995);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, REEP);
		addKillId(BLACK_WOLF, GOBLIN_SNOOPER, UTUKU_ORC, UTUKU_ORC_ARCHER, GOBLIN_BRIGAND, GARUM_WEREWOLF, GOBLIN_BRIGAND_LIEUTENANT);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html");
		addCondRace(Race.DWARF, "no-race.html");
		registerQuestItems(BOUNTY_POSTER, BALCK_WOLF_TOOTH, GOBLINS_NAVIGATION_DEVICE, UTUKU_ORC_AMULET, GOBLIN_BRIGANDS_OLD_SWORD, GARUM_WEREWOLF_TAIL, GOBLIN_BRIGAND_LIEUTENANT_NECKLACE);
		setQuestNameNpcStringId(NpcStringId.LV_2_20_MUTUAL_BENEFIT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30601-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(7))
				{
					takeItems(player, BOUNTY_POSTER, 1);
					takeItems(player, BALCK_WOLF_TOOTH, 10);
					takeItems(player, GOBLINS_NAVIGATION_DEVICE, 10);
					takeItems(player, UTUKU_ORC_AMULET, 10);
					takeItems(player, GOBLIN_BRIGANDS_OLD_SWORD, 10);
					takeItems(player, GARUM_WEREWOLF_TAIL, 10);
					giveItems(player, SILVERSMITH_HAMMER, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30516-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case BLACK_WOLF:
				{
					if ((qs.isCond(2) && (getQuestItemsCount(killer, BALCK_WOLF_TOOTH) < 10)))
					{
						giveItems(killer, BALCK_WOLF_TOOTH, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if ((getQuestItemsCount(killer, BALCK_WOLF_TOOTH) >= 10) && (getQuestItemsCount(killer, GOBLINS_NAVIGATION_DEVICE) >= 10))
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_BLACK_WOLVES_AND_GOBLIN_SNOOPERS_NGO_HUNTING_AND_KILL_UTUKU_ORCS_AND_UTUKU_ORC_ARCHERS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(3);
						}
					}
					break;
				}
				case GOBLIN_SNOOPER:
				{
					if ((qs.isCond(2) && (getQuestItemsCount(killer, GOBLINS_NAVIGATION_DEVICE) < 10)))
					{
						giveItems(killer, GOBLINS_NAVIGATION_DEVICE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if ((getQuestItemsCount(killer, BALCK_WOLF_TOOTH) >= 10) && (getQuestItemsCount(killer, GOBLINS_NAVIGATION_DEVICE) >= 10))
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_BLACK_WOLVES_AND_GOBLIN_SNOOPERS_NGO_HUNTING_AND_KILL_UTUKU_ORCS_AND_UTUKU_ORC_ARCHERS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(3);
						}
					}
					break;
				}
				case UTUKU_ORC:
				case UTUKU_ORC_ARCHER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, UTUKU_ORC_AMULET) < 10))
					{
						giveItems(killer, UTUKU_ORC_AMULET, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, UTUKU_ORC_AMULET) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_UTUKU_ORCS_AND_UTUKU_ORC_ARCHERS_NGO_HUNTING_AND_KILL_GOBLIN_BRIGANDS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(4);
						}
					}
					break;
				}
				case GOBLIN_BRIGAND:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, GOBLIN_BRIGANDS_OLD_SWORD) < 10))
					{
						giveItems(killer, GOBLIN_BRIGANDS_OLD_SWORD, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, GOBLIN_BRIGANDS_OLD_SWORD) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_BRIGANDS_N_GO_HUNTING_AND_KILL_GARUM_WEREWOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(5);
						}
					}
					break;
				}
				case GARUM_WEREWOLF:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, GARUM_WEREWOLF_TAIL) < 10))
					{
						giveItems(killer, GARUM_WEREWOLF_TAIL, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, GARUM_WEREWOLF_TAIL) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GARUM_WEREWOLVES_N_GO_HUNTING_AND_KILL_GOBLIN_BRIGAND_LIEUTENANTS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(6);
						}
					}
					break;
				}
				case GOBLIN_BRIGAND_LIEUTENANT:
				{
					if (qs.isCond(6) && (getQuestItemsCount(killer, GOBLIN_BRIGAND_LIEUTENANT_NECKLACE) < 10))
					{
						giveItems(killer, GOBLIN_BRIGAND_LIEUTENANT_NECKLACE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, GOBLIN_BRIGAND_LIEUTENANT_NECKLACE) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_BRIGAND_LIEUTENANTS_NRETURN_TO_WEAPON_MERCHANT_REEP, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(7);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					htmltext = "30601-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30601-02a.html";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == REEP)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30516-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_BLACK_WOLVES_AND_GOBLIN_SNOOPERS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, BOUNTY_POSTER, 1);
							break;
						}
						case 2:
						{
							htmltext = "30516-01a.html";
							break;
						}
						case 7:
						{
							htmltext = "30516-02.html";
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
}