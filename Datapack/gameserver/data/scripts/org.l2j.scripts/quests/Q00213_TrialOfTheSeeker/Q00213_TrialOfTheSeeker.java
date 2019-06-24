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
package quests.Q00213_TrialOfTheSeeker;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Trial Of The Seeker (213)
 * @author ivantotov
 */
public final class Q00213_TrialOfTheSeeker extends Quest
{
	// NPCs
	private static final int MASTER_TERRY = 30064;
	private static final int MASTER_DUFNER = 30106;
	private static final int BLACKSMITH_BRUNON = 30526;
	private static final int TRADER_VIKTOR = 30684;
	private static final int MAGISTER_MARINA = 30715;
	// Items
	private static final int DUFNERS_LETTER = 2647;
	private static final int TERRYS_1ST_ORDER = 2648;
	private static final int TERRYS_2ND_ORDER = 2649;
	private static final int TERRYS_LETTER = 2650;
	private static final int VIKTORS_LETTER = 2651;
	private static final int HAWKEYES_LETTER = 2652;
	private static final int MYSTERIOUS_SPIRIT_ORE = 2653;
	private static final int OL_MAHUM_SPIRIT_ORE = 2654;
	private static final int TUREK_SPIRIT_ORE = 2655;
	private static final int ANT_SPIRIT_ORE = 2656;
	private static final int TURAK_BUGBEAR_SPIRIT_ORE = 2657;
	private static final int TERRY_BOX = 2658;
	private static final int VIKTORS_REQUEST = 2659;
	private static final int MEDUSA_SCALES = 2660;
	private static final int SHILENS_SPIRIT_ORE = 2661;
	private static final int ANALYSIS_REQUEST = 2662;
	private static final int MARINAS_LETTER = 2663;
	private static final int EXPERIMENT_TOOLS = 2664;
	private static final int ANALYSIS_RESULT = 2665;
	private static final int TERRYS_3RD_ORDER = 2666;
	private static final int LIST_OF_HOST = 2667;
	private static final int ABYSS_SPIRIT_ORE1 = 2668;
	private static final int ABYSS_SPIRIT_ORE2 = 2669;
	private static final int ABYSS_SPIRIT_ORE3 = 2670;
	private static final int ABYSS_SPIRIT_ORE4 = 2671;
	private static final int TERRYS_REPORT = 2672;
	// Reward
	private static final int MARK_OF_SEEKER = 2673;
	// Monsters
	private static final int ANT_CAPTAIN = 20080;
	private static final int ANT_WARRIOR_CAPTAIN = 20088;
	private static final int MEDUSA = 20158;
	private static final int NEER_GHOUL_BERSERKER = 20198;
	private static final int OL_MAHUM_CAPTAIN = 20211;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int TURAK_BUGBEAR_WARRIOR = 20249;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int TUREK_ORC_WARLORD = 20495;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	// Misc
	private static final int MIN_LVL = 35;
	private static final int LEVEL = 36;
	
	public Q00213_TrialOfTheSeeker()
	{
		super(213);
		addStartNpc(MASTER_DUFNER);
		addTalkId(MASTER_DUFNER, MASTER_TERRY, BLACKSMITH_BRUNON, TRADER_VIKTOR, MAGISTER_MARINA);
		addKillId(ANT_CAPTAIN, ANT_WARRIOR_CAPTAIN, MEDUSA, NEER_GHOUL_BERSERKER, OL_MAHUM_CAPTAIN, MARSH_STAKATO_DRONE, TURAK_BUGBEAR_WARRIOR, BREKA_ORC_OVERLORD, TUREK_ORC_WARLORD, LETO_LIZARDMAN_WARRIOR);
		registerQuestItems(DUFNERS_LETTER, TERRYS_1ST_ORDER, TERRYS_2ND_ORDER, TERRYS_LETTER, VIKTORS_LETTER, HAWKEYES_LETTER, MYSTERIOUS_SPIRIT_ORE, OL_MAHUM_SPIRIT_ORE, TUREK_SPIRIT_ORE, ANT_SPIRIT_ORE, TURAK_BUGBEAR_SPIRIT_ORE, TERRY_BOX, VIKTORS_REQUEST, MEDUSA_SCALES, SHILENS_SPIRIT_ORE, ANALYSIS_REQUEST, MARINAS_LETTER, EXPERIMENT_TOOLS, ANALYSIS_RESULT, TERRYS_3RD_ORDER, LIST_OF_HOST, ABYSS_SPIRIT_ORE1, ABYSS_SPIRIT_ORE2, ABYSS_SPIRIT_ORE3, ABYSS_SPIRIT_ORE4, TERRYS_REPORT);
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
			case "ACCEPT":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					if (!hasQuestItems(player, DUFNERS_LETTER))
					{
						giveItems(player, DUFNERS_LETTER, 1);
					}
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30106-04.htm":
			case "30064-02.html":
			case "30064-07.html":
			case "30064-16.html":
			case "30064-17.html":
			case "30064-19.html":
			case "30684-02.html":
			case "30684-03.html":
			case "30684-04.html":
			case "30684-06.html":
			case "30684-07.html":
			case "30684-08.html":
			case "30684-09.html":
			case "30684-10.html":
			{
				htmltext = event;
				break;
			}
			case "30064-03.html":
			{
				if (hasQuestItems(player, DUFNERS_LETTER))
				{
					takeItems(player, DUFNERS_LETTER, 1);
					giveItems(player, TERRYS_1ST_ORDER, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30064-06.html":
			{
				if (hasQuestItems(player, TERRYS_1ST_ORDER))
				{
					takeItems(player, TERRYS_1ST_ORDER, 1);
					giveItems(player, TERRYS_2ND_ORDER, 1);
					takeItems(player, MYSTERIOUS_SPIRIT_ORE, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30064-10.html":
			{
				giveItems(player, TERRYS_LETTER, 1);
				takeItems(player, OL_MAHUM_SPIRIT_ORE, 1);
				takeItems(player, TUREK_SPIRIT_ORE, 1);
				takeItems(player, ANT_SPIRIT_ORE, 1);
				takeItems(player, TURAK_BUGBEAR_SPIRIT_ORE, 1);
				takeItems(player, TERRYS_2ND_ORDER, 1);
				giveItems(player, TERRY_BOX, 1);
				qs.setCond(6, true);
				htmltext = event;
				break;
			}
			case "30064-18.html":
			{
				if (hasQuestItems(player, ANALYSIS_RESULT))
				{
					takeItems(player, ANALYSIS_RESULT, 1);
					giveItems(player, LIST_OF_HOST, 1);
					qs.setCond(15, true);
					htmltext = event;
				}
				break;
			}
			case "30684-05.html":
			{
				if (hasQuestItems(player, TERRYS_LETTER))
				{
					takeItems(player, TERRYS_LETTER, 1);
					giveItems(player, VIKTORS_LETTER, 1);
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "30684-11.html":
			{
				takeItems(player, TERRYS_LETTER, 1);
				takeItems(player, TERRY_BOX, 1);
				takeItems(player, HAWKEYES_LETTER, 1);
				takeItems(player, VIKTORS_LETTER, 1);
				giveItems(player, VIKTORS_REQUEST, 1);
				qs.setCond(9, true);
				htmltext = event;
				break;
			}
			case "30684-15.html":
			{
				takeItems(player, VIKTORS_REQUEST, 1);
				takeItems(player, MEDUSA_SCALES, -1);
				giveItems(player, SHILENS_SPIRIT_ORE, 1);
				giveItems(player, ANALYSIS_REQUEST, 1);
				qs.setCond(11, true);
				htmltext = event;
				break;
			}
			case "30715-02.html":
			{
				takeItems(player, SHILENS_SPIRIT_ORE, 1);
				takeItems(player, ANALYSIS_REQUEST, 1);
				giveItems(player, MARINAS_LETTER, 1);
				qs.setCond(12, true);
				htmltext = event;
				break;
			}
			case "30715-05.html":
			{
				takeItems(player, EXPERIMENT_TOOLS, 1);
				giveItems(player, ANALYSIS_RESULT, 1);
				qs.setCond(14, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case ANT_CAPTAIN:
				{
					if (hasQuestItems(killer, TERRYS_2ND_ORDER) && !hasQuestItems(killer, ANT_SPIRIT_ORE))
					{
						giveItems(killer, ANT_SPIRIT_ORE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, OL_MAHUM_SPIRIT_ORE, TUREK_SPIRIT_ORE, TURAK_BUGBEAR_SPIRIT_ORE))
						{
							qs.setCond(5);
						}
					}
					break;
				}
				case ANT_WARRIOR_CAPTAIN:
				{
					if (hasQuestItems(killer, LIST_OF_HOST) && !hasQuestItems(killer, ABYSS_SPIRIT_ORE3))
					{
						giveItems(killer, ABYSS_SPIRIT_ORE3, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, ABYSS_SPIRIT_ORE1, ABYSS_SPIRIT_ORE2, ABYSS_SPIRIT_ORE4))
						{
							qs.setCond(16);
						}
					}
					break;
				}
				case MEDUSA:
				{
					if (hasQuestItems(killer, VIKTORS_REQUEST) && (getQuestItemsCount(killer, MEDUSA_SCALES) < 10))
					{
						giveItems(killer, MEDUSA_SCALES, 1);
						if (getQuestItemsCount(killer, MEDUSA_SCALES) == 10)
						{
							qs.setCond(10, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case NEER_GHOUL_BERSERKER:
				{
					if (hasQuestItems(killer, TERRYS_1ST_ORDER) && !hasQuestItems(killer, MYSTERIOUS_SPIRIT_ORE))
					{
						if (getRandom(100) < 50)
						{
							giveItems(killer, MYSTERIOUS_SPIRIT_ORE, 1);
							qs.setCond(3, true);
						}
					}
					break;
				}
				case OL_MAHUM_CAPTAIN:
				{
					if (hasQuestItems(killer, TERRYS_2ND_ORDER) && !hasQuestItems(killer, OL_MAHUM_SPIRIT_ORE))
					{
						giveItems(killer, OL_MAHUM_SPIRIT_ORE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, TUREK_SPIRIT_ORE, ANT_SPIRIT_ORE, TURAK_BUGBEAR_SPIRIT_ORE))
						{
							qs.setCond(5);
						}
					}
					break;
				}
				case MARSH_STAKATO_DRONE:
				{
					if (hasQuestItems(killer, LIST_OF_HOST) && !hasQuestItems(killer, ABYSS_SPIRIT_ORE1))
					{
						giveItems(killer, ABYSS_SPIRIT_ORE1, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, ABYSS_SPIRIT_ORE2, ABYSS_SPIRIT_ORE3, ABYSS_SPIRIT_ORE4))
						{
							qs.setCond(16);
						}
					}
					break;
				}
				case TURAK_BUGBEAR_WARRIOR:
				{
					if (hasQuestItems(killer, TERRYS_2ND_ORDER) && !hasQuestItems(killer, TURAK_BUGBEAR_SPIRIT_ORE))
					{
						giveItems(killer, TURAK_BUGBEAR_SPIRIT_ORE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, OL_MAHUM_SPIRIT_ORE, TUREK_SPIRIT_ORE, ANT_SPIRIT_ORE))
						{
							qs.setCond(5);
						}
					}
					break;
				}
				case BREKA_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, LIST_OF_HOST) && !hasQuestItems(killer, ABYSS_SPIRIT_ORE2))
					{
						giveItems(killer, ABYSS_SPIRIT_ORE2, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, ABYSS_SPIRIT_ORE1, ABYSS_SPIRIT_ORE3, ABYSS_SPIRIT_ORE4))
						{
							qs.setCond(16);
						}
					}
					break;
				}
				case TUREK_ORC_WARLORD:
				{
					if (hasQuestItems(killer, TERRYS_2ND_ORDER) && !hasQuestItems(killer, TUREK_SPIRIT_ORE))
					{
						giveItems(killer, TUREK_SPIRIT_ORE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, OL_MAHUM_SPIRIT_ORE, ANT_SPIRIT_ORE, TURAK_BUGBEAR_SPIRIT_ORE))
						{
							qs.setCond(5);
						}
					}
					break;
				}
				case LETO_LIZARDMAN_WARRIOR:
				{
					if (hasQuestItems(killer, LIST_OF_HOST) && !hasQuestItems(killer, ABYSS_SPIRIT_ORE4))
					{
						giveItems(killer, ABYSS_SPIRIT_ORE4, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, ABYSS_SPIRIT_ORE1, ABYSS_SPIRIT_ORE2, ABYSS_SPIRIT_ORE3))
						{
							qs.setCond(16);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == MASTER_DUFNER)
			{
				if ((player.getClassId() == ClassId.ROGUE) || (player.getClassId() == ClassId.ELVEN_SCOUT) || ((player.getClassId() == ClassId.ASSASSIN)))
				{
					if (player.getLevel() < MIN_LVL)
					{
						htmltext = "30106-02.html";
					}
					else
					{
						htmltext = "30106-03.htm";
					}
				}
				else
				{
					htmltext = "30106-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_DUFNER:
				{
					if (hasQuestItems(player, DUFNERS_LETTER) && !hasQuestItems(player, TERRYS_REPORT))
					{
						htmltext = "30106-06.html";
					}
					else if (!hasAtLeastOneQuestItem(player, DUFNERS_LETTER, TERRYS_REPORT))
					{
						htmltext = "30106-07.html";
					}
					else if (hasQuestItems(player, TERRYS_REPORT) && !hasQuestItems(player, DUFNERS_LETTER))
					{
						giveAdena(player, 187606, true);
						giveItems(player, MARK_OF_SEEKER, 1);
						addExpAndSp(player, 1029478, 66768);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30106-08.html";
					}
					break;
				}
				case MASTER_TERRY:
				{
					if (hasQuestItems(player, DUFNERS_LETTER))
					{
						htmltext = "30064-01.html";
					}
					else if (hasQuestItems(player, TERRYS_1ST_ORDER))
					{
						if (!hasQuestItems(player, MYSTERIOUS_SPIRIT_ORE))
						{
							htmltext = "30064-04.html";
						}
						else
						{
							htmltext = "30064-05.html";
						}
					}
					else if (hasQuestItems(player, TERRYS_2ND_ORDER))
					{
						if ((getQuestItemsCount(player, OL_MAHUM_SPIRIT_ORE) + getQuestItemsCount(player, TUREK_SPIRIT_ORE) + getQuestItemsCount(player, ANT_SPIRIT_ORE) + getQuestItemsCount(player, TURAK_BUGBEAR_SPIRIT_ORE)) < 4)
						{
							htmltext = "30064-08.html";
						}
						else
						{
							htmltext = "30064-09.html";
						}
					}
					else if (hasQuestItems(player, TERRYS_LETTER))
					{
						htmltext = "30064-11.html";
					}
					else if (hasQuestItems(player, VIKTORS_LETTER))
					{
						takeItems(player, VIKTORS_LETTER, 1);
						giveItems(player, HAWKEYES_LETTER, 1);
						qs.setCond(8, true);
						htmltext = "30064-12.html";
					}
					else if (hasQuestItems(player, HAWKEYES_LETTER))
					{
						htmltext = "30064-13.html";
					}
					else if (hasAtLeastOneQuestItem(player, VIKTORS_REQUEST, ANALYSIS_REQUEST, MARINAS_LETTER, EXPERIMENT_TOOLS))
					{
						htmltext = "30064-14.html";
					}
					else if (hasQuestItems(player, ANALYSIS_RESULT))
					{
						htmltext = "30064-15.html";
					}
					else if (hasQuestItems(player, TERRYS_3RD_ORDER))
					{
						if (player.getLevel() < LEVEL)
						{
							htmltext = "30064-20.html";
						}
						else
						{
							takeItems(player, TERRYS_3RD_ORDER, 1);
							giveItems(player, LIST_OF_HOST, 1);
							qs.setCond(15, true);
							htmltext = "30064-21.html";
						}
					}
					else if (hasQuestItems(player, LIST_OF_HOST))
					{
						if ((getQuestItemsCount(player, ABYSS_SPIRIT_ORE1) + getQuestItemsCount(player, ABYSS_SPIRIT_ORE2) + getQuestItemsCount(player, ABYSS_SPIRIT_ORE3) + getQuestItemsCount(player, ABYSS_SPIRIT_ORE4)) < 4)
						{
							htmltext = "30064-22.html";
						}
						else
						{
							takeItems(player, LIST_OF_HOST, 1);
							takeItems(player, ABYSS_SPIRIT_ORE1, 1);
							takeItems(player, ABYSS_SPIRIT_ORE2, 1);
							takeItems(player, ABYSS_SPIRIT_ORE3, 1);
							takeItems(player, ABYSS_SPIRIT_ORE4, 1);
							giveItems(player, TERRYS_REPORT, 1);
							qs.setCond(17, true);
							htmltext = "30064-23.html";
						}
					}
					else if (hasQuestItems(player, TERRYS_REPORT))
					{
						htmltext = "30064-24.html";
					}
					break;
				}
				case BLACKSMITH_BRUNON:
				{
					if (hasQuestItems(player, MARINAS_LETTER))
					{
						takeItems(player, MARINAS_LETTER, 1);
						giveItems(player, EXPERIMENT_TOOLS, 1);
						qs.setCond(13, true);
						htmltext = "30526-01.html";
					}
					else if (hasQuestItems(player, EXPERIMENT_TOOLS))
					{
						htmltext = "30526-02.html";
					}
					break;
				}
				case TRADER_VIKTOR:
				{
					if (hasQuestItems(player, TERRYS_LETTER))
					{
						htmltext = "30684-01.html";
					}
					else if (hasQuestItems(player, HAWKEYES_LETTER))
					{
						htmltext = "30684-12.html";
					}
					else if (hasQuestItems(player, VIKTORS_REQUEST))
					{
						if (getQuestItemsCount(player, MEDUSA_SCALES) < 10)
						{
							htmltext = "30684-13.html";
						}
						else
						{
							htmltext = "30684-14.html";
						}
					}
					else if (hasQuestItems(player, SHILENS_SPIRIT_ORE, ANALYSIS_REQUEST))
					{
						htmltext = "30684-16.html";
					}
					else if (hasQuestItems(player, MARINAS_LETTER, EXPERIMENT_TOOLS, ANALYSIS_REQUEST, TERRYS_REPORT))
					{
						htmltext = "30684-17.html";
					}
					else if (hasQuestItems(player, VIKTORS_LETTER))
					{
						htmltext = "30684-05.html";
					}
					break;
				}
				case MAGISTER_MARINA:
				{
					if (hasQuestItems(player, SHILENS_SPIRIT_ORE, ANALYSIS_REQUEST))
					{
						htmltext = "30715-01.html";
					}
					else if (hasQuestItems(player, MARINAS_LETTER))
					{
						htmltext = "30715-03.html";
					}
					else if (hasQuestItems(player, EXPERIMENT_TOOLS))
					{
						htmltext = "30715-04.html";
					}
					else if (hasQuestItems(player, ANALYSIS_RESULT))
					{
						htmltext = "30715-06.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_DUFNER)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}