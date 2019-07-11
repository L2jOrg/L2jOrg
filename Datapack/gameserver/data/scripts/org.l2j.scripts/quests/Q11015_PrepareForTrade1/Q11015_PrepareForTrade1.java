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
package quests.Q11015_PrepareForTrade1;

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
 * Prepare for Trade (1/3) (11015)
 * @author Stayway
 */
public class Q11015_PrepareForTrade1 extends Quest
{
	// NPCs
	private static final int PAYNE = 30136;
	private static final int VOLLODOS = 30137;
	// Items
	private static final int STONE_GIANTS_GUARDIANS_CORE = 90250;
	private static final int CRYSTALLINE_BEASTS_SHINEDUST = 90251;
	private static final int GIANT_SPIDER_SKIN_FRAGMENT = 90252;
	private static final int SUPPLIES_CERTIFICATE = 90249;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int STONE_GIANT_GUARDIANS = 20380;
	private static final int CRYSTALLINE_BEAST = 20418;
	private static final int PROWLER = 20034;
	private static final int GIANT_VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_TRACKER = 20043;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q11015_PrepareForTrade1()
	{
		super(11015);
		addStartNpc(PAYNE);
		addTalkId(PAYNE, VOLLODOS);
		addKillId(STONE_GIANT_GUARDIANS, CRYSTALLINE_BEAST, PROWLER, GIANT_VENOMOUS_SPIDER, ARACHNID_TRACKER);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.DARK_ELF, "no-race.html"); // Custom
		registerQuestItems(SUPPLIES_CERTIFICATE, STONE_GIANTS_GUARDIANS_CORE, CRYSTALLINE_BEASTS_SHINEDUST, GIANT_SPIDER_SKIN_FRAGMENT);
		setQuestNameNpcStringId(NpcStringId.LV_15_20_PREPARE_FOR_TRADE_1_3);
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
			case "30136-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(5))
				{
					takeItems(player, SUPPLIES_CERTIFICATE, 1);
					takeItems(player, STONE_GIANTS_GUARDIANS_CORE, 20);
					takeItems(player, CRYSTALLINE_BEASTS_SHINEDUST, 10);
					takeItems(player, GIANT_SPIDER_SKIN_FRAGMENT, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30137-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(5))
				{
					takeItems(player, SUPPLIES_CERTIFICATE, 1);
					takeItems(player, STONE_GIANTS_GUARDIANS_CORE, 20);
					takeItems(player, CRYSTALLINE_BEASTS_SHINEDUST, 10);
					takeItems(player, GIANT_SPIDER_SKIN_FRAGMENT, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SPIRITSHOT_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30137-04.html";
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
				if (npc.getId() == PAYNE)
				{
					htmltext = "30136-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == PAYNE)
				{
					if (qs.isCond(1))
					{
						htmltext = "30136-02a.html";
					}
					break;
				}
				else if (npc.getId() == VOLLODOS)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30137-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_STONE_GIANT_GUARDIANS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, SUPPLIES_CERTIFICATE, 1);
							break;
						}
						case 2:
						{
							htmltext = "30137-01a.html";
							break;
						}
						case 5:
						{
							htmltext = "30137-02.html";
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
				case STONE_GIANT_GUARDIANS:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, STONE_GIANTS_GUARDIANS_CORE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, STONE_GIANTS_GUARDIANS_CORE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, STONE_GIANTS_GUARDIANS_CORE) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_STONE_GIANT_GUARDIANS_N_GO_HUNTING_AND_KILL_CRYSTALLINE_BEASTS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case CRYSTALLINE_BEAST:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, CRYSTALLINE_BEASTS_SHINEDUST) < 10))
					{
						if (getRandom(100) < 87)
						{
							giveItems(killer, CRYSTALLINE_BEASTS_SHINEDUST, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, CRYSTALLINE_BEASTS_SHINEDUST) >= 10)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_CRYSTALLINE_BEASTS_N_GO_HUNTING_AND_KILL_PROWLERS_GIANT_VENOMOUS_SPIDERS_AND_ARACHNID_TRACKERS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case PROWLER:
				case GIANT_VENOMOUS_SPIDER:
				case ARACHNID_TRACKER:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, GIANT_SPIDER_SKIN_FRAGMENT) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, GIANT_SPIDER_SKIN_FRAGMENT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, GIANT_SPIDER_SKIN_FRAGMENT) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_PROWLERS_GIANT_VENOMOUS_SPIDERS_AND_ARACHNID_TRACKERS_NRETURN_TO_GROCER_VOLLODOS, ExShowScreenMessage.TOP_CENTER, 10000);
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