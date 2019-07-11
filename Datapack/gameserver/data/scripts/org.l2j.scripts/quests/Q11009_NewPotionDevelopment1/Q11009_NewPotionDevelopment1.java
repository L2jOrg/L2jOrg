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
package quests.Q11009_NewPotionDevelopment1;

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
 * Prepare for New Potion Development (1/3) (11009)
 * @author Stayway
 */
public class Q11009_NewPotionDevelopment1 extends Quest
{
	// NPCs
	private static final int STARDEN = 30220;
	private static final int HERBIEL = 30150;
	// Items
	private static final int SPIDER_ICHOR = 90229;
	private static final int MOONSTONE_BEAST_SCALES = 90230;
	private static final int MEDICINE_RESEARCH = 90228;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int SCAVENGER_SPIDER = 20410;
	private static final int RED_SCAVENGER_SPIDER = 20393;
	private static final int MOONSTONE_BEAST = 20369;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q11009_NewPotionDevelopment1()
	{
		super(11009);
		addStartNpc(STARDEN);
		addTalkId(STARDEN, HERBIEL);
		addKillId(SCAVENGER_SPIDER, RED_SCAVENGER_SPIDER, MOONSTONE_BEAST);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.ELF, "no-race.html"); // Custom
		registerQuestItems(MEDICINE_RESEARCH, SPIDER_ICHOR, MOONSTONE_BEAST_SCALES);
		setQuestNameNpcStringId(NpcStringId.LV_15_20_NEW_POTION_DEVELOPMENT_1_3);
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
			case "30220-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MEDICINE_RESEARCH, 1);
					takeItems(player, SPIDER_ICHOR, 20);
					takeItems(player, MOONSTONE_BEAST_SCALES, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30150-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(4))
				{
					takeItems(player, MEDICINE_RESEARCH, 1);
					takeItems(player, SPIDER_ICHOR, 20);
					takeItems(player, MOONSTONE_BEAST_SCALES, 20);
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
				if (npc.getId() == STARDEN)
				{
					htmltext = "30220-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == STARDEN)
				{
					if (qs.isCond(1))
					{
						htmltext = "30220-02a.html";
					}
					break;
				}
				else if (npc.getId() == HERBIEL)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30150-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_SCAVENGER_SPIDERS_AND_RED_SCAVENGER_SPIDERS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, MEDICINE_RESEARCH, 1);
							break;
						}
						case 2:
						{
							htmltext = "30150-01a.html";
							break;
						}
						case 4:
						{
							htmltext = "30150-02.html";
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
				case SCAVENGER_SPIDER:
				case RED_SCAVENGER_SPIDER:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, SPIDER_ICHOR) < 20))
					{
						if (getRandom(100) < 92)
						{
							giveItems(killer, SPIDER_ICHOR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, SPIDER_ICHOR) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_SCAVENGER_SPIDERS_AND_RED_SCAVENGER_SPIDERS_N_GO_HUNTING_AND_KILL_MOONSTONE_BEASTS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case MOONSTONE_BEAST:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, MOONSTONE_BEAST_SCALES) < 20))
					{
						if (getRandom(100) < 92)
						{
							giveItems(killer, MOONSTONE_BEAST_SCALES, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, MOONSTONE_BEAST_SCALES) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.RETURN_TO_GROCER_HERBIEL, ExShowScreenMessage.TOP_CENTER, 10000);
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