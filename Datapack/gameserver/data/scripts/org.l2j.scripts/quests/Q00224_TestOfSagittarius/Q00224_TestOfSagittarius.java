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
package quests.Q00224_TestOfSagittarius;

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
 * Test Of Sagittarius (224)
 * @author ivantotov
 */
public final class Q00224_TestOfSagittarius extends Quest
{
	// NPCs
	private static final int PREFECT_VOKIAN = 30514;
	private static final int SAGITTARIUS_HAMIL = 30626;
	private static final int SIR_ARON_TANFORD = 30653;
	private static final int GUILD_PRESIDENT_BERNARD = 30702;
	private static final int MAGISTER_GAUEN = 30717;
	// Items
	private static final int WOODEN_ARROW = 17;
	private static final int CRESCENT_MOON_BOW = 3028;
	private static final int BERNARDS_INTRODUCTION = 3294;
	private static final int HAMILS_1ST_LETTER = 3295;
	private static final int HAMILS_2ND_LETTER = 3296;
	private static final int HAMILS_3RD_LETTER = 3297;
	private static final int HUNTERS_1ST_RUNE = 3298;
	private static final int HUNTERS_2ND_RUNE = 3299;
	private static final int TALISMAN_OF_KADESH = 3300;
	private static final int TALISMAN_OF_SNAKE = 3301;
	private static final int MITHRIL_CLIP = 3302;
	private static final int STAKATO_CHITIN = 3303;
	private static final int REINFORCED_BOWSTRING = 3304;
	private static final int MANASHENS_HORN = 3305;
	private static final int BLOOD_OF_LIZARDMAN = 3306;
	// Reward
	private static final int MARK_OF_SAGITTARIUS = 3293;
	// Monster
	private static final int ANT = 20079;
	private static final int ANT_CAPTAIN = 20080;
	private static final int ANT_OVERSEER = 20081;
	private static final int ANT_RECRUIT = 20082;
	private static final int ANT_PATROL = 20084;
	private static final int ANT_GUARD = 20086;
	private static final int NOBLE_ANT = 20089;
	private static final int NOBLE_ANT_LEADER = 20090;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_SPIDER = 20233;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int MANASHEN_GARGOYLE = 20563;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	// Quest Monster
	private static final int SERPENT_DEMON_KADESH = 27090;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00224_TestOfSagittarius()
	{
		super(224);
		addStartNpc(GUILD_PRESIDENT_BERNARD);
		addTalkId(GUILD_PRESIDENT_BERNARD, PREFECT_VOKIAN, SAGITTARIUS_HAMIL, SIR_ARON_TANFORD, MAGISTER_GAUEN);
		addKillId(ANT, ANT_CAPTAIN, ANT_OVERSEER, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, NOBLE_ANT, NOBLE_ANT_LEADER, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_SPIDER, MARSH_STAKATO_DRONE, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, ROAD_SCAVENGER, MANASHEN_GARGOYLE, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, SERPENT_DEMON_KADESH);
		registerQuestItems(CRESCENT_MOON_BOW, BERNARDS_INTRODUCTION, HAMILS_1ST_LETTER, HAMILS_2ND_LETTER, HAMILS_3RD_LETTER, HUNTERS_1ST_RUNE, HUNTERS_2ND_RUNE, TALISMAN_OF_KADESH, TALISMAN_OF_SNAKE, MITHRIL_CLIP, STAKATO_CHITIN, REINFORCED_BOWSTRING, MANASHENS_HORN, BLOOD_OF_LIZARDMAN);
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
					giveItems(player, BERNARDS_INTRODUCTION, 1);
				}
				break;
			}
			case "30514-02.html":
			{
				if (hasQuestItems(player, HAMILS_2ND_LETTER))
				{
					takeItems(player, HAMILS_2ND_LETTER, 1);
					qs.setMemoState(6);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30626-02.html":
			case "30626-06.html":
			{
				htmltext = event;
				break;
			}
			case "30626-03.html":
			{
				if (hasQuestItems(player, BERNARDS_INTRODUCTION))
				{
					takeItems(player, BERNARDS_INTRODUCTION, 1);
					giveItems(player, HAMILS_1ST_LETTER, 1);
					qs.setMemoState(2);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30626-07.html":
			{
				if (getQuestItemsCount(player, HUNTERS_1ST_RUNE) >= 10)
				{
					giveItems(player, HAMILS_2ND_LETTER, 1);
					takeItems(player, HUNTERS_1ST_RUNE, -1);
					qs.setMemoState(5);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30653-02.html":
			{
				if (hasQuestItems(player, HAMILS_1ST_LETTER))
				{
					takeItems(player, HAMILS_1ST_LETTER, 1);
					qs.setMemoState(3);
					qs.setCond(3, true);
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
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case ANT:
				case ANT_CAPTAIN:
				case ANT_OVERSEER:
				case ANT_RECRUIT:
				case ANT_PATROL:
				case ANT_GUARD:
				case NOBLE_ANT:
				case NOBLE_ANT_LEADER:
				{
					if (qs.isMemoState(3) && (getQuestItemsCount(killer, HUNTERS_1ST_RUNE) < 10))
					{
						if (getQuestItemsCount(killer, HUNTERS_1ST_RUNE) == 9)
						{
							giveItems(killer, HUNTERS_1ST_RUNE, 1);
							qs.setMemoState(4);
							qs.setCond(4, true);
						}
						else
						{
							giveItems(killer, HUNTERS_1ST_RUNE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_STAKATO_WORKER:
				case MARSH_STAKATO_SOLDIER:
				case MARSH_STAKATO_DRONE:
				{
					if (qs.isMemoState(10) && !hasQuestItems(killer, STAKATO_CHITIN))
					{
						if (hasQuestItems(killer, MITHRIL_CLIP, REINFORCED_BOWSTRING, MANASHENS_HORN))
						{
							giveItems(killer, STAKATO_CHITIN, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
						}
						else
						{
							giveItems(killer, STAKATO_CHITIN, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_SPIDER:
				{
					if (qs.isMemoState(10) && !hasQuestItems(killer, REINFORCED_BOWSTRING))
					{
						if (hasQuestItems(killer, MITHRIL_CLIP, MANASHENS_HORN, STAKATO_CHITIN))
						{
							giveItems(killer, REINFORCED_BOWSTRING, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
						}
						else
						{
							giveItems(killer, REINFORCED_BOWSTRING, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BREKA_ORC_SHAMAN:
				case BREKA_ORC_OVERLORD:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, HUNTERS_2ND_RUNE) < 10))
					{
						if (getQuestItemsCount(killer, HUNTERS_2ND_RUNE) == 9)
						{
							giveItems(killer, HUNTERS_2ND_RUNE, 1);
							giveItems(killer, TALISMAN_OF_SNAKE, 1);
							qs.setMemoState(7);
							qs.setCond(7, true);
						}
						else
						{
							giveItems(killer, HUNTERS_2ND_RUNE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ROAD_SCAVENGER:
				{
					if (qs.isMemoState(10) && !hasQuestItems(killer, MITHRIL_CLIP))
					{
						if (hasQuestItems(killer, REINFORCED_BOWSTRING, MANASHENS_HORN, STAKATO_CHITIN))
						{
							giveItems(killer, MITHRIL_CLIP, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
						}
						else
						{
							giveItems(killer, MITHRIL_CLIP, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MANASHEN_GARGOYLE:
				{
					if (qs.isMemoState(10) && !hasQuestItems(killer, MANASHENS_HORN))
					{
						if (hasQuestItems(killer, MITHRIL_CLIP, REINFORCED_BOWSTRING, STAKATO_CHITIN))
						{
							giveItems(killer, MANASHENS_HORN, 1);
							qs.setMemoState(11);
							qs.setCond(11, true);
						}
						else
						{
							giveItems(killer, MANASHENS_HORN, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LETO_LIZARDMAN:
				case LETO_LIZARDMAN_ARCHER:
				case LETO_LIZARDMAN_SOLDIER:
				case LETO_LIZARDMAN_WARRIOR:
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (qs.isMemoState(13) && (getQuestItemsCount(killer, BLOOD_OF_LIZARDMAN) < 140))
					{
						if (((getQuestItemsCount(killer, BLOOD_OF_LIZARDMAN) - 10) * 5) > getRandom(100))
						{
							addSpawn(SERPENT_DEMON_KADESH, npc, true, 300000);
							takeItems(killer, BLOOD_OF_LIZARDMAN, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
						}
						else
						{
							giveItems(killer, BLOOD_OF_LIZARDMAN, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case SERPENT_DEMON_KADESH:
				{
					if (qs.isMemoState(13) && !hasQuestItems(killer, TALISMAN_OF_KADESH))
					{
						if (npc.getKillingBlowWeapon() == CRESCENT_MOON_BOW)
						{
							giveItems(killer, TALISMAN_OF_KADESH, 1);
							qs.setMemoState(14);
							qs.setCond(14, true);
						}
						else
						{
							addSpawn(SERPENT_DEMON_KADESH, npc, true, 300000);
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
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == GUILD_PRESIDENT_BERNARD)
			{
				if ((player.getClassId() == ClassId.ROGUE) || (player.getClassId() == ClassId.ELVEN_SCOUT) || (player.getClassId() == ClassId.ASSASSIN))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30702-03.htm";
					}
					else
					{
						htmltext = "30702-01.html";
					}
				}
				else
				{
					htmltext = "30702-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case GUILD_PRESIDENT_BERNARD:
				{
					if (hasQuestItems(player, BERNARDS_INTRODUCTION))
					{
						htmltext = "30702-05.html";
					}
					break;
				}
				case PREFECT_VOKIAN:
				{
					if (memoState == 5)
					{
						if (hasQuestItems(player, HAMILS_2ND_LETTER))
						{
							htmltext = "30514-01.html";
						}
					}
					else if (memoState == 6)
					{
						htmltext = "30514-03.html";
					}
					else if (memoState == 7)
					{
						if (hasQuestItems(player, TALISMAN_OF_SNAKE))
						{
							takeItems(player, TALISMAN_OF_SNAKE, 1);
							qs.setMemoState(8);
							qs.setCond(8, true);
							htmltext = "30514-04.html";
						}
					}
					else if (memoState == 8)
					{
						htmltext = "30514-05.html";
					}
					break;
				}
				case SAGITTARIUS_HAMIL:
				{
					if (memoState == 1)
					{
						if (hasQuestItems(player, BERNARDS_INTRODUCTION))
						{
							htmltext = "30626-01.html";
						}
					}
					else if (memoState == 2)
					{
						if (hasQuestItems(player, HAMILS_1ST_LETTER))
						{
							htmltext = "30626-04.html";
						}
					}
					else if (memoState == 4)
					{
						if (getQuestItemsCount(player, HUNTERS_1ST_RUNE) == 10)
						{
							htmltext = "30626-05.html";
						}
					}
					else if (memoState == 5)
					{
						if (hasQuestItems(player, HAMILS_2ND_LETTER))
						{
							htmltext = "30626-08.html";
						}
					}
					else if (memoState == 8)
					{
						giveItems(player, HAMILS_3RD_LETTER, 1);
						takeItems(player, HUNTERS_2ND_RUNE, -1);
						qs.setMemoState(9);
						qs.setCond(9, true);
						htmltext = "30626-09.html";
					}
					else if (memoState == 9)
					{
						if (hasQuestItems(player, HAMILS_3RD_LETTER))
						{
							htmltext = "30626-10.html";
						}
					}
					else if (memoState == 12)
					{
						if (hasQuestItems(player, CRESCENT_MOON_BOW))
						{
							qs.setCond(13, true);
							qs.setMemoState(13);
							htmltext = "30626-11.html";
						}
					}
					else if (memoState == 13)
					{
						htmltext = "30626-12.html";
					}
					else if (memoState == 14)
					{
						if (hasQuestItems(player, TALISMAN_OF_KADESH))
						{
							giveAdena(player, 161806, true);
							giveItems(player, MARK_OF_SAGITTARIUS, 1);
							addExpAndSp(player, 894888, 61408);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30626-13.html";
						}
					}
					break;
				}
				case SIR_ARON_TANFORD:
				{
					if (memoState == 2)
					{
						if (hasQuestItems(player, HAMILS_1ST_LETTER))
						{
							htmltext = "30653-01.html";
						}
					}
					else if (memoState == 3)
					{
						htmltext = "30653-03.html";
					}
					break;
				}
				case MAGISTER_GAUEN:
				{
					if (memoState == 9)
					{
						if (hasQuestItems(player, HAMILS_3RD_LETTER))
						{
							takeItems(player, HAMILS_3RD_LETTER, 1);
							qs.setMemoState(10);
							qs.setCond(10, true);
							htmltext = "30717-01.html";
						}
					}
					else if (memoState == 10)
					{
						htmltext = "30717-03.html";
					}
					else if (memoState == 12)
					{
						htmltext = "30717-04.html";
					}
					else if (memoState == 11)
					{
						if (hasQuestItems(player, STAKATO_CHITIN, MITHRIL_CLIP, REINFORCED_BOWSTRING, MANASHENS_HORN))
						{
							giveItems(player, WOODEN_ARROW, 10);
							giveItems(player, CRESCENT_MOON_BOW, 1);
							takeItems(player, MITHRIL_CLIP, 1);
							takeItems(player, STAKATO_CHITIN, 1);
							takeItems(player, REINFORCED_BOWSTRING, 1);
							takeItems(player, MANASHENS_HORN, 1);
							qs.setMemoState(12);
							qs.setCond(12, true);
							htmltext = "30717-02.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == GUILD_PRESIDENT_BERNARD)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}