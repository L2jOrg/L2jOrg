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
package quests.Q00222_TestOfTheDuelist;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Test Of The Duelist (222)
 * @author ivantotov
 */
public final class Q00222_TestOfTheDuelist extends Quest
{
	// NPC
	private static final int DUELIST_KAIEN = 30623;
	// Items
	private static final int ORDER_GLUDIO = 2763;
	private static final int ORDER_DION = 2764;
	private static final int ORDER_GIRAN = 2765;
	private static final int ORDER_OREN = 2766;
	private static final int ORDER_ADEN = 2767;
	private static final int PUNCHERS_SHARD = 2768;
	private static final int NOBLE_ANTS_FEELER = 2769;
	private static final int DRONES_CHITIN = 2770;
	private static final int DEAD_SEEKER_FANG = 2771;
	private static final int OVERLORD_NECKLACE = 2772;
	private static final int FETTERED_SOULS_CHAIN = 2773;
	private static final int CHIEDS_AMULET = 2774;
	private static final int ENCHANTED_EYE_MEAT = 2775;
	private static final int TAMRIN_ORCS_RING = 2776;
	private static final int TAMRIN_ORCS_ARROW = 2777;
	private static final int FINAL_ORDER = 2778;
	private static final int EXCUROS_SKIN = 2779;
	private static final int KRATORS_SHARD = 2780;
	private static final int GRANDIS_SKIN = 2781;
	private static final int TIMAK_ORCS_BELT = 2782;
	private static final int LAKINS_MACE = 2783;
	// Reward
	private static final int MARK_OF_DUELIST = 2762;
	// Monster
	private static final int PUNCHER = 20085;
	private static final int NOBLE_ANT_LEADER = 20090;
	private static final int DEAD_SEEKER = 20202;
	private static final int EXCURO = 20214;
	private static final int KRATOR = 20217;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int FETTERED_SOUL = 20552;
	private static final int GRANDIS = 20554;
	private static final int ENCHANTED_MONSTEREYE = 20564;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int TIMAK_ORC_OVERLORD = 20588;
	private static final int TAMLIN_ORC = 20601;
	private static final int TAMLIN_ORC_ARCHER = 20602;
	private static final int LAKIN = 20604;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00222_TestOfTheDuelist()
	{
		super(222);
		addStartNpc(DUELIST_KAIEN);
		addTalkId(DUELIST_KAIEN);
		addKillId(PUNCHER, NOBLE_ANT_LEADER, DEAD_SEEKER, EXCURO, KRATOR, MARSH_STAKATO_DRONE, BREKA_ORC_OVERLORD, FETTERED_SOUL, GRANDIS, ENCHANTED_MONSTEREYE, LETO_LIZARDMAN_OVERLORD, TIMAK_ORC_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, LAKIN);
		registerQuestItems(ORDER_GLUDIO, ORDER_DION, ORDER_GIRAN, ORDER_OREN, ORDER_ADEN, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW, FINAL_ORDER, EXCUROS_SKIN, KRATORS_SHARD, GRANDIS_SKIN, TIMAK_ORCS_BELT, LAKINS_MACE);
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
					giveItems(player, ORDER_GLUDIO, 1);
					giveItems(player, ORDER_DION, 1);
					giveItems(player, ORDER_GIRAN, 1);
					giveItems(player, ORDER_OREN, 1);
					giveItems(player, ORDER_ADEN, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30623-04.htm":
			{
				if (player.getRace() != Race.ORC)
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30623-05.htm";
				}
				break;
			}
			case "30623-06.htm":
			case "30623-07.html":
			case "30623-09.html":
			case "30623-10.html":
			case "30623-11.html":
			case "30623-12.html":
			case "30623-15.html":
			{
				htmltext = event;
				break;
			}
			case "30623-08.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30623-16.html":
			{
				takeItems(player, PUNCHERS_SHARD, -1);
				takeItems(player, NOBLE_ANTS_FEELER, -1);
				takeItems(player, DEAD_SEEKER_FANG, -1);
				takeItems(player, DRONES_CHITIN, -1);
				takeItems(player, OVERLORD_NECKLACE, -1);
				takeItems(player, FETTERED_SOULS_CHAIN, -1);
				takeItems(player, CHIEDS_AMULET, -1);
				takeItems(player, ENCHANTED_EYE_MEAT, -1);
				takeItems(player, TAMRIN_ORCS_RING, -1);
				takeItems(player, TAMRIN_ORCS_ARROW, -1);
				takeItems(player, ORDER_GLUDIO, 1);
				takeItems(player, ORDER_DION, 1);
				takeItems(player, ORDER_GIRAN, 1);
				takeItems(player, ORDER_OREN, 1);
				takeItems(player, ORDER_ADEN, 1);
				giveItems(player, FINAL_ORDER, 1);
				qs.setMemoState(2);
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
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case PUNCHER:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_GLUDIO))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, PUNCHERS_SHARD, 1, 10, 1.0, true) && (getQuestItemsCount(killer, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case NOBLE_ANT_LEADER:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_GLUDIO))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, NOBLE_ANTS_FEELER, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case DEAD_SEEKER:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_DION))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, DEAD_SEEKER_FANG, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case EXCURO:
				{
					
					if (qs.isMemoState(2) && hasQuestItems(killer, FINAL_ORDER))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, EXCUROS_SKIN, 1, 3, 1.0, true) && (getQuestItemsCount(killer, KRATORS_SHARD, LAKINS_MACE, GRANDIS_SKIN, TIMAK_ORCS_BELT) == 12))
						{
							if (i0 >= 5)
							{
								qs.setCond(5);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case KRATOR:
				{
					if (qs.isMemoState(2) && hasQuestItems(killer, FINAL_ORDER))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, KRATORS_SHARD, 1, 3, 1.0, true) && (getQuestItemsCount(killer, EXCUROS_SKIN, LAKINS_MACE, GRANDIS_SKIN, TIMAK_ORCS_BELT) == 12))
						{
							if (i0 >= 5)
							{
								qs.setCond(5);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case MARSH_STAKATO_DRONE:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_DION))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, DRONES_CHITIN, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case BREKA_ORC_OVERLORD:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_GIRAN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, OVERLORD_NECKLACE, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case FETTERED_SOUL:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_GIRAN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, FETTERED_SOULS_CHAIN, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case GRANDIS:
				{
					if (qs.isMemoState(2) && hasQuestItems(killer, FINAL_ORDER))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, GRANDIS_SKIN, 1, 3, 1.0, true) && (getQuestItemsCount(killer, EXCUROS_SKIN, KRATORS_SHARD, LAKINS_MACE, TIMAK_ORCS_BELT) == 12))
						{
							if (i0 >= 5)
							{
								qs.setCond(5);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case ENCHANTED_MONSTEREYE:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_OREN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, ENCHANTED_EYE_MEAT, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_OREN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, CHIEDS_AMULET, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case TIMAK_ORC_OVERLORD:
				{
					if (qs.isMemoState(2) && hasQuestItems(killer, FINAL_ORDER))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, TIMAK_ORCS_BELT, 1, 3, 1.0, true) && (getQuestItemsCount(killer, EXCUROS_SKIN, KRATORS_SHARD, LAKINS_MACE, GRANDIS_SKIN) == 12))
						{
							if (i0 >= 5)
							{
								qs.setCond(5);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case TAMLIN_ORC:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_ADEN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, TAMRIN_ORCS_RING, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_ARROW) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case TAMLIN_ORC_ARCHER:
				{
					if (qs.isMemoState(1) && hasQuestItems(killer, ORDER_ADEN))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, TAMRIN_ORCS_ARROW, 1, 10, 1.0, true) && (getQuestItemsCount(killer, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING) == 90))
						{
							if (i0 >= 9)
							{
								qs.setCond(3);
							}
							qs.setMemoStateEx(1, 0);
						}
					}
					break;
				}
				case LAKIN:
				{
					if (qs.isMemoState(2) && hasQuestItems(killer, FINAL_ORDER))
					{
						final int i0 = qs.getMemoStateEx(1);
						qs.setMemoStateEx(1, i0 + 1);
						if (giveItemRandomly(killer, npc, LAKINS_MACE, 1, 3, 1.0, true) && (getQuestItemsCount(killer, EXCUROS_SKIN, KRATORS_SHARD, GRANDIS_SKIN, TIMAK_ORCS_BELT) == 12))
						{
							if (i0 >= 5)
							{
								qs.setCond(5);
							}
							qs.setMemoStateEx(1, 0);
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
			if ((player.getClassId() == ClassId.WARRIOR) || (player.getClassId() == ClassId.ELVEN_KNIGHT) || (player.getClassId() == ClassId.PALUS_KNIGHT) || (player.getClassId() == ClassId.ORC_MONK))
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					htmltext = "30623-03.htm";
				}
				else
				{
					htmltext = "30623-01.html";
				}
			}
			else
			{
				htmltext = "30623-02.html";
			}
		}
		else if (qs.isStarted())
		{
			if (hasQuestItems(player, ORDER_GLUDIO, ORDER_DION, ORDER_GIRAN, ORDER_OREN, ORDER_ADEN))
			{
				if (getQuestItemsCount(player, PUNCHERS_SHARD, NOBLE_ANTS_FEELER, DRONES_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOULS_CHAIN, CHIEDS_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORCS_RING, TAMRIN_ORCS_ARROW) == 100)
				{
					htmltext = "30623-13.html";
				}
				else
				{
					htmltext = "30623-14.html";
				}
			}
			else if (hasQuestItems(player, FINAL_ORDER))
			{
				if (getQuestItemsCount(player, EXCUROS_SKIN, KRATORS_SHARD, LAKINS_MACE, GRANDIS_SKIN, TIMAK_ORCS_BELT) == 15)
				{
					giveAdena(player, 161806, true);
					giveItems(player, MARK_OF_DUELIST, 1);
					addExpAndSp(player, 894888, 61408);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = "30623-18.html";
				}
				else
				{
					htmltext = "30623-17.html";
				}
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
}