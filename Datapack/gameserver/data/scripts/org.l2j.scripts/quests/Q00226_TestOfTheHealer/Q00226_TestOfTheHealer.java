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
package quests.Q00226_TestOfTheHealer;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Test Of The Healer(226)
 * @author ivantotov
 */
public final class Q00226_TestOfTheHealer extends Quest
{
	// NPCs
	private static final int MASTER_SORIUS = 30327;
	private static final int ALLANA = 30424;
	private static final int PERRIN = 30428;
	private static final int PRIEST_BANDELLOS = 30473;
	private static final int FATHER_GUPU = 30658;
	private static final int ORPHAN_GIRL = 30659;
	private static final int WINDY_SHAORING = 30660;
	private static final int MYSTERIOUS_DARK_ELF = 30661;
	private static final int PIPER_LONGBOW = 30662;
	private static final int SLEIN_SHINING_BLADE = 30663;
	private static final int CAIN_FLYING_KNIFE = 30664;
	private static final int SAINT_KRISTINA = 30665;
	private static final int DAURIN_HAMMERCRUSH = 30674;
	// Items
	private static final int ADENA = 57;
	private static final int REPORT_OF_PERRIN = 2810;
	private static final int CRISTINAS_LETTER = 2811;
	private static final int PICTURE_OF_WINDY = 2812;
	private static final int GOLDEN_STATUE = 2813;
	private static final int WINDYS_PEBBLES = 2814;
	private static final int ORDER_OF_SORIUS = 2815;
	private static final int SECRET_LETTER1 = 2816;
	private static final int SECRET_LETTER2 = 2817;
	private static final int SECRET_LETTER3 = 2818;
	private static final int SECRET_LETTER4 = 2819;
	// Reward
	private static final int MARK_OF_HEALER = 2820;
	// Quest Monster
	private static final int LERO_LIZARDMAN_AGENT = 27122;
	private static final int LERO_LIZARDMAN_LEADER = 27123;
	private static final int LERO_LIZARDMAN_ASSASSIN = 27124;
	private static final int LERO_LIZARDMAN_SNIPER = 27125;
	private static final int LERO_LIZARDMAN_WIZARD = 27126;
	private static final int LERO_LIZARDMAN_LORD = 27127;
	private static final int TATOMA = 27134;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00226_TestOfTheHealer()
	{
		super(226);
		addStartNpc(PRIEST_BANDELLOS);
		addTalkId(PRIEST_BANDELLOS, MASTER_SORIUS, ALLANA, PERRIN, FATHER_GUPU, ORPHAN_GIRL, WINDY_SHAORING, MYSTERIOUS_DARK_ELF, PIPER_LONGBOW, SLEIN_SHINING_BLADE, CAIN_FLYING_KNIFE, SAINT_KRISTINA, DAURIN_HAMMERCRUSH);
		addKillId(LERO_LIZARDMAN_AGENT, LERO_LIZARDMAN_LEADER, LERO_LIZARDMAN_ASSASSIN, LERO_LIZARDMAN_SNIPER, LERO_LIZARDMAN_WIZARD, LERO_LIZARDMAN_LORD, TATOMA);
		registerQuestItems(REPORT_OF_PERRIN, CRISTINAS_LETTER, PICTURE_OF_WINDY, GOLDEN_STATUE, WINDYS_PEBBLES, ORDER_OF_SORIUS, SECRET_LETTER1, SECRET_LETTER2, SECRET_LETTER3, SECRET_LETTER4);
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
					qs.setMemoState(1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, REPORT_OF_PERRIN, 1);
				}
				break;
			}
			case "30473-08.html":
			{
				if (qs.isMemoState(10) && hasQuestItems(player, GOLDEN_STATUE))
				{
					htmltext = event;
				}
				break;
			}
			case "30473-09.html":
			{
				if (qs.isMemoState(10) && hasQuestItems(player, GOLDEN_STATUE))
				{
					giveAdena(player, 233490, true);
					giveItems(player, MARK_OF_HEALER, 1);
					addExpAndSp(player, 738283, 50662);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = event;
				}
				break;
			}
			case "30428-02.html":
			{
				if (qs.isMemoState(1) && hasQuestItems(player, REPORT_OF_PERRIN))
				{
					qs.setCond(2, true);
					if (npc.getSummonedNpcCount() < 1)
					{
						addAttackPlayerDesire(addSpawn(npc, TATOMA, npc, true, 200000), player);
					}
				}
				htmltext = event;
				break;
			}
			case "30658-02.html":
			{
				if (qs.isMemoState(4) && !hasAtLeastOneQuestItem(player, PICTURE_OF_WINDY, WINDYS_PEBBLES, GOLDEN_STATUE))
				{
					if (getQuestItemsCount(player, ADENA) >= 100000)
					{
						takeItems(player, ADENA, 100000);
						giveItems(player, PICTURE_OF_WINDY, 1);
						qs.setCond(7, true);
						htmltext = event;
					}
					else
					{
						htmltext = "30658-05.html";
					}
				}
				break;
			}
			case "30658-03.html":
			{
				if (qs.isMemoState(4) && !hasAtLeastOneQuestItem(player, PICTURE_OF_WINDY, WINDYS_PEBBLES, GOLDEN_STATUE))
				{
					qs.setMemoState(5);
					htmltext = event;
				}
				break;
			}
			case "30658-07.html":
			{
				htmltext = event;
				break;
			}
			case "30660-02.html":
			{
				if (hasQuestItems(player, PICTURE_OF_WINDY))
				{
					htmltext = event;
				}
				break;
			}
			case "30660-03.html":
			{
				if (hasQuestItems(player, PICTURE_OF_WINDY))
				{
					takeItems(player, PICTURE_OF_WINDY, 1);
					giveItems(player, WINDYS_PEBBLES, 1);
					qs.setCond(8, true);
					npc.deleteMe();
					htmltext = event;
				}
				break;
			}
			case "30665-02.html":
			{
				if ((getQuestItemsCount(player, SECRET_LETTER1) + getQuestItemsCount(player, SECRET_LETTER2) + getQuestItemsCount(player, SECRET_LETTER3) + getQuestItemsCount(player, SECRET_LETTER4)) == 4)
				{
					giveItems(player, CRISTINAS_LETTER, 1);
					takeItems(player, SECRET_LETTER1, 1);
					takeItems(player, SECRET_LETTER2, 1);
					takeItems(player, SECRET_LETTER3, 1);
					takeItems(player, SECRET_LETTER4, 1);
					qs.setMemoState(9);
					qs.setCond(22, true);
					htmltext = event;
				}
				break;
			}
			case "30674-02.html":
			{
				if (qs.isMemoState(6))
				{
					qs.setCond(11);
					takeItems(player, ORDER_OF_SORIUS, 1);
					addSpawn(npc, LERO_LIZARDMAN_AGENT, npc, true, 200000);
					addSpawn(npc, LERO_LIZARDMAN_AGENT, npc, true, 200000);
					addSpawn(npc, LERO_LIZARDMAN_LEADER, npc, true, 200000);
					playSound(player, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
					htmltext = event;
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
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case LERO_LIZARDMAN_LEADER:
				{
					if (qs.isMemoState(6) && !hasQuestItems(killer, SECRET_LETTER1))
					{
						giveItems(killer, SECRET_LETTER1, 1);
						qs.setCond(12, true);
					}
					break;
				}
				case LERO_LIZARDMAN_ASSASSIN:
				{
					if (qs.isMemoState(8) && hasQuestItems(killer, SECRET_LETTER1) && !hasQuestItems(killer, SECRET_LETTER2))
					{
						giveItems(killer, SECRET_LETTER2, 1);
						qs.setCond(15, true);
					}
					break;
				}
				case LERO_LIZARDMAN_SNIPER:
				{
					if (qs.isMemoState(8) && hasQuestItems(killer, SECRET_LETTER1) && !hasQuestItems(killer, SECRET_LETTER3))
					{
						giveItems(killer, SECRET_LETTER3, 1);
						qs.setCond(17, true);
					}
					break;
				}
				case LERO_LIZARDMAN_LORD:
				{
					if (qs.isMemoState(8) && hasQuestItems(killer, SECRET_LETTER1) && !hasQuestItems(killer, SECRET_LETTER4))
					{
						giveItems(killer, SECRET_LETTER4, 1);
						qs.setCond(19, true);
					}
					break;
				}
				case TATOMA:
				{
					if (qs.isMemoState(1))
					{
						qs.setMemoState(2);
						qs.setCond(3, true);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == PRIEST_BANDELLOS)
			{
				if (player.isInCategory(CategoryType.WHITE_MAGIC_GROUP))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30473-03.htm";
					}
					else
					{
						htmltext = "30473-01.html";
					}
				}
				else
				{
					htmltext = "30473-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PRIEST_BANDELLOS:
				{
					if ((memoState >= 1) && (memoState < 10))
					{
						htmltext = "30473-05.html";
					}
					else if (memoState == 10)
					{
						if (hasQuestItems(player, GOLDEN_STATUE))
						{
							htmltext = "30473-07.html";
						}
						else
						{
							giveAdena(player, 266980, true);
							giveItems(player, MARK_OF_HEALER, 1);
							addExpAndSp(player, 1476566, 101324);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30473-06.html";
						}
					}
					break;
				}
				case MASTER_SORIUS:
				{
					if (memoState == 5)
					{
						giveItems(player, ORDER_OF_SORIUS, 1);
						qs.setMemoState(6);
						qs.setCond(10, true);
						htmltext = "30327-01.html";
					}
					else if ((memoState >= 6) && (memoState < 9))
					{
						htmltext = "30327-02.html";
					}
					else if (memoState == 9)
					{
						if (hasQuestItems(player, CRISTINAS_LETTER))
						{
							takeItems(player, CRISTINAS_LETTER, 1);
							qs.setMemoState(10);
							qs.setCond(23, true);
							htmltext = "30327-03.html";
						}
					}
					else if (memoState >= 10)
					{
						htmltext = "30327-04.html";
					}
					break;
				}
				case ALLANA:
				{
					if (memoState == 3)
					{
						qs.setMemoState(4);
						qs.setCond(5, true);
						htmltext = "30424-01.html";
					}
					else if (memoState == 4)
					{
						qs.setMemoState(4);
						htmltext = "30424-02.html";
					}
					break;
				}
				case PERRIN:
				{
					if (memoState == 1)
					{
						if (hasQuestItems(player, REPORT_OF_PERRIN))
						{
							htmltext = "30428-01.html";
						}
					}
					else if (memoState == 2)
					{
						takeItems(player, REPORT_OF_PERRIN, 1);
						qs.setMemoState(3);
						qs.setCond(4, true);
						htmltext = "30428-03.html";
					}
					else if (memoState == 3)
					{
						htmltext = "30428-04.html";
					}
					break;
				}
				case FATHER_GUPU:
				{
					if (memoState == 4)
					{
						if (!hasAtLeastOneQuestItem(player, PICTURE_OF_WINDY, WINDYS_PEBBLES, GOLDEN_STATUE))
						{
							qs.setCond(6, true);
							htmltext = "30658-01.html";
						}
						else if (hasQuestItems(player, PICTURE_OF_WINDY))
						{
							htmltext = "30658-04.html";
						}
						else if (hasQuestItems(player, WINDYS_PEBBLES))
						{
							giveItems(player, GOLDEN_STATUE, 1);
							takeItems(player, WINDYS_PEBBLES, 1);
							qs.setMemoState(5);
							htmltext = "30658-06.html";
						}
					}
					else if (memoState == 5)
					{
						qs.setCond(9, true);
						htmltext = "30658-07.html";
					}
					break;
				}
				case ORPHAN_GIRL:
				{
					switch (getRandom(5))
					{
						case 0:
						{
							htmltext = "30659-01.html";
							break;
						}
						case 1:
						{
							htmltext = "30659-02.html";
							break;
						}
						case 2:
						{
							htmltext = "30659-03.html";
							break;
						}
						case 3:
						{
							htmltext = "30659-04.html";
							break;
						}
						case 4:
						{
							htmltext = "30659-05.html";
							break;
						}
					}
					break;
				}
				case WINDY_SHAORING:
				{
					if (hasQuestItems(player, PICTURE_OF_WINDY))
					{
						htmltext = "30660-01.html";
					}
					else if (hasQuestItems(player, WINDYS_PEBBLES))
					{
						htmltext = "30660-04.html";
					}
					break;
				}
				case MYSTERIOUS_DARK_ELF:
				{
					if (memoState == 8)
					{
						if (hasQuestItems(player, SECRET_LETTER1) && !hasQuestItems(player, SECRET_LETTER2))
						{
							if (npc.getSummonedNpcCount() < 36)
							{
								addSpawn(npc, LERO_LIZARDMAN_ASSASSIN, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_ASSASSIN, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_ASSASSIN, npc, true, 200000);
								playSound(player, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
							}
							qs.setCond(14);
							npc.deleteMe();
							htmltext = "30661-01.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER1, SECRET_LETTER2) && !hasQuestItems(player, SECRET_LETTER3))
						{
							if (npc.getSummonedNpcCount() < 36)
							{
								addSpawn(npc, LERO_LIZARDMAN_SNIPER, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_SNIPER, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_SNIPER, npc, true, 200000);
								playSound(player, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
							}
							qs.setCond(16);
							npc.deleteMe();
							htmltext = "30661-02.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER1, SECRET_LETTER2, SECRET_LETTER3) && !hasQuestItems(player, SECRET_LETTER4))
						{
							if (npc.getSummonedNpcCount() < 36)
							{
								addSpawn(npc, LERO_LIZARDMAN_WIZARD, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_WIZARD, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_LORD, npc, true, 200000);
								playSound(player, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
							}
							qs.setCond(18);
							npc.deleteMe();
							htmltext = "30661-03.html";
						}
						else if ((getQuestItemsCount(player, SECRET_LETTER1) + getQuestItemsCount(player, SECRET_LETTER2) + getQuestItemsCount(player, SECRET_LETTER3) + getQuestItemsCount(player, SECRET_LETTER4)) == 4)
						{
							qs.setCond(20, true);
							htmltext = "30661-04.html";
						}
					}
					break;
				}
				case PIPER_LONGBOW:
				{
					if (memoState == 8)
					{
						if (hasQuestItems(player, SECRET_LETTER1) && !hasQuestItems(player, SECRET_LETTER2))
						{
							htmltext = "30662-01.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2) && !hasQuestItems(player, SECRET_LETTER3, SECRET_LETTER4))
						{
							htmltext = "30662-02.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2, SECRET_LETTER3, SECRET_LETTER4))
						{
							qs.setCond(21, true);
							htmltext = "30662-03.html";
						}
					}
					break;
				}
				case SLEIN_SHINING_BLADE:
				{
					if (memoState == 8)
					{
						if (hasQuestItems(player, SECRET_LETTER1) && !hasQuestItems(player, SECRET_LETTER2))
						{
							htmltext = "30663-01.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2) && !hasQuestItems(player, SECRET_LETTER3, SECRET_LETTER4))
						{
							htmltext = "30663-02.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2, SECRET_LETTER3, SECRET_LETTER4))
						{
							qs.setCond(21, true);
							htmltext = "30663-03.html";
						}
					}
					break;
				}
				case CAIN_FLYING_KNIFE:
				{
					if (memoState == 8)
					{
						if (hasQuestItems(player, SECRET_LETTER1) && !hasQuestItems(player, SECRET_LETTER4))
						{
							htmltext = "30664-01.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2) && !hasQuestItems(player, SECRET_LETTER3, SECRET_LETTER4))
						{
							htmltext = "30664-02.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER2, SECRET_LETTER3, SECRET_LETTER4))
						{
							qs.setCond(21, true);
							htmltext = "30664-03.html";
						}
					}
					break;
				}
				case SAINT_KRISTINA:
				{
					if ((getQuestItemsCount(player, SECRET_LETTER1) + getQuestItemsCount(player, SECRET_LETTER2) + getQuestItemsCount(player, SECRET_LETTER3) + getQuestItemsCount(player, SECRET_LETTER4)) == 4)
					{
						htmltext = "30665-01.html";
					}
					else if (memoState < 9)
					{
						if ((getQuestItemsCount(player, SECRET_LETTER1) + getQuestItemsCount(player, SECRET_LETTER2) + getQuestItemsCount(player, SECRET_LETTER3) + getQuestItemsCount(player, SECRET_LETTER4)) < 4)
						{
							htmltext = "30665-03.html";
						}
					}
					else if (memoState >= 9)
					{
						htmltext = "30665-04.html";
					}
					break;
				}
				case DAURIN_HAMMERCRUSH:
				{
					if (memoState == 6)
					{
						if (hasQuestItems(player, ORDER_OF_SORIUS))
						{
							htmltext = "30674-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, SECRET_LETTER1, ORDER_OF_SORIUS))
						{
							if (npc.getSummonedNpcCount() < 4)
							{
								addSpawn(npc, LERO_LIZARDMAN_AGENT, npc, true, 200000);
								addSpawn(npc, LERO_LIZARDMAN_LEADER, npc, true, 200000);
							}
							htmltext = "30674-02a.html";
						}
						else if (hasQuestItems(player, SECRET_LETTER1))
						{
							qs.setMemoState(8);
							qs.setCond(13, true);
							htmltext = "30674-03.html";
						}
					}
					else if (memoState >= 8)
					{
						htmltext = "30674-04.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == PRIEST_BANDELLOS)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}