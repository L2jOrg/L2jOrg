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
package quests.Q00211_TrialOfTheChallenger;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.RadarControl;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Trial of the Challenger (211)
 * @author Pandragon
 */
public final class Q00211_TrialOfTheChallenger extends Quest
{
	// NPCs
	private static final int FILAUR = 30535;
	private static final int KASH = 30644;
	private static final int MARTIAN = 30645;
	private static final int RALDO = 30646;
	private static final int CHEST_OF_SHYSLASSYS = 30647;
	// Monsters
	private static final int SHYSLASSYS = 27110;
	private static final int CAVEBASILISK = 27111;
	private static final int GORR = 27112;
	private static final int BARAHAM = 27113;
	private static final int QUEEN_OF_SUCCUBUS = 27114;
	// Items
	private static final int LETTER_OF_KASH = 2628;
	private static final int WATCHERS_EYE1 = 2629;
	private static final int WATCHERS_EYE2 = 2630;
	private static final int SCROLL_OF_SHYSLASSYS = 2631;
	private static final int BROKEN_KEY = 2632;
	
	// Rewards
	private static final int ELVEN_NECKLACE_BEADS = 1904;
	private static final int WHITE_TUNIC_PATTERN = 1936;
	private static final int IRON_BOOTS_DESIGN = 1940;
	private static final int MANTICOR_SKIN_GAITERS_PATTERN = 1943;
	private static final int GAUNTLET_OF_REPOSE_PATTERN = 1946;
	private static final int MITHRIL_SCALE_GAITERS_MATERIAL = 2918;
	private static final int BRIGAMDINE_GAUNTLET_PATTERN = 2927;
	private static final int TOME_OF_BLOOD_PAGE = 2030;
	private static final int MARK_OF_CHALLENGER = 2627;
	// Misc
	private static final int MIN_LVL = 35;
	
	public Q00211_TrialOfTheChallenger()
	{
		super(211);
		addStartNpc(KASH);
		addTalkId(FILAUR, KASH, MARTIAN, RALDO, CHEST_OF_SHYSLASSYS);
		addKillId(SHYSLASSYS, CAVEBASILISK, GORR, BARAHAM, QUEEN_OF_SUCCUBUS);
		registerQuestItems(LETTER_OF_KASH, WATCHERS_EYE1, WATCHERS_EYE2, SCROLL_OF_SHYSLASSYS, BROKEN_KEY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "30644-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30645-07.html":
			case "30645-08.html":
			case "30646-02.html":
			case "30646-03.html":
			{
				if (qs.isStarted())
				{
					htmltext = event;
				}
				break;
			}
			case "30644-06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30647-02.html":
			{
				if (qs.isCond(2) && hasQuestItems(player, BROKEN_KEY))
				{
					takeItems(player, BROKEN_KEY, -1);
					if (getRandom(10) < 2)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_JACKPOT);
						final int random = getRandom(100);
						if (random > 90)
						{
							rewardItems(player, MITHRIL_SCALE_GAITERS_MATERIAL, 1);
							rewardItems(player, BRIGAMDINE_GAUNTLET_PATTERN, 1);
							rewardItems(player, MANTICOR_SKIN_GAITERS_PATTERN, 1);
							rewardItems(player, GAUNTLET_OF_REPOSE_PATTERN, 1);
							rewardItems(player, IRON_BOOTS_DESIGN, 1);
						}
						else if (random > 70)
						{
							rewardItems(player, TOME_OF_BLOOD_PAGE, 1);
							rewardItems(player, ELVEN_NECKLACE_BEADS, 1);
						}
						else if (random > 40)
						{
							rewardItems(player, WHITE_TUNIC_PATTERN, 1);
						}
						else
						{
							rewardItems(player, IRON_BOOTS_DESIGN, 1);
						}
						htmltext = "30647-03.html";
					}
					else
					{
						giveAdena(player, getRandom(1000) + 1, true);
						htmltext = event;
					}
				}
				else
				{
					htmltext = "30647-04.html";
				}
				break;
			}
			case "30645-02.html":
			{
				if (qs.isCond(3) && hasQuestItems(player, LETTER_OF_KASH))
				{
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30646-04.html":
			case "30646-05.html":
			{
				if (qs.isCond(7) && hasQuestItems(player, WATCHERS_EYE2))
				{
					takeItems(player, WATCHERS_EYE2, -1);
					qs.setCond(8, true);
					htmltext = event;
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
		switch (npc.getId())
		{
			case KASH:
			{
				if (qs.isCreated())
				{
					if (!talker.isInCategory(CategoryType.WARRIOR_GROUP))
					{
						htmltext = "30644-02.html";
					}
					else if (talker.getLevel() < MIN_LVL)
					{
						htmltext = "30644-01.html";
					}
					else
					{
						htmltext = "30644-03.htm";
					}
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30644-07.html";
							break;
						}
						case 2:
						{
							if (hasQuestItems(talker, SCROLL_OF_SHYSLASSYS))
							{
								takeItems(talker, SCROLL_OF_SHYSLASSYS, -1);
								giveItems(talker, LETTER_OF_KASH, 1);
								qs.setCond(3, true);
								htmltext = "30644-08.html";
							}
							break;
						}
						case 3:
						{
							if (hasQuestItems(talker, LETTER_OF_KASH))
							{
								htmltext = "30644-09.html";
							}
							break;
						}
						case 8:
						case 9:
						case 10:
						{
							htmltext = "30644-10.html";
							break;
						}
					}
				}
				else if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
			case MARTIAN:
			{
				switch (qs.getCond())
				{
					case 3:
					{
						if (hasQuestItems(talker, LETTER_OF_KASH))
						{
							htmltext = "30645-01.html";
						}
						break;
					}
					case 4:
					{
						htmltext = "30645-03.html";
						break;
					}
					case 5:
					{
						if (hasQuestItems(talker, WATCHERS_EYE1))
						{
							takeItems(talker, WATCHERS_EYE1, -1);
							qs.setCond(6, true);
							htmltext = "30645-04.html";
						}
						break;
					}
					case 6:
					{
						htmltext = "30645-05.html";
						break;
					}
					case 7:
					{
						htmltext = "30645-06.html";
						break;
					}
					case 8:
					case 9:
					{
						htmltext = "30645-09.html";
						break;
					}
				}
				break;
			}
			case CHEST_OF_SHYSLASSYS:
			{
				if (qs.isStarted())
				{
					htmltext = "30647-01.html";
				}
				break;
			}
			case RALDO:
			{
				switch (qs.getCond())
				{
					case 7:
					{
						if (hasQuestItems(talker, WATCHERS_EYE2))
						{
							htmltext = "30646-01.html";
						}
						break;
					}
					case 8:
					{
						htmltext = "30646-06.html";
						break;
					}
					case 10:
					{
						addExpAndSp(talker, 1067606, 69242);
						giveAdena(talker, 194556, true);
						giveItems(talker, MARK_OF_CHALLENGER, 1);
						talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
						qs.exitQuest(false, true);
						htmltext = "30646-07.html";
						break;
					}
				}
				break;
			}
			case FILAUR:
			{
				switch (qs.getCond())
				{
					case 8:
					{
						htmltext = "30535-01.html";
						qs.setCond(9, true);
						break;
					}
					case 9:
					{
						talker.sendPacket(new RadarControl(0, 2, 151589, -174823, -1776));
						htmltext = "30535-02.html";
						break;
					}
					case 10:
					{
						htmltext = "30535-03.html";
						break;
					}
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
		if ((qs == null) || !Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		switch (npc.getId())
		{
			case SHYSLASSYS:
			{
				if (qs.isCond(1))
				{
					if (SpawnTable.getInstance().getSpawns(npc.getId()).size() < 10)
					{
						addSpawn(CHEST_OF_SHYSLASSYS, npc, false, 200000);
					}
					giveItems(killer, SCROLL_OF_SHYSLASSYS, 1);
					giveItems(killer, BROKEN_KEY, 1);
					qs.setCond(2, true);
				}
				break;
			}
			case GORR:
			{
				if (qs.isCond(4))
				{
					giveItems(killer, WATCHERS_EYE1, 1);
					qs.setCond(5, true);
				}
				break;
			}
			case BARAHAM:
			{
				if (qs.isCond(6))
				{
					if (SpawnTable.getInstance().getSpawns(npc.getId()).size() < 10)
					{
						addSpawn(RALDO, npc, false, 100000);
					}
					giveItems(killer, WATCHERS_EYE2, 1);
					qs.setCond(7, true);
				}
				break;
			}
			case QUEEN_OF_SUCCUBUS:
			{
				if (qs.isCond(9))
				{
					if (SpawnTable.getInstance().getSpawns(npc.getId()).size() < 10)
					{
						addSpawn(RALDO, npc, false, 100000);
					}
					qs.setCond(10, true);
				}
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
