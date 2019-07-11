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
package quests.Q11004_PerfectLeatherArmor2;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q11003_PerfectLeatherArmor1.Q11003_PerfectLeatherArmor1;

/**
 * Perfect Leather Armor (2/3) (11004)
 * @author Stayway
 */
public class Q11004_PerfectLeatherArmor2 extends Quest
{
	// NPCs
	private static final int LECTOR = 30001;
	// Items
	private static final int COBWEB = 90212;
	private static final int ESSENCE_OF_WATER = 90213;
	private static final int LECTORS_NOTES = 90211;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 10650;
	private static final int HEALING_POTION = 1073;
	private static final int MP_RECOVERY_POTION = 90310;
	private static final int SOULSHOTS_NO_GRADE = 5789;
	private static final int SPIRITSHOT_NO_GRADE = 5790;
	// Monsters
	private static final int GIANT_SPIDER = 20103;
	private static final int GIANT_FANG_SPIDER = 20106;
	private static final int GIANT_BLADE_SPIDER = 20108;
	private static final int UNDINE = 20110;
	private static final int UNDINE_ELDER = 20113;
	private static final int UNDINE_NOBLE = 20115;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LVL = 20;
	
	public Q11004_PerfectLeatherArmor2()
	{
		super(11004);
		addStartNpc(LECTOR);
		addTalkId(LECTOR);
		addKillId(GIANT_SPIDER, GIANT_FANG_SPIDER, GIANT_BLADE_SPIDER, UNDINE, UNDINE_ELDER, UNDINE_NOBLE);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.HUMAN, "no-race.html"); // Custom
		addCondCompletedQuest(Q11003_PerfectLeatherArmor1.class.getSimpleName(), "30001-06.html");
		registerQuestItems(LECTORS_NOTES, COBWEB, ESSENCE_OF_WATER);
		setQuestNameNpcStringId(NpcStringId.LV_15_PERFECT_LEATHER_ARMOR_2_3);
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
			case "30001-02.htm":
			{
				qs.startQuest();
				qs.setCond(2);
				showOnScreenMsg(player, NpcStringId.LECTOR_WANTS_YOU_TO_BRING_HIM_MATERIALS_FOR_NEW_ARMOR_N_GO_HUNTING_AND_KILL_GIANT_SPIDERS, ExShowScreenMessage.TOP_CENTER, 10000);
				giveItems(player, LECTORS_NOTES, 1);
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(4))
				{
					takeItems(player, LECTORS_NOTES, 1);
					takeItems(player, COBWEB, 25);
					takeItems(player, ESSENCE_OF_WATER, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SOULSHOTS_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30001-04.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(4))
				{
					takeItems(player, LECTORS_NOTES, 1);
					takeItems(player, COBWEB, 25);
					takeItems(player, ESSENCE_OF_WATER, 20);
					giveItems(player, SCROLL_OF_ESCAPE, 5);
					giveItems(player, HEALING_POTION, 40);
					giveItems(player, MP_RECOVERY_POTION, 40);
					giveItems(player, SPIRITSHOT_NO_GRADE, 1000);
					addExpAndSp(player, 70000, 3600);
					qs.exitQuest(false, true);
					htmltext = "30001-05.html";
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
				htmltext = "30001-01.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(2))
				{
					htmltext = "30001-02a.html";
				}
				else if (qs.isCond(4))
				{
					htmltext = "30001-03.html";
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
				case GIANT_SPIDER:
				case GIANT_BLADE_SPIDER:
				case GIANT_FANG_SPIDER:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, COBWEB) < 25))
					{
						if (getRandom(100) < 87)
						{
							giveItems(killer, COBWEB, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, COBWEB) >= 25)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GIANT_SPIDERS_N_GO_HUNTING_AND_KILL_UNDINES, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case UNDINE:
				case UNDINE_ELDER:
				case UNDINE_NOBLE:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, ESSENCE_OF_WATER) < 20))
					{
						if (getRandom(100) < 100)
						{
							giveItems(killer, ESSENCE_OF_WATER, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ESSENCE_OF_WATER) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_ALL_OF_THE_ITEMS_LECTOR_REQUESTED_RETURN_TO_HIM, ExShowScreenMessage.TOP_CENTER, 10000);
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