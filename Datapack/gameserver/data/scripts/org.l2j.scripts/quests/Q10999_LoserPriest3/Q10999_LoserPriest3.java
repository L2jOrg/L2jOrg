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
package quests.Q10999_LoserPriest3;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10998_LoserPriest2.Q10998_LoserPriest2;

/**
 * Loser Priest (3/3) (10999)
 * @author Stayway
 */
public class Q10999_LoserPriest3 extends Quest
{
	// NPCs
	private static final int GERALD = 30650;
	// Items
	private static final int BAT_BRISTLE = 90303;
	private static final int TRIMDEN_WEB = 90304;
	private static final int KEROPE_WEREWOLF_TWIG_CHARM = 90305;
	private static final int NEW_CLEANING_TOOLS_LIST = 90302;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int RED_EYE_BARBED_BAT = 21124;
	private static final int NORTHERN_TRIMDEN = 21125;
	private static final int KEROPE_WEREWOLF = 21126;
	private static final int KEROPE_WEREWOLF_CHIEFTAIN = 21129;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q10999_LoserPriest3()
	{
		super(10999);
		addStartNpc(GERALD);
		addTalkId(GERALD);
		addKillId(NORTHERN_TRIMDEN, RED_EYE_BARBED_BAT, KEROPE_WEREWOLF_CHIEFTAIN, KEROPE_WEREWOLF);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.DWARF, "no-race.html"); // Custom
		addCondCompletedQuest(Q10998_LoserPriest2.class.getSimpleName(), "30650-05.html");
		registerQuestItems(NEW_CLEANING_TOOLS_LIST, BAT_BRISTLE, TRIMDEN_WEB, KEROPE_WEREWOLF_TWIG_CHARM);
		setQuestNameNpcStringId(NpcStringId.LV_15_LOSER_PRIEST_3_3);
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
			case "abort.html":
			{
				htmltext = event;
				break;
			}
			case "30650-02.htm":
			{
				qs.startQuest();
				qs.setCond(2);
				showOnScreenMsg(player, NpcStringId.GO_HUNTING_AND_KILL_RED_EYE_BARBED_BATS, ExShowScreenMessage.TOP_CENTER, 10000);
				giveItems(player, NEW_CLEANING_TOOLS_LIST, 1);
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(5))
				{
					takeItems(player, NEW_CLEANING_TOOLS_LIST, 1);
					takeItems(player, BAT_BRISTLE, 20);
					takeItems(player, TRIMDEN_WEB, 20);
					takeItems(player, KEROPE_WEREWOLF_TWIG_CHARM, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30650-04.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(5))
				{
					takeItems(player, NEW_CLEANING_TOOLS_LIST, 1);
					takeItems(player, BAT_BRISTLE, 20);
					takeItems(player, TRIMDEN_WEB, 20);
					takeItems(player, KEROPE_WEREWOLF_TWIG_CHARM, 20);
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
	public String onTalk(L2Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30650-01.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(2))
				{
					htmltext = "30650-02a.html";
				}
				else if (qs.isCond(5))
				{
					htmltext = "30650-03.html";
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
				case RED_EYE_BARBED_BAT:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, BAT_BRISTLE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, BAT_BRISTLE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, BAT_BRISTLE) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_RED_EYE_BARBED_BATS_NGO_HUNTING_AND_KILL_NORTHERN_TRIMDENS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case NORTHERN_TRIMDEN:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, TRIMDEN_WEB) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, TRIMDEN_WEB, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, TRIMDEN_WEB) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_NORTHERN_TRIMDENS_N_GO_HUNTING_AND_KILL_KEROPE_WEREWOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case KEROPE_WEREWOLF_CHIEFTAIN:
				case KEROPE_WEREWOLF:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, KEROPE_WEREWOLF_TWIG_CHARM) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, KEROPE_WEREWOLF_TWIG_CHARM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, KEROPE_WEREWOLF_TWIG_CHARM) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_KEROPE_WEREWOLVES_NRETURN_TO_PRIEST_OF_THE_EARTH_GERALD, ExShowScreenMessage.TOP_CENTER, 10000);
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