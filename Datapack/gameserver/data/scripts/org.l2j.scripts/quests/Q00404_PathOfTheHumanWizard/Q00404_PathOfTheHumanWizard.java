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
package quests.Q00404_PathOfTheHumanWizard;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Path Of The Human Wizard (404)
 * @author ivantotov
 */
public final class Q00404_PathOfTheHumanWizard extends Quest
{
	// NPCs
	private static final int PARINA = 30391;
	private static final int EARTH_SNAKE = 30409;
	private static final int WASTELAND_LIZARDMAN = 30410;
	private static final int FLAME_SALAMANDER = 30411;
	private static final int WIND_SYLPH = 30412;
	private static final int WATER_UNDINE = 30413;
	// Items
	private static final int MAP_OF_LUSTER = 1280;
	private static final int KEY_OF_FLAME = 1281;
	private static final int FLAME_EARING = 1282;
	private static final int BROKEN_BRONZE_MIRROR = 1283;
	private static final int WIND_FEATHER = 1284;
	private static final int WIND_BANGLE = 1285;
	private static final int RAMAS_DIARY = 1286;
	private static final int SPARKLE_PEBBLE = 1287;
	private static final int WATER_NECKLACE = 1288;
	private static final int RUSTY_COIN = 1289;
	private static final int RED_SOIL = 1290;
	private static final int EARTH_RING = 1291;
	// Reward
	private static final int BEAD_OF_SEASON = 1292;
	// Monster
	private static final int RED_BEAR = 20021;
	private static final int RATMAN_WARRIOR = 20359;
	// Quest Monster
	private static final int WATER_SEER = 27030;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00404_PathOfTheHumanWizard()
	{
		super(404);
		addStartNpc(PARINA);
		addTalkId(PARINA, EARTH_SNAKE, WASTELAND_LIZARDMAN, FLAME_SALAMANDER, WIND_SYLPH, WATER_UNDINE);
		addKillId(RED_BEAR, RATMAN_WARRIOR, WATER_SEER);
		registerQuestItems(MAP_OF_LUSTER, KEY_OF_FLAME, FLAME_EARING, BROKEN_BRONZE_MIRROR, WIND_FEATHER, WIND_BANGLE, RAMAS_DIARY, SPARKLE_PEBBLE, WATER_NECKLACE, RUSTY_COIN, RED_SOIL, EARTH_RING);
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
				if (player.getClassId() == ClassId.MAGE)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, BEAD_OF_SEASON))
						{
							htmltext = "30391-03.htm";
						}
						else
						{
							qs.startQuest();
							htmltext = "30391-07.htm";
						}
					}
					else
					{
						htmltext = "30391-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.WIZARD)
				{
					htmltext = "30391-02a.htm";
				}
				else
				{
					htmltext = "30391-01.htm";
				}
				break;
			}
			case "30410-02.html":
			{
				htmltext = event;
				break;
			}
			case "30410-03.html":
			{
				giveItems(player, WIND_FEATHER, 1);
				qs.setCond(6, true);
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
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case RED_BEAR:
				{
					if (hasQuestItems(killer, RUSTY_COIN) && !hasQuestItems(killer, RED_SOIL) && (getRandom(100) < 20))
					{
						giveItems(killer, RED_SOIL, 1);
						qs.setCond(12, true);
					}
					break;
				}
				case RATMAN_WARRIOR:
				{
					if (hasQuestItems(killer, MAP_OF_LUSTER) && !hasQuestItems(killer, KEY_OF_FLAME) && (getRandom(100) < 80))
					{
						giveItems(killer, KEY_OF_FLAME, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case WATER_SEER:
				{
					if (hasQuestItems(killer, RAMAS_DIARY) && (getQuestItemsCount(killer, SPARKLE_PEBBLE) < 2) && (getRandom(100) < 80))
					{
						giveItems(killer, SPARKLE_PEBBLE, 1);
						if (getQuestItemsCount(killer, SPARKLE_PEBBLE) == 2)
						{
							qs.setCond(9, true);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
			if (npc.getId() == PARINA)
			{
				htmltext = "30391-04.htm";
			}
		}
		if (qs.isCompleted())
		{
			if (npc.getId() == PARINA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PARINA:
				{
					if (!hasQuestItems(player, FLAME_EARING, WIND_BANGLE, WATER_NECKLACE, EARTH_RING))
					{
						htmltext = "30391-05.html";
					}
					else
					{
						takeItems(player, FLAME_EARING, 1);
						takeItems(player, WIND_BANGLE, 1);
						takeItems(player, WATER_NECKLACE, 1);
						takeItems(player, EARTH_RING, 1);
						if (!hasQuestItems(player, BEAD_OF_SEASON))
						{
							giveItems(player, BEAD_OF_SEASON, 1);
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
						htmltext = "30391-06.html";
					}
					break;
				}
				case EARTH_SNAKE:
				{
					if (hasQuestItems(player, WATER_NECKLACE) && !hasAtLeastOneQuestItem(player, RUSTY_COIN, EARTH_RING))
					{
						if (!hasQuestItems(player, RUSTY_COIN))
						{
							giveItems(player, RUSTY_COIN, 1);
						}
						qs.setCond(11, true);
						htmltext = "30409-01.html";
					}
					else if (hasQuestItems(player, RUSTY_COIN))
					{
						if (!hasQuestItems(player, RED_SOIL))
						{
							htmltext = "30409-02.html";
						}
						else
						{
							takeItems(player, RUSTY_COIN, 1);
							takeItems(player, RED_SOIL, 1);
							if (!hasQuestItems(player, EARTH_RING))
							{
								giveItems(player, EARTH_RING, 1);
							}
							qs.setCond(13, true);
							htmltext = "30409-03.html";
						}
					}
					else if (hasQuestItems(player, EARTH_RING))
					{
						htmltext = "30409-04.html";
					}
					break;
				}
				case WASTELAND_LIZARDMAN:
				{
					if (hasQuestItems(player, BROKEN_BRONZE_MIRROR))
					{
						if (!hasQuestItems(player, WIND_FEATHER))
						{
							htmltext = "30410-01.html";
						}
						else
						{
							htmltext = "30410-04.html";
						}
					}
					break;
				}
				case FLAME_SALAMANDER:
				{
					if (!hasAtLeastOneQuestItem(player, MAP_OF_LUSTER, FLAME_EARING))
					{
						if (!hasQuestItems(player, MAP_OF_LUSTER))
						{
							giveItems(player, MAP_OF_LUSTER, 1);
						}
						qs.setCond(2, true);
						htmltext = "30411-01.html";
					}
					else if (hasQuestItems(player, MAP_OF_LUSTER))
					{
						if (!hasQuestItems(player, KEY_OF_FLAME))
						{
							htmltext = "30411-02.html";
						}
						else
						{
							takeItems(player, MAP_OF_LUSTER, 1);
							takeItems(player, KEY_OF_FLAME, 1);
							if (!hasQuestItems(player, FLAME_EARING))
							{
								giveItems(player, FLAME_EARING, 1);
							}
							qs.setCond(4, true);
							htmltext = "30411-03.html";
						}
					}
					else if (hasQuestItems(player, FLAME_EARING))
					{
						htmltext = "30411-04.html";
					}
					break;
				}
				case WIND_SYLPH:
				{
					if (hasQuestItems(player, FLAME_EARING) && !hasAtLeastOneQuestItem(player, BROKEN_BRONZE_MIRROR, WIND_BANGLE))
					{
						if (!hasQuestItems(player, BROKEN_BRONZE_MIRROR))
						{
							giveItems(player, BROKEN_BRONZE_MIRROR, 1);
						}
						qs.setCond(5, true);
						htmltext = "30412-01.html";
					}
					else if (hasQuestItems(player, BROKEN_BRONZE_MIRROR))
					{
						if (!hasQuestItems(player, WIND_FEATHER))
						{
							htmltext = "30412-02.html";
						}
						else
						{
							takeItems(player, BROKEN_BRONZE_MIRROR, 1);
							takeItems(player, WIND_FEATHER, 1);
							if (!hasQuestItems(player, WIND_BANGLE))
							{
								giveItems(player, WIND_BANGLE, 1);
							}
							qs.setCond(7, true);
							htmltext = "30412-03.html";
						}
					}
					else if (hasQuestItems(player, WIND_BANGLE))
					{
						htmltext = "30412-04.html";
					}
					break;
				}
				case WATER_UNDINE:
				{
					if (hasQuestItems(player, WIND_BANGLE) && !hasAtLeastOneQuestItem(player, RAMAS_DIARY, WATER_NECKLACE))
					{
						if (!hasQuestItems(player, RAMAS_DIARY))
						{
							giveItems(player, RAMAS_DIARY, 1);
						}
						qs.setCond(8, true);
						htmltext = "30413-01.html";
					}
					else if (hasQuestItems(player, RAMAS_DIARY))
					{
						if (getQuestItemsCount(player, SPARKLE_PEBBLE) < 2)
						{
							htmltext = "30413-02.html";
						}
						else
						{
							takeItems(player, RAMAS_DIARY, 1);
							takeItems(player, SPARKLE_PEBBLE, -1);
							if (!hasQuestItems(player, WATER_NECKLACE))
							{
								giveItems(player, WATER_NECKLACE, 1);
							}
							qs.setCond(10, true);
							htmltext = "30413-03.html";
						}
					}
					else if (hasQuestItems(player, WATER_NECKLACE))
					{
						htmltext = "30413-04.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}