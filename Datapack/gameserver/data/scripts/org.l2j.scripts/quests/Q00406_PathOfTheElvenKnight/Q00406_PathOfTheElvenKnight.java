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
package quests.Q00406_PathOfTheElvenKnight;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Path Of The Elven Knight (406)
 * @author ivantotov
 */
public final class Q00406_PathOfTheElvenKnight extends Quest
{
	// NPCs
	private static final int BLACKSMITH_KLUTO = 30317;
	private static final int MASTER_SORIUS = 30327;
	// Items
	private static final int SORIUS_LETTER = 1202;
	private static final int KLUTO_BOX = 1203;
	private static final int TOPAZ_PIECE = 1205;
	private static final int EMERALD_PIECE = 1206;
	private static final int KLUTO_MEMO = 1276;
	// Reward
	private static final int ELVEN_KNIGHT_BROOCH = 1204;
	// Misc
	private static final int MIN_LEVEL = 19;
	// Mobs
	private static final int OL_MAHUM_NOVICE = 20782;
	private static final Map<Integer, ItemChanceHolder> MONSTER_DROPS = new HashMap<>();
	static
	{
		MONSTER_DROPS.put(20035, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Tracker Skeleton
		MONSTER_DROPS.put(20042, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Tracker Skeleton Leader
		MONSTER_DROPS.put(20045, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Skeleton Scout
		MONSTER_DROPS.put(20051, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Skeleton Bowman
		MONSTER_DROPS.put(20054, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Ruin Spartoi
		MONSTER_DROPS.put(20060, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Salamander Noble
		MONSTER_DROPS.put(OL_MAHUM_NOVICE, new ItemChanceHolder(EMERALD_PIECE, 50)); // Ol Mahum Novice
	}
	
	public Q00406_PathOfTheElvenKnight()
	{
		super(406);
		addStartNpc(MASTER_SORIUS);
		addTalkId(MASTER_SORIUS, BLACKSMITH_KLUTO);
		addKillId(MONSTER_DROPS.keySet());
		registerQuestItems(SORIUS_LETTER, KLUTO_BOX, TOPAZ_PIECE, EMERALD_PIECE, KLUTO_MEMO);
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
				if (player.getClassId() != ClassId.ELVEN_FIGHTER)
				{
					if (player.getClassId() == ClassId.ELVEN_KNIGHT)
					{
						htmltext = "30327-02a.htm";
					}
					else
					{
						htmltext = "30327-02.htm";
					}
				}
				else if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30327-03.htm";
				}
				else if (hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
				{
					htmltext = "30327-04.htm";
				}
				else
				{
					htmltext = "30327-05.htm";
				}
				break;
			}
			case "30327-06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30317-02.html":
			{
				takeItems(player, SORIUS_LETTER, 1);
				if (!hasQuestItems(player, KLUTO_MEMO))
				{
					giveItems(player, KLUTO_MEMO, 1);
				}
				qs.setCond(4, true);
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
		final ItemChanceHolder reward = MONSTER_DROPS.get(npc.getId());
		int requiredItemId = KLUTO_BOX;
		int cond = 2;
		boolean check = !hasQuestItems(killer, requiredItemId);
		if (npc.getId() == OL_MAHUM_NOVICE)
		{
			requiredItemId = KLUTO_MEMO;
			cond = 5;
			check = hasQuestItems(killer, requiredItemId);
		}
		
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, false))
		{
			if (check && (getQuestItemsCount(killer, reward.getId()) < 20) && (getRandom(100) < reward.getChance()))
			{
				giveItems(killer, reward);
				if (getQuestItemsCount(killer, reward.getId()) == 20)
				{
					qs.setCond(cond, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
			if (npc.getId() == MASTER_SORIUS)
			{
				htmltext = "30327-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_SORIUS)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_SORIUS:
				{
					if (!hasQuestItems(player, KLUTO_BOX))
					{
						if (!hasQuestItems(player, TOPAZ_PIECE))
						{
							htmltext = "30327-07.html";
						}
						else if (hasQuestItems(player, TOPAZ_PIECE) && (getQuestItemsCount(player, TOPAZ_PIECE) < 20))
						{
							htmltext = "30327-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, KLUTO_MEMO, SORIUS_LETTER) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							if (!hasQuestItems(player, SORIUS_LETTER))
							{
								giveItems(player, SORIUS_LETTER, 1);
							}
							qs.setCond(3, true);
							htmltext = "30327-09.html";
						}
						else if ((getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && hasAtLeastOneQuestItem(player, SORIUS_LETTER, KLUTO_MEMO))
						{
							htmltext = "30327-11.html";
						}
					}
					else
					{
						if (!hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
						{
							giveItems(player, ELVEN_KNIGHT_BROOCH, 1);
						}
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
						htmltext = "30327-10.html";
					}
					break;
				}
				case BLACKSMITH_KLUTO:
				{
					if (!hasQuestItems(player, KLUTO_BOX))
					{
						if (hasQuestItems(player, SORIUS_LETTER) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							htmltext = "30317-01.html";
						}
						else if (!hasQuestItems(player, EMERALD_PIECE) && hasQuestItems(player, KLUTO_MEMO) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							htmltext = "30317-03.html";
						}
						else if (hasQuestItems(player, KLUTO_MEMO, EMERALD_PIECE) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && (getQuestItemsCount(player, EMERALD_PIECE) < 20))
						{
							htmltext = "30317-04.html";
						}
						else if (hasQuestItems(player, KLUTO_MEMO) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && (getQuestItemsCount(player, EMERALD_PIECE) >= 20))
						{
							if (!hasQuestItems(player, KLUTO_BOX))
							{
								giveItems(player, KLUTO_BOX, 1);
							}
							takeItems(player, TOPAZ_PIECE, -1);
							takeItems(player, EMERALD_PIECE, -1);
							takeItems(player, KLUTO_MEMO, 1);
							qs.setCond(6, true);
							htmltext = "30317-05.html";
						}
					}
					else if (hasQuestItems(player, KLUTO_BOX))
					{
						htmltext = "30317-06.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}