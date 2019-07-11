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
package quests.Q11007_NoiseInWoods;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;

/**
 * Noise in Woods (11007)
 * @author Stayway
 */
public class Q11007_NoiseInWoods extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30599;
	private static final int KENDELL = 30218;
	// Items
	private static final int TAIL_OF_A_GRAY_WOLF = 90218;
	private static final int GOBLINS_PACK_OF_KNICKKNACKS = 90219;
	private static final int KABBO_ORC_STURDY_AMULET = 90220;
	private static final int MUSHROOM_SPORE_POWDER = 90221;
	private static final int MARK_OF_SECURITY = 90217;
	// Rewards
	private static final int RED_SUNSET_SWORD = 49046;
	private static final int RED_SUNSET_STAFF = 49045;
	private static final int RING_NOVICE = 29497;
	private static final int NECKLACE_NOVICE = 49039;
	// Monsters
	private static final int GRAY_WOLF = 20525;
	private static final int GOBLIN_RAIDER = 20325;
	private static final int KABOO_ORC = 20468;
	private static final int KABOO_ORC_ARCHER = 20469;
	private static final int KABOO_ORC_SOLDIER = 20470;
	private static final int SPORE_FUNGUS = 20509;
	// Misc
	private static final int MIN_LVL = 2;
	private static final int MAX_LVL = 20;
	
	public Q11007_NoiseInWoods()
	{
		super(11007);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, KENDELL);
		addKillId(GRAY_WOLF, GOBLIN_RAIDER, KABOO_ORC, KABOO_ORC_ARCHER, KABOO_ORC_SOLDIER, SPORE_FUNGUS);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html");
		addCondRace(Race.ELF, "no-race.html");
		registerQuestItems(MARK_OF_SECURITY, TAIL_OF_A_GRAY_WOLF, GOBLINS_PACK_OF_KNICKKNACKS, KABBO_ORC_STURDY_AMULET, MUSHROOM_SPORE_POWDER);
		setQuestNameNpcStringId(NpcStringId.LV_2_20_NOISE_IN_WOODS);
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
			case "30599-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(6))
				{
					takeItems(player, MARK_OF_SECURITY, 1);
					takeItems(player, TAIL_OF_A_GRAY_WOLF, 10);
					takeItems(player, GOBLINS_PACK_OF_KNICKKNACKS, 10);
					takeItems(player, KABBO_ORC_STURDY_AMULET, 10);
					takeItems(player, MUSHROOM_SPORE_POWDER, 20);
					giveItems(player, RED_SUNSET_SWORD, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30218-04.html"; // Need retail sword html
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(6))
				{
					takeItems(player, MARK_OF_SECURITY, 1);
					takeItems(player, TAIL_OF_A_GRAY_WOLF, 10);
					takeItems(player, GOBLINS_PACK_OF_KNICKKNACKS, 10);
					takeItems(player, KABBO_ORC_STURDY_AMULET, 10);
					takeItems(player, MUSHROOM_SPORE_POWDER, 20);
					giveItems(player, RED_SUNSET_STAFF, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30218-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case GRAY_WOLF:
				{
					if ((qs.isCond(2) && (getQuestItemsCount(killer, TAIL_OF_A_GRAY_WOLF) < 10)))
					{
						giveItems(killer, TAIL_OF_A_GRAY_WOLF, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, TAIL_OF_A_GRAY_WOLF) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GRAY_WOLVES_N_GO_HUNTING_AND_KILL_GOBLIN_RAIDERS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(3);
						}
					}
					break;
				}
				case GOBLIN_RAIDER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, GOBLINS_PACK_OF_KNICKKNACKS) < 10))
					{
						giveItems(killer, GOBLINS_PACK_OF_KNICKKNACKS, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, GOBLINS_PACK_OF_KNICKKNACKS) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_RAIDERS_N_GO_HUNTING_AND_KILL_KABOO_ORCS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(4);
						}
					}
					break;
				}
				case KABOO_ORC:
				case KABOO_ORC_ARCHER:
				case KABOO_ORC_SOLDIER:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, KABBO_ORC_STURDY_AMULET) < 10))
					{
						giveItems(killer, KABBO_ORC_STURDY_AMULET, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, KABBO_ORC_STURDY_AMULET) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_KABOO_ORCS_N_GO_HUNTING_AND_KILL_SPORE_FUNGUS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(5);
						}
					}
					break;
				}
				case SPORE_FUNGUS:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, MUSHROOM_SPORE_POWDER) < 20))
					{
						giveItems(killer, MUSHROOM_SPORE_POWDER, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, MUSHROOM_SPORE_POWDER) >= 20)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_SPORE_FUNGUS_N_RETURN_TO_SENTINEL_KENDELL, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(6);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
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
					htmltext = "30599-01.html";
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
							htmltext = "30599-02a.html";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == KENDELL)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30218-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_GRAY_WOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, MARK_OF_SECURITY, 1);
							break;
						}
						case 2:
						{
							htmltext = "30218-01a.html";
							break;
						}
						case 6:
						{
							htmltext = "30218-02.html";
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