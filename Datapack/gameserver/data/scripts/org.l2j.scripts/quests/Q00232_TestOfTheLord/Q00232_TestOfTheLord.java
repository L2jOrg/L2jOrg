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
package quests.Q00232_TestOfTheLord;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Test Of The Lord (232)
 * @author ivantotov
 */
public final class Q00232_TestOfTheLord extends Quest
{
	// NPCs
	private static final int SEER_SOMAK = 30510;
	private static final int SEER_MANAKIA = 30515;
	private static final int TRADER_JAKAL = 30558;
	private static final int BLACKSMITH_SUMARI = 30564;
	private static final int FLAME_LORD_KAKAI = 30565;
	private static final int ATUBA_CHIEF_VARKEES = 30566;
	private static final int NERUGA_CHIEF_TANTUS = 30567;
	private static final int URUTU_CHIEF_HATOS = 30568;
	private static final int DUDA_MARA_CHIEF_TAKUNA = 30641;
	private static final int GANDI_CHIEF_CHIANTA = 30642;
	private static final int FIRST_ORC = 30643;
	private static final int ANCESTOR_MARTANKUS = 30649;
	// Items
	private static final int ADENA = 57;
	private static final int BONE_ARROW = 1341;
	private static final int ORDEAL_NECKLACE = 3391;
	private static final int VARKEES_CHARM = 3392;
	private static final int TANTUS_CHARM = 3393;
	private static final int HATOS_CHARM = 3394;
	private static final int TAKUNA_CHARM = 3395;
	private static final int CHIANTA_CHARM = 3396;
	private static final int MANAKIAS_ORDERS = 3397;
	private static final int BREKA_ORC_FANG = 3398;
	private static final int MANAKIAS_AMULET = 3399;
	private static final int HUGE_ORC_FANG = 3400;
	private static final int SUMARIS_LETTER = 3401;
	private static final int URUTU_BLADE = 3402;
	private static final int TIMAK_ORC_SKULL = 3403;
	private static final int SWORD_INTO_SKULL = 3404;
	private static final int NERUGA_AXE_BLADE = 3405;
	private static final int AXE_OF_CEREMONY = 3406;
	private static final int MARSH_SPIDER_FEELER = 3407;
	private static final int MARSH_SPIDER_FEET = 3408;
	private static final int HANDIWORK_SPIDER_BROOCH = 3409;
	private static final int ENCHANTED_MONSTER_CORNEA = 3410;
	private static final int MONSTER_EYE_WOODCARVING = 3411;
	private static final int BEAR_FANG_NECKLACE = 3412;
	private static final int MARTANKUS_CHARM = 3413;
	private static final int RAGNA_ORC_HEAD = 3414;
	private static final int RAGNA_CHIEF_NOTICE = 3415;
	private static final int IMMORTAL_FLAME = 3416;
	// Reward
	private static final int MARK_OF_LORD = 3390;
	// Monster
	private static final int MARSH_SPIDER = 20233;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int ENCHANTED_MONSTEREYE = 20564;
	private static final int TIMAK_ORC = 20583;
	private static final int TIMAK_ORC_ARCHER = 20584;
	private static final int TIMAK_ORC_SOLDIER = 20585;
	private static final int TIMAK_ORC_WARRIOR = 20586;
	private static final int TIMAK_ORC_SHAMAN = 20587;
	private static final int TIMAK_ORC_OVERLORD = 20588;
	private static final int RAGNA_ORC_OVERLORD = 20778;
	private static final int RAGNA_ORC_SEER = 20779;
	// Misc
	private static final int MIN_LEVEL = 39;
	// Locations
	private static final Location FIRST_ORC_SPAWN = new Location(21036, -107690, -3038);
	
	public Q00232_TestOfTheLord()
	{
		super(232);
		addStartNpc(FLAME_LORD_KAKAI);
		addTalkId(FLAME_LORD_KAKAI, SEER_SOMAK, SEER_MANAKIA, TRADER_JAKAL, BLACKSMITH_SUMARI, ATUBA_CHIEF_VARKEES, NERUGA_CHIEF_TANTUS, URUTU_CHIEF_HATOS, DUDA_MARA_CHIEF_TAKUNA, GANDI_CHIEF_CHIANTA, FIRST_ORC, ANCESTOR_MARTANKUS);
		addKillId(MARSH_SPIDER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, ENCHANTED_MONSTEREYE, TIMAK_ORC, TIMAK_ORC_ARCHER, TIMAK_ORC_SOLDIER, TIMAK_ORC_SOLDIER, TIMAK_ORC_WARRIOR, TIMAK_ORC_SHAMAN, TIMAK_ORC_OVERLORD, RAGNA_ORC_OVERLORD, RAGNA_ORC_SEER);
		registerQuestItems(ORDEAL_NECKLACE, VARKEES_CHARM, TANTUS_CHARM, HATOS_CHARM, TAKUNA_CHARM, CHIANTA_CHARM, MANAKIAS_ORDERS, BREKA_ORC_FANG, MANAKIAS_AMULET, HUGE_ORC_FANG, SUMARIS_LETTER, URUTU_BLADE, TIMAK_ORC_SKULL, SWORD_INTO_SKULL, NERUGA_AXE_BLADE, AXE_OF_CEREMONY, MARSH_SPIDER_FEELER, MARSH_SPIDER_FEET, HANDIWORK_SPIDER_BROOCH, ENCHANTED_MONSTER_CORNEA, MONSTER_EYE_WOODCARVING, BEAR_FANG_NECKLACE, MARTANKUS_CHARM, RAGNA_ORC_HEAD, RAGNA_CHIEF_NOTICE, IMMORTAL_FLAME);
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
					giveItems(player, ORDEAL_NECKLACE, 1);
				}
				break;
			}
			case "30565-05a.html":
			case "30558-03a.html":
			case "30643-02.html":
			case "30643-03.html":
			case "30649-02.html":
			case "30649-03.html":
			{
				htmltext = event;
				break;
			}
			case "30565-08.html":
			{
				if (hasQuestItems(player, HUGE_ORC_FANG))
				{
					takeItems(player, ORDEAL_NECKLACE, 1);
					takeItems(player, HUGE_ORC_FANG, 1);
					takeItems(player, SWORD_INTO_SKULL, 1);
					takeItems(player, AXE_OF_CEREMONY, 1);
					takeItems(player, HANDIWORK_SPIDER_BROOCH, 1);
					takeItems(player, MONSTER_EYE_WOODCARVING, 1);
					giveItems(player, BEAR_FANG_NECKLACE, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30558-02.html":
			{
				if (getQuestItemsCount(player, ADENA) >= 1000)
				{
					takeItems(player, ADENA, 1000);
					giveItems(player, NERUGA_AXE_BLADE, 1);
					htmltext = event;
				}
				break;
			}
			case "30566-02.html":
			{
				giveItems(player, VARKEES_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30567-02.html":
			{
				giveItems(player, TANTUS_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30568-02.html":
			{
				giveItems(player, HATOS_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30641-02.html":
			{
				giveItems(player, TAKUNA_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30642-02.html":
			{
				giveItems(player, CHIANTA_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30649-04.html":
			{
				if (hasQuestItems(player, BEAR_FANG_NECKLACE))
				{
					takeItems(player, BEAR_FANG_NECKLACE, 1);
					giveItems(player, MARTANKUS_CHARM, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30649-07.html":
			{
				if (npc.getSummonedNpcCount() < 1)
				{
					addSpawn(npc, FIRST_ORC, FIRST_ORC_SPAWN, false, 10000);
				}
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
				case MARSH_SPIDER:
				{
					if (hasQuestItems(killer, ORDEAL_NECKLACE, TAKUNA_CHARM) && !hasQuestItems(killer, HANDIWORK_SPIDER_BROOCH))
					{
						if (getQuestItemsCount(killer, MARSH_SPIDER_FEELER) < 10)
						{
							giveItems(killer, MARSH_SPIDER_FEELER, 2);
							if (getQuestItemsCount(killer, MARSH_SPIDER_FEELER) >= 10)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else if (getQuestItemsCount(killer, MARSH_SPIDER_FEET) < 10)
						{
							giveItems(killer, MARSH_SPIDER_FEET, 2);
							if (getQuestItemsCount(killer, MARSH_SPIDER_FEET) >= 10)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case BREKA_ORC_SHAMAN:
				case BREKA_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, ORDEAL_NECKLACE, VARKEES_CHARM, MANAKIAS_ORDERS) && !hasAtLeastOneQuestItem(killer, HUGE_ORC_FANG, MANAKIAS_AMULET))
					{
						if (getQuestItemsCount(killer, BREKA_ORC_FANG) < 20)
						{
							giveItems(killer, BREKA_ORC_FANG, 2);
							if (getQuestItemsCount(killer, BREKA_ORC_FANG) >= 20)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case ENCHANTED_MONSTEREYE:
				{
					if (hasQuestItems(killer, ORDEAL_NECKLACE, CHIANTA_CHARM) && !hasQuestItems(killer, MONSTER_EYE_WOODCARVING))
					{
						if (getQuestItemsCount(killer, ENCHANTED_MONSTER_CORNEA) < 20)
						{
							giveItems(killer, ENCHANTED_MONSTER_CORNEA, 1);
							if (getQuestItemsCount(killer, ENCHANTED_MONSTER_CORNEA) >= 20)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case TIMAK_ORC:
				case TIMAK_ORC_ARCHER:
				case TIMAK_ORC_SOLDIER:
				case TIMAK_ORC_WARRIOR:
				case TIMAK_ORC_SHAMAN:
				case TIMAK_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, ORDEAL_NECKLACE, HATOS_CHARM) && !hasQuestItems(killer, SWORD_INTO_SKULL))
					{
						if (getQuestItemsCount(killer, TIMAK_ORC_SKULL) < 10)
						{
							giveItems(killer, TIMAK_ORC_SKULL, 1);
							if (getQuestItemsCount(killer, TIMAK_ORC_SKULL) >= 10)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case RAGNA_ORC_OVERLORD:
				case RAGNA_ORC_SEER:
				{
					if (hasQuestItems(killer, MARTANKUS_CHARM))
					{
						if (!hasQuestItems(killer, RAGNA_CHIEF_NOTICE))
						{
							giveItems(killer, RAGNA_CHIEF_NOTICE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, RAGNA_ORC_HEAD))
						{
							giveItems(killer, RAGNA_ORC_HEAD, 1);
							qs.setCond(5, true);
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
			if (npc.getId() == FLAME_LORD_KAKAI)
			{
				if (player.getRace() != Race.ORC)
				{
					htmltext = "30565-01.html";
				}
				else if (player.getClassId() != ClassId.ORC_SHAMAN)
				{
					htmltext = "30565-02.html";
				}
				else if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30565-03.html";
				}
				else
				{
					htmltext = "30565-04.htm";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case FLAME_LORD_KAKAI:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE))
					{
						if (hasQuestItems(player, HUGE_ORC_FANG, SWORD_INTO_SKULL, AXE_OF_CEREMONY, MONSTER_EYE_WOODCARVING, HANDIWORK_SPIDER_BROOCH))
						{
							htmltext = "30565-07.html";
						}
						else
						{
							htmltext = "30565-06.html";
						}
					}
					else if (hasQuestItems(player, BEAR_FANG_NECKLACE))
					{
						htmltext = "30565-09.html";
					}
					else if (hasQuestItems(player, MARTANKUS_CHARM))
					{
						htmltext = "30565-10.html";
					}
					else if (hasQuestItems(player, IMMORTAL_FLAME))
					{
						giveAdena(player, 161806, true);
						giveItems(player, MARK_OF_LORD, 1);
						addExpAndSp(player, 894888, 61408);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30565-11.html";
					}
					break;
				}
				case SEER_SOMAK:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE, HATOS_CHARM, SUMARIS_LETTER) && !hasAtLeastOneQuestItem(player, SWORD_INTO_SKULL, URUTU_BLADE))
					{
						takeItems(player, SUMARIS_LETTER, 1);
						giveItems(player, URUTU_BLADE, 1);
						htmltext = "30510-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HATOS_CHARM, URUTU_BLADE) && !hasAtLeastOneQuestItem(player, SWORD_INTO_SKULL, SUMARIS_LETTER))
					{
						htmltext = "30510-02.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, SWORD_INTO_SKULL) && !hasAtLeastOneQuestItem(player, HATOS_CHARM, URUTU_BLADE, SUMARIS_LETTER))
					{
						htmltext = "30510-03.html";
					}
					break;
				}
				case SEER_MANAKIA:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE, VARKEES_CHARM) && !hasAtLeastOneQuestItem(player, HUGE_ORC_FANG, MANAKIAS_AMULET, MANAKIAS_ORDERS))
					{
						giveItems(player, MANAKIAS_ORDERS, 1);
						htmltext = "30515-01.html";
					}
					else if (hasQuestItems(player, VARKEES_CHARM, ORDEAL_NECKLACE, MANAKIAS_ORDERS) && !hasAtLeastOneQuestItem(player, HUGE_ORC_FANG, MANAKIAS_AMULET))
					{
						if (getQuestItemsCount(player, BREKA_ORC_FANG) < 20)
						{
							htmltext = "30515-02.html";
						}
						else
						{
							takeItems(player, MANAKIAS_ORDERS, 1);
							takeItems(player, BREKA_ORC_FANG, -1);
							giveItems(player, MANAKIAS_AMULET, 1);
							htmltext = "30515-03.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, VARKEES_CHARM, MANAKIAS_AMULET) && !hasAtLeastOneQuestItem(player, HUGE_ORC_FANG, MANAKIAS_ORDERS))
					{
						htmltext = "30515-04.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HUGE_ORC_FANG) && !hasAtLeastOneQuestItem(player, VARKEES_CHARM, MANAKIAS_AMULET, MANAKIAS_ORDERS))
					{
						htmltext = "30515-05.html";
					}
					break;
				}
				case TRADER_JAKAL:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE, TANTUS_CHARM) && !hasAtLeastOneQuestItem(player, AXE_OF_CEREMONY, NERUGA_AXE_BLADE))
					{
						if (getQuestItemsCount(player, ADENA) >= 1000)
						{
							htmltext = "30558-01.html";
						}
						else
						{
							htmltext = "30558-03.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, TANTUS_CHARM, NERUGA_AXE_BLADE) && !hasQuestItems(player, AXE_OF_CEREMONY))
					{
						htmltext = "30558-04.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, AXE_OF_CEREMONY) && !hasQuestItems(player, TANTUS_CHARM))
					{
						htmltext = "30558-05.html";
					}
					break;
				}
				case BLACKSMITH_SUMARI:
				{
					if (hasQuestItems(player, HATOS_CHARM, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, SWORD_INTO_SKULL, URUTU_BLADE, SUMARIS_LETTER))
					{
						giveItems(player, SUMARIS_LETTER, 1);
						htmltext = "30564-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HATOS_CHARM, SUMARIS_LETTER) && !hasAtLeastOneQuestItem(player, SWORD_INTO_SKULL, URUTU_BLADE))
					{
						htmltext = "30564-02.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HATOS_CHARM, URUTU_BLADE) && !hasAtLeastOneQuestItem(player, SUMARIS_LETTER, SWORD_INTO_SKULL))
					{
						htmltext = "30564-03.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, SWORD_INTO_SKULL) && !hasAtLeastOneQuestItem(player, HATOS_CHARM, URUTU_BLADE, SUMARIS_LETTER))
					{
						htmltext = "30564-04.html";
					}
					break;
				}
				case ATUBA_CHIEF_VARKEES:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, HUGE_ORC_FANG, VARKEES_CHARM))
					{
						htmltext = "30566-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, VARKEES_CHARM) && !hasAtLeastOneQuestItem(player, HUGE_ORC_FANG, MANAKIAS_AMULET))
					{
						htmltext = "30566-03.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, VARKEES_CHARM, MANAKIAS_AMULET) && !hasQuestItems(player, HUGE_ORC_FANG))
					{
						takeItems(player, VARKEES_CHARM, 1);
						takeItems(player, MANAKIAS_AMULET, 1);
						giveItems(player, HUGE_ORC_FANG, 1);
						if (hasQuestItems(player, AXE_OF_CEREMONY, SWORD_INTO_SKULL, HANDIWORK_SPIDER_BROOCH, MONSTER_EYE_WOODCARVING))
						{
							qs.setCond(2, true);
						}
						htmltext = "30566-04.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HUGE_ORC_FANG) && !hasQuestItems(player, VARKEES_CHARM))
					{
						htmltext = "30566-05.html";
					}
					break;
				}
				case NERUGA_CHIEF_TANTUS:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, AXE_OF_CEREMONY, TANTUS_CHARM))
					{
						htmltext = "30567-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, TANTUS_CHARM) && !hasQuestItems(player, AXE_OF_CEREMONY))
					{
						if (!hasQuestItems(player, NERUGA_AXE_BLADE) || (getQuestItemsCount(player, BONE_ARROW) < 1000))
						{
							htmltext = "30567-03.html";
						}
						else
						{
							takeItems(player, BONE_ARROW, 1000);
							takeItems(player, TANTUS_CHARM, 1);
							takeItems(player, NERUGA_AXE_BLADE, 1);
							giveItems(player, AXE_OF_CEREMONY, 1);
							if (hasQuestItems(player, HUGE_ORC_FANG, SWORD_INTO_SKULL, HANDIWORK_SPIDER_BROOCH, MONSTER_EYE_WOODCARVING))
							{
								qs.setCond(2, true);
							}
							htmltext = "30567-04.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, AXE_OF_CEREMONY) && !hasQuestItems(player, TANTUS_CHARM))
					{
						htmltext = "30567-05.html";
					}
					break;
				}
				case URUTU_CHIEF_HATOS:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, SWORD_INTO_SKULL, HATOS_CHARM))
					{
						htmltext = "30568-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HATOS_CHARM) && !hasQuestItems(player, SWORD_INTO_SKULL))
					{
						if (hasQuestItems(player, URUTU_BLADE) && (getQuestItemsCount(player, TIMAK_ORC_SKULL) >= 10))
						{
							takeItems(player, HATOS_CHARM, 1);
							takeItems(player, URUTU_BLADE, 1);
							takeItems(player, TIMAK_ORC_SKULL, -1);
							giveItems(player, SWORD_INTO_SKULL, 1);
							if (hasQuestItems(player, HUGE_ORC_FANG, AXE_OF_CEREMONY, HANDIWORK_SPIDER_BROOCH, MONSTER_EYE_WOODCARVING))
							{
								qs.setCond(2, true);
							}
							htmltext = "30568-04.html";
						}
						else
						{
							htmltext = "30568-03.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, SWORD_INTO_SKULL) && !hasQuestItems(player, HATOS_CHARM))
					{
						htmltext = "30568-05.html";
					}
					break;
				}
				case DUDA_MARA_CHIEF_TAKUNA:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, HANDIWORK_SPIDER_BROOCH, TAKUNA_CHARM))
					{
						htmltext = "30641-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, TAKUNA_CHARM) && !hasQuestItems(player, HANDIWORK_SPIDER_BROOCH))
					{
						if ((getQuestItemsCount(player, MARSH_SPIDER_FEELER) >= 10) && (getQuestItemsCount(player, MARSH_SPIDER_FEET) >= 10))
						{
							takeItems(player, TAKUNA_CHARM, 1);
							takeItems(player, MARSH_SPIDER_FEELER, -1);
							takeItems(player, MARSH_SPIDER_FEET, -1);
							giveItems(player, HANDIWORK_SPIDER_BROOCH, 1);
							if (hasQuestItems(player, HUGE_ORC_FANG, AXE_OF_CEREMONY, SWORD_INTO_SKULL, MONSTER_EYE_WOODCARVING))
							{
								qs.setCond(2, true);
							}
							htmltext = "30641-04.html";
						}
						else
						{
							htmltext = "30641-03.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, HANDIWORK_SPIDER_BROOCH) && !hasQuestItems(player, TAKUNA_CHARM))
					{
						htmltext = "30641-05.html";
					}
					break;
				}
				case GANDI_CHIEF_CHIANTA:
				{
					if (hasQuestItems(player, ORDEAL_NECKLACE) && !hasAtLeastOneQuestItem(player, MONSTER_EYE_WOODCARVING, CHIANTA_CHARM))
					{
						htmltext = "30642-01.html";
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, CHIANTA_CHARM) && !hasQuestItems(player, MONSTER_EYE_WOODCARVING))
					{
						if (getQuestItemsCount(player, ENCHANTED_MONSTER_CORNEA) < 20)
						{
							htmltext = "30642-03.html";
						}
						else
						{
							takeItems(player, CHIANTA_CHARM, 1);
							takeItems(player, ENCHANTED_MONSTER_CORNEA, -1);
							giveItems(player, MONSTER_EYE_WOODCARVING, 1);
							if (hasQuestItems(player, HUGE_ORC_FANG, AXE_OF_CEREMONY, SWORD_INTO_SKULL, HANDIWORK_SPIDER_BROOCH))
							{
								qs.setCond(2, true);
							}
							htmltext = "30642-04.html";
						}
					}
					else if (hasQuestItems(player, ORDEAL_NECKLACE, MONSTER_EYE_WOODCARVING) && !hasQuestItems(player, CHIANTA_CHARM))
					{
						htmltext = "30642-05.html";
					}
					break;
				}
				case FIRST_ORC:
				{
					if (hasAtLeastOneQuestItem(player, MARTANKUS_CHARM, IMMORTAL_FLAME))
					{
						qs.setCond(7, true);
						htmltext = "30643-01.html";
					}
					break;
				}
				case ANCESTOR_MARTANKUS:
				{
					if (hasQuestItems(player, BEAR_FANG_NECKLACE))
					{
						htmltext = "30649-01.html";
					}
					else if (hasQuestItems(player, MARTANKUS_CHARM) && !hasAtLeastOneQuestItem(player, RAGNA_CHIEF_NOTICE, RAGNA_ORC_HEAD))
					{
						htmltext = "30649-05.html";
					}
					else if (hasQuestItems(player, MARTANKUS_CHARM, RAGNA_CHIEF_NOTICE, RAGNA_ORC_HEAD))
					{
						takeItems(player, MARTANKUS_CHARM, 1);
						takeItems(player, RAGNA_ORC_HEAD, 1);
						takeItems(player, RAGNA_CHIEF_NOTICE, 1);
						giveItems(player, IMMORTAL_FLAME, 1);
						qs.setCond(6, true);
						htmltext = "30649-06.html";
					}
					else if (hasQuestItems(player, IMMORTAL_FLAME))
					{
						if (npc.getSummonedNpcCount() < 1)
						{
							addSpawn(npc, FIRST_ORC, FIRST_ORC_SPAWN, false, 10000);
						}
						htmltext = "30649-08.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == FLAME_LORD_KAKAI)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}