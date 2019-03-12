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
package quests.Q10997_LoserPriest1;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Loser Priest (1/3) (10997)
 * @author Stayway
 */
public class Q10997_LoserPriest1 extends Quest
{
	// NPCs
	private static final int ZIMENF = 30538;
	private static final int GERALD = 30650;
	// Items
	private static final int HUNTER_TARANTULA_VENOM = 90297;
	private static final int PLUNDER_TARANTULA_KIDNEY = 90298;
	private static final int MAINTENANCE_REQUEST = 90296;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int HUNTER_TARANTULA = 20403;
	private static final int PLUNDER_TARANTULA = 20508;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q10997_LoserPriest1()
	{
		super(10997);
		addStartNpc(ZIMENF);
		addTalkId(ZIMENF, GERALD);
		addKillId(PLUNDER_TARANTULA, HUNTER_TARANTULA);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.DWARF, "no-race.html"); // Custom
		registerQuestItems(MAINTENANCE_REQUEST, HUNTER_TARANTULA_VENOM, PLUNDER_TARANTULA_KIDNEY);
		setQuestNameNpcStringId(NpcStringId.LV_15_20_LOSER_PRIEST_1_3);
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
			case "30538-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MAINTENANCE_REQUEST, 1);
					takeItems(player, HUNTER_TARANTULA_VENOM, 20);
					takeItems(player, PLUNDER_TARANTULA_KIDNEY, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30650-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MAINTENANCE_REQUEST, 1);
					takeItems(player, HUNTER_TARANTULA_VENOM, 20);
					takeItems(player, PLUNDER_TARANTULA_KIDNEY, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SPIRITSHOT_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30650-04.html";
				}
				break;
			}
		}
		return htmltext;
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
				if (npc.getId() == ZIMENF)
				{
					htmltext = "30538-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == ZIMENF)
				{
					if (qs.isCond(1))
					{
						htmltext = "30538-02a.html";
					}
					break;
				}
				else if (npc.getId() == GERALD)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30650-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_HUNTER_TARANTULAS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, MAINTENANCE_REQUEST, 1);
							break;
						}
						case 2:
						{
							htmltext = "30650-01a.html";
							break;
						}
						case 4:
						{
							htmltext = "30650-02.html";
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
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case HUNTER_TARANTULA:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, HUNTER_TARANTULA_VENOM) < 20))
					{
						if (getRandom(100) < 94)
						{
							giveItems(killer, HUNTER_TARANTULA_VENOM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, HUNTER_TARANTULA_VENOM) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_HUNTER_TARANTULAS_N_GO_HUNTING_AND_KILL_PLUNDER_TARANTULAS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case PLUNDER_TARANTULA:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, PLUNDER_TARANTULA_KIDNEY) < 20))
					{
						if (getRandom(100) < 94)
						{
							giveItems(killer, PLUNDER_TARANTULA_KIDNEY, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, PLUNDER_TARANTULA_KIDNEY) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_PLUNDER_TARANTULAS_NRETURN_TO_PRIEST_OF_THE_EARTH_GERALD, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
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