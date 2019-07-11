/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00414_PathOfTheOrcRaider;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path Of The Orc Raider (414)
 * @author ivantotov
 */
public final class Q00414_PathOfTheOrcRaider extends Quest
{
	// NPCs
	private static final int PREFECT_KARUKIA = 30570;
	private static final int PREFRCT_KASMAN = 30501;
	// Items
	private static final int GREEN_BLOOD = 1578;
	private static final int GOBLIN_DWELLING_MAP = 1579;
	private static final int KURUKA_RATMAN_TOOTH = 1580;
	private static final int BETRAYER_UMBAR_REPORT = 1589;
	private static final int BETRAYER_ZAKAN_REPORT = 1590;
	private static final int HEAD_OF_BETRAYER = 1591;
	private static final int TIMORA_ORC_HEAD = 8544;
	// Reward
	private static final int MARK_OF_RAIDER = 1592;
	// Quest Monster
	private static final int KURUKA_RATMAN_LEADER = 27045;
	private static final int UMBAR_ORC = 27054;
	// Monster
	private static final int GOBLIN_TOMB_RAIDER_LEADER = 20320;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00414_PathOfTheOrcRaider()
	{
		super(414);
		addStartNpc(PREFECT_KARUKIA);
		addTalkId(PREFECT_KARUKIA, PREFRCT_KASMAN);
		addKillId(KURUKA_RATMAN_LEADER, UMBAR_ORC, GOBLIN_TOMB_RAIDER_LEADER);
		registerQuestItems(GREEN_BLOOD, GOBLIN_DWELLING_MAP, KURUKA_RATMAN_TOOTH, BETRAYER_UMBAR_REPORT, BETRAYER_ZAKAN_REPORT, HEAD_OF_BETRAYER, TIMORA_ORC_HEAD);
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
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.ORC_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, MARK_OF_RAIDER))
						{
							htmltext = "30570-04.htm";
						}
						else
						{
							if (!hasQuestItems(player, GOBLIN_DWELLING_MAP))
							{
								giveItems(player, GOBLIN_DWELLING_MAP, 1);
							}
							qs.startQuest();
							htmltext = "30570-05.htm";
						}
					}
					else
					{
						htmltext = "30570-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.ORC_RAIDER)
				{
					htmltext = "30570-02a.htm";
				}
				else
				{
					htmltext = "30570-03.htm";
				}
				break;
			}
			case "30570-07a.htm":
			{
				if (hasQuestItems(player, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(player, KURUKA_RATMAN_TOOTH) >= 10))
				{
					takeItems(player, GOBLIN_DWELLING_MAP, 1);
					takeItems(player, KURUKA_RATMAN_TOOTH, -1);
					giveItems(player, BETRAYER_UMBAR_REPORT, 1);
					giveItems(player, BETRAYER_ZAKAN_REPORT, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30570-07b.htm":
			{
				if (hasQuestItems(player, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(player, KURUKA_RATMAN_TOOTH) >= 10))
				{
					takeItems(player, GOBLIN_DWELLING_MAP, 1);
					takeItems(player, KURUKA_RATMAN_TOOTH, -1);
					qs.setCond(5, true);
					qs.setMemoState(2);
					htmltext = event;
				}
				break;
			}
			case "31978-04.htm":
			{
				if (qs.isMemoState(2))
				{
					htmltext = event;
				}
				break;
			}
			case "31978-02.htm":
			{
				if (qs.isMemoState(2))
				{
					qs.setMemoState(3);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case GOBLIN_TOMB_RAIDER_LEADER:
				{
					if (hasQuestItems(killer, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(killer, KURUKA_RATMAN_TOOTH) < 10) && (getQuestItemsCount(killer, GREEN_BLOOD) <= 20))
					{
						if (getQuestItemsCount(killer, GREEN_BLOOD) <= getRandom(20))
						{
							giveItems(killer, GREEN_BLOOD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else
						{
							takeItems(killer, GREEN_BLOOD, -1);
							final L2Attackable monster = (L2Attackable) addSpawn(KURUKA_RATMAN_LEADER, npc, true, 0, true);
							attackPlayer(monster, killer);
						}
					}
					break;
				}
				case KURUKA_RATMAN_LEADER:
				{
					if (hasQuestItems(killer, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(killer, KURUKA_RATMAN_TOOTH) < 10))
					{
						takeItems(killer, GREEN_BLOOD, -1);
						if (getQuestItemsCount(killer, KURUKA_RATMAN_TOOTH) >= 9)
						{
							giveItems(killer, KURUKA_RATMAN_TOOTH, 1);
							qs.setCond(2, true);
						}
						else
						{
							giveItems(killer, KURUKA_RATMAN_TOOTH, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case UMBAR_ORC:
				{
					if (hasAtLeastOneQuestItem(killer, BETRAYER_UMBAR_REPORT, BETRAYER_ZAKAN_REPORT) && (getQuestItemsCount(killer, HEAD_OF_BETRAYER) < 2) && (getRandom(10) < 2))
					{
						giveItems(killer, HEAD_OF_BETRAYER, 1);
						if (hasQuestItems(killer, BETRAYER_ZAKAN_REPORT))
						{
							takeItems(killer, BETRAYER_ZAKAN_REPORT, 1);
						}
						else if (hasQuestItems(killer, BETRAYER_UMBAR_REPORT))
						{
							takeItems(killer, BETRAYER_UMBAR_REPORT, 1);
						}
						if (getQuestItemsCount(killer, HEAD_OF_BETRAYER) == 2)
						{
							qs.setCond(4, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == PREFECT_KARUKIA)
			{
				htmltext = "30570-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == PREFECT_KARUKIA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PREFECT_KARUKIA:
				{
					if (hasQuestItems(player, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(player, KURUKA_RATMAN_TOOTH) < 10))
					{
						htmltext = "30570-06.htm";
					}
					else if (hasQuestItems(player, GOBLIN_DWELLING_MAP) && (getQuestItemsCount(player, KURUKA_RATMAN_TOOTH) >= 10))
					{
						if (!hasAtLeastOneQuestItem(player, BETRAYER_UMBAR_REPORT, BETRAYER_ZAKAN_REPORT))
						{
							htmltext = "30570-07.htm";
						}
					}
					else if (hasQuestItems(player, HEAD_OF_BETRAYER) || hasAtLeastOneQuestItem(player, BETRAYER_UMBAR_REPORT, BETRAYER_ZAKAN_REPORT))
					{
						htmltext = "30570-08.htm";
					}
					else if (qs.isMemoState(2))
					{
						htmltext = "30570-07b.htm";
					}
					break;
				}
				case PREFRCT_KASMAN:
				{
					if (!hasQuestItems(player, HEAD_OF_BETRAYER) && (getQuestItemsCount(player, BETRAYER_UMBAR_REPORT, BETRAYER_ZAKAN_REPORT) >= 2))
					{
						htmltext = "30501-01.htm";
					}
					else if (getQuestItemsCount(player, HEAD_OF_BETRAYER) == 1)
					{
						htmltext = "30501-02.htm";
					}
					else if (getQuestItemsCount(player, HEAD_OF_BETRAYER) == 2)
					{
						giveItems(player, MARK_OF_RAIDER, 1);
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else if (level == 19)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else
						{
							addExpAndSp(player, 80314, 5087);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30501-03.htm";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	private static void attackPlayer(L2Attackable npc, Player player)
	{
		if ((npc != null) && (player != null))
		{
			npc.setRunning();
			npc.addDamageHate(player, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
	}
}