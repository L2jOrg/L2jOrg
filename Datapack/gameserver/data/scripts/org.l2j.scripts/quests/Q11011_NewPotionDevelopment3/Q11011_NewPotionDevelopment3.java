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
package quests.Q11011_NewPotionDevelopment3;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q11010_NewPotionDevelopment2.Q11010_NewPotionDevelopment2;

/**
 * New Potion Development (3/3) (11011)
 * @author Stayway
 */
public class Q11011_NewPotionDevelopment3 extends Quest
{
	// NPCs
	private static final int HERBIEL = 30150;
	// Items
	private static final int ANTIDOTE = 90235;
	private static final int ARACHNID_TRACKER_THORN = 90236;
	private static final int MEDICATIONS_RESEARCH = 90234;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int RATMAN_SCAVENGER = 20039;
	private static final int ARACHNID_TRACKER = 20043;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q11011_NewPotionDevelopment3()
	{
		super(11011);
		addStartNpc(HERBIEL);
		addTalkId(HERBIEL);
		addKillId(RATMAN_SCAVENGER, ARACHNID_TRACKER);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.ELF, "no-race.html"); // Custom
		addCondCompletedQuest(Q11010_NewPotionDevelopment2.class.getSimpleName(), "30150-05.html");
		registerQuestItems(MEDICATIONS_RESEARCH, ANTIDOTE, ARACHNID_TRACKER_THORN);
		setQuestNameNpcStringId(NpcStringId.LV_15_NEW_POTION_DEVELOPMENT_3_3);
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
			case "abort.html":
			{
				htmltext = event;
				break;
			}
			case "30150-02.htm":
			{
				qs.startQuest();
				qs.setCond(2, true);
				showOnScreenMsg(player, NpcStringId.GO_HUNTING_AND_KILL_RATMAN_SCAVENGERS, ExShowScreenMessage.TOP_CENTER, 10000);
				giveItems(player, MEDICATIONS_RESEARCH, 1);
				htmltext = event;
				
				break;
			}
			case "reward1":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MEDICATIONS_RESEARCH, 1);
					takeItems(player, ANTIDOTE, 20);
					takeItems(player, ARACHNID_TRACKER_THORN, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30150-04.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MEDICATIONS_RESEARCH, 1);
					takeItems(player, ANTIDOTE, 20);
					takeItems(player, ARACHNID_TRACKER_THORN, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SPIRITSHOT_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30150-04.html";
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
				htmltext = "30150-01.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(2))
				{
					htmltext = "30150-02a.html";
				}
				else if (qs.isCond(4))
				{
					htmltext = "30150-03.html";
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
				case RATMAN_SCAVENGER:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, ANTIDOTE) < 20))
					{
						if (getRandom(100) < 95)
						{
							giveItems(killer, ANTIDOTE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ANTIDOTE) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_RATMAN_SCAVENGERS_N_GO_HUNTING_AND_KILL_ARACHNID_TRACKERS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case ARACHNID_TRACKER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, ARACHNID_TRACKER_THORN) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, ARACHNID_TRACKER_THORN, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ARACHNID_TRACKER_THORN) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.RETURN_TO_GROCER_HERBIEL_3, ExShowScreenMessage.TOP_CENTER, 10000);
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