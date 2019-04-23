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
package quests.Q00228_TestOfMagus;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Test Of Magus (228)
 * @author ivantotov
 */
public final class Q00228_TestOfMagus extends Quest
{
	// NPCs
	private static final int PARINA = 30391;
	private static final int EARTH_SNAKE = 30409;
	private static final int FLAME_SALAMANDER = 30411;
	private static final int WIND_SYLPH = 30412;
	private static final int WATER_UNDINE = 30413;
	private static final int ELDER_CASIAN = 30612;
	private static final int BARD_RUKAL = 30629;
	// Items
	private static final int RUKALS_LETTER = 2841;
	private static final int PARINAS_LETTER = 2842;
	private static final int LILAC_CHARM = 2843;
	private static final int GOLDEN_SEED_1ST = 2844;
	private static final int GOLDEN_SEED_2ND = 2845;
	private static final int GOLDEN_SEED_3RD = 2846;
	private static final int SCORE_OF_ELEMENTS = 2847;
	private static final int DAZZLING_DROP = 2848;
	private static final int FLAME_CRYSTAL = 2849;
	private static final int HARPYS_FEATHER = 2850;
	private static final int WYRMS_WINGBONE = 2851;
	private static final int WINDSUS_MANE = 2852;
	private static final int ENCHANTED_MONSTER_EYE_SHELL = 2853;
	private static final int ENCHANTED_GOLEM_POWDER = 2854;
	private static final int ENCHANTED_IRON_GOLEM_SCRAP = 2855;
	private static final int TONE_OF_WATER = 2856;
	private static final int TONE_OF_FIRE = 2857;
	private static final int TONE_OF_WIND = 2858;
	private static final int TONE_OF_EARTH = 2859;
	private static final int SALAMANDER_CHARM = 2860;
	private static final int SYLPH_CHARM = 2861;
	private static final int UNDINE_CHARM = 2862;
	private static final int SERPENT_CHARM = 2863;
	// Reward
	private static final int MARK_OF_MAGUS = 2840;
	// Monster
	private static final int HARPY = 20145;
	private static final int MARSH_STAKATO = 20157;
	private static final int WYRM = 20176;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int TOAD_LORD = 20231;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int WINDSUS = 20553;
	private static final int ENCHANTED_MONSTEREYE = 20564;
	private static final int ENCHANTED_STOLEN_GOLEM = 20565;
	private static final int ENCHANTED_IRON_GOLEM = 20566;
	// Quest Monster
	private static final int SINGING_FLOWER_PHANTASM = 27095;
	private static final int SINGING_FLOWER_NIGTMATE = 27096;
	private static final int SINGING_FLOWER_DARKLING = 27097;
	private static final int GHOST_FIRE = 27098;
	// Misc
	private static final int MIN_LVL = 39;
	
	public Q00228_TestOfMagus()
	{
		super(228);
		addStartNpc(BARD_RUKAL);
		addTalkId(BARD_RUKAL, PARINA, EARTH_SNAKE, FLAME_SALAMANDER, WIND_SYLPH, WATER_UNDINE, ELDER_CASIAN);
		addKillId(HARPY, MARSH_STAKATO, WYRM, MARSH_STAKATO_WORKER, TOAD_LORD, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, WINDSUS, ENCHANTED_MONSTEREYE, ENCHANTED_STOLEN_GOLEM, ENCHANTED_IRON_GOLEM, SINGING_FLOWER_PHANTASM, SINGING_FLOWER_NIGTMATE, SINGING_FLOWER_DARKLING, GHOST_FIRE);
		registerQuestItems(RUKALS_LETTER, PARINAS_LETTER, LILAC_CHARM, GOLDEN_SEED_1ST, GOLDEN_SEED_2ND, GOLDEN_SEED_3RD, SCORE_OF_ELEMENTS, DAZZLING_DROP, FLAME_CRYSTAL, HARPYS_FEATHER, WYRMS_WINGBONE, WINDSUS_MANE, ENCHANTED_MONSTER_EYE_SHELL, ENCHANTED_GOLEM_POWDER, ENCHANTED_IRON_GOLEM_SCRAP, TONE_OF_WATER, TONE_OF_FIRE, TONE_OF_WIND, TONE_OF_EARTH, SALAMANDER_CHARM, SYLPH_CHARM, UNDINE_CHARM, SERPENT_CHARM);
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
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, RUKALS_LETTER, 1);
				}
				break;
			}
			case "30629-09.html":
			case "30409-02.html":
			{
				htmltext = event;
				break;
			}
			case "30629-10.html":
			{
				if (hasQuestItems(player, GOLDEN_SEED_3RD))
				{
					takeItems(player, LILAC_CHARM, 1);
					takeItems(player, GOLDEN_SEED_1ST, 1);
					takeItems(player, GOLDEN_SEED_2ND, 1);
					takeItems(player, GOLDEN_SEED_3RD, 1);
					giveItems(player, SCORE_OF_ELEMENTS, 1);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30391-02.html":
			{
				if (hasQuestItems(player, RUKALS_LETTER))
				{
					takeItems(player, RUKALS_LETTER, 1);
					giveItems(player, PARINAS_LETTER, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30409-03.html":
			{
				giveItems(player, SERPENT_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30412-02.html":
			{
				giveItems(player, SYLPH_CHARM, 1);
				htmltext = event;
				break;
			}
			case "30612-02.html":
			{
				takeItems(player, PARINAS_LETTER, 1);
				giveItems(player, LILAC_CHARM, 1);
				qs.setCond(3, true);
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
				case HARPY:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SYLPH_CHARM) && (getQuestItemsCount(killer, HARPYS_FEATHER) < 20))
					{
						giveItems(killer, HARPYS_FEATHER, 1);
						if (getQuestItemsCount(killer, HARPYS_FEATHER) >= 20)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_STAKATO:
				case MARSH_STAKATO_WORKER:
				case TOAD_LORD:
				case MARSH_STAKATO_SOLDIER:
				case MARSH_STAKATO_DRONE:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, UNDINE_CHARM) && (getQuestItemsCount(killer, DAZZLING_DROP) < 20))
					{
						giveItems(killer, DAZZLING_DROP, 1);
						if (getQuestItemsCount(killer, DAZZLING_DROP) >= 20)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case WYRM:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SYLPH_CHARM) && (getQuestItemsCount(killer, WYRMS_WINGBONE) < 10))
					{
						if (getRandom(100) < 50)
						{
							giveItems(killer, WYRMS_WINGBONE, 1);
							if (getQuestItemsCount(killer, WYRMS_WINGBONE) >= 10)
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
				case WINDSUS:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SYLPH_CHARM) && (getQuestItemsCount(killer, WINDSUS_MANE) < 10))
					{
						if (getRandom(100) < 50)
						{
							giveItems(killer, WINDSUS_MANE, 1);
							if (getQuestItemsCount(killer, WINDSUS_MANE) >= 10)
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
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SERPENT_CHARM) && (getQuestItemsCount(killer, ENCHANTED_MONSTER_EYE_SHELL) < 10))
					{
						giveItems(killer, ENCHANTED_MONSTER_EYE_SHELL, 1);
						if (getQuestItemsCount(killer, ENCHANTED_MONSTER_EYE_SHELL) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ENCHANTED_STOLEN_GOLEM:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SERPENT_CHARM) && (getQuestItemsCount(killer, ENCHANTED_GOLEM_POWDER) < 10))
					{
						giveItems(killer, ENCHANTED_GOLEM_POWDER, 1);
						if (getQuestItemsCount(killer, ENCHANTED_GOLEM_POWDER) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ENCHANTED_IRON_GOLEM:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SERPENT_CHARM) && (getQuestItemsCount(killer, ENCHANTED_IRON_GOLEM_SCRAP) < 10))
					{
						giveItems(killer, ENCHANTED_IRON_GOLEM_SCRAP, 1);
						if (getQuestItemsCount(killer, ENCHANTED_IRON_GOLEM_SCRAP) >= 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case SINGING_FLOWER_PHANTASM:
				{
					if (hasQuestItems(killer, LILAC_CHARM) && !hasQuestItems(killer, GOLDEN_SEED_1ST))
					{
						giveItems(killer, GOLDEN_SEED_1ST, 1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_AM_A_TREE_OF_NOTHING_A_TREE_THAT_KNOWS_WHERE_TO_RETURN));
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, GOLDEN_SEED_2ND, GOLDEN_SEED_3RD))
						{
							qs.setCond(4);
						}
					}
					break;
				}
				case SINGING_FLOWER_NIGTMATE:
				{
					if (hasQuestItems(killer, LILAC_CHARM) && !hasQuestItems(killer, GOLDEN_SEED_2ND))
					{
						giveItems(killer, GOLDEN_SEED_2ND, 1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_AM_A_CREATURE_THAT_SHOWS_THE_TRUTH_OF_THE_PLACE_DEEP_IN_MY_HEART));
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, GOLDEN_SEED_1ST, GOLDEN_SEED_3RD))
						{
							qs.setCond(4);
						}
					}
					break;
				}
				case SINGING_FLOWER_DARKLING:
				{
					if (hasQuestItems(killer, LILAC_CHARM) && !hasQuestItems(killer, GOLDEN_SEED_3RD))
					{
						giveItems(killer, GOLDEN_SEED_3RD, 1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_AM_A_MIRROR_OF_DARKNESS_AN_ILLUSION_OF_DARKNESS));
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (hasQuestItems(killer, GOLDEN_SEED_1ST, GOLDEN_SEED_2ND))
						{
							qs.setCond(4);
						}
					}
					break;
				}
				case GHOST_FIRE:
				{
					if (hasQuestItems(killer, SCORE_OF_ELEMENTS, SALAMANDER_CHARM) && (getQuestItemsCount(killer, FLAME_CRYSTAL) < 5))
					{
						if (getRandom(100) < 50)
						{
							giveItems(killer, FLAME_CRYSTAL, 1);
							if (getQuestItemsCount(killer, FLAME_CRYSTAL) >= 5)
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
			if (npc.getId() == BARD_RUKAL)
			{
				if ((player.getClassId() == ClassId.WIZARD) || (player.getClassId() == ClassId.ELVEN_WIZARD) || ((player.getClassId() == ClassId.DARK_WIZARD)))
				{
					if (player.getLevel() < MIN_LVL)
					{
						htmltext = "30629-02.html";
					}
					else
					{
						htmltext = "30629-03.htm";
					}
				}
				else
				{
					htmltext = "30629-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case BARD_RUKAL:
				{
					if (hasQuestItems(player, RUKALS_LETTER))
					{
						htmltext = "30629-05.html";
					}
					else if (hasQuestItems(player, PARINAS_LETTER))
					{
						htmltext = "30629-06.html";
					}
					else if (hasQuestItems(player, LILAC_CHARM))
					{
						if (hasQuestItems(player, GOLDEN_SEED_1ST, GOLDEN_SEED_2ND, GOLDEN_SEED_3RD))
						{
							htmltext = "30629-08.html";
						}
						else
						{
							htmltext = "30629-07.html";
						}
					}
					else if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						if (hasQuestItems(player, TONE_OF_WATER, TONE_OF_FIRE, TONE_OF_WIND, TONE_OF_EARTH))
						{
							giveAdena(player, 372154, true);
							giveItems(player, MARK_OF_MAGUS, 1);
							addExpAndSp(player, 2058244, 141240);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30629-12.html";
						}
						else
						{
							htmltext = "30629-11.html";
						}
					}
					break;
				}
				case PARINA:
				{
					if (hasQuestItems(player, RUKALS_LETTER))
					{
						htmltext = "30391-01.html";
					}
					else if (hasQuestItems(player, PARINAS_LETTER))
					{
						htmltext = "30391-03.html";
					}
					else if (hasQuestItems(player, LILAC_CHARM))
					{
						htmltext = "30391-04.html";
					}
					else if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						htmltext = "30391-05.html";
					}
					break;
				}
				case EARTH_SNAKE:
				{
					if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						if (!hasAtLeastOneQuestItem(player, TONE_OF_EARTH, SERPENT_CHARM))
						{
							htmltext = "30409-01.html";
						}
						else if (hasQuestItems(player, SERPENT_CHARM))
						{
							if ((getQuestItemsCount(player, ENCHANTED_MONSTER_EYE_SHELL) >= 10) && (getQuestItemsCount(player, ENCHANTED_GOLEM_POWDER) >= 10) && (getQuestItemsCount(player, ENCHANTED_IRON_GOLEM_SCRAP) >= 10))
							{
								takeItems(player, ENCHANTED_MONSTER_EYE_SHELL, -1);
								takeItems(player, ENCHANTED_GOLEM_POWDER, -1);
								takeItems(player, ENCHANTED_IRON_GOLEM_SCRAP, -1);
								giveItems(player, TONE_OF_EARTH, 1);
								takeItems(player, SERPENT_CHARM, 1);
								if (hasQuestItems(player, TONE_OF_FIRE, TONE_OF_WATER, TONE_OF_WIND))
								{
									qs.setCond(6, true);
								}
								htmltext = "30409-05.html";
							}
							else
							{
								htmltext = "30409-04.html";
							}
						}
						else if (hasQuestItems(player, TONE_OF_EARTH) && !hasQuestItems(player, SERPENT_CHARM))
						{
							htmltext = "30409-06.html";
						}
					}
					break;
				}
				case FLAME_SALAMANDER:
				{
					if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						if (!hasAtLeastOneQuestItem(player, TONE_OF_FIRE, SALAMANDER_CHARM))
						{
							htmltext = "30411-01.html";
							giveItems(player, SALAMANDER_CHARM, 1);
						}
						else if (hasQuestItems(player, SALAMANDER_CHARM))
						{
							if (getQuestItemsCount(player, FLAME_CRYSTAL) < 5)
							{
								htmltext = "30411-02.html";
							}
							else
							{
								takeItems(player, FLAME_CRYSTAL, -1);
								giveItems(player, TONE_OF_FIRE, 1);
								takeItems(player, SALAMANDER_CHARM, 1);
								if (hasQuestItems(player, TONE_OF_WATER, TONE_OF_WIND, TONE_OF_EARTH))
								{
									qs.setCond(6, true);
								}
								htmltext = "30411-03.html";
							}
						}
						else if (hasQuestItems(player, TONE_OF_FIRE) && !hasQuestItems(player, SALAMANDER_CHARM))
						{
							htmltext = "30411-04.html";
						}
					}
					break;
				}
				case WIND_SYLPH:
				{
					if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						if (!hasAtLeastOneQuestItem(player, TONE_OF_WIND, SYLPH_CHARM))
						{
							htmltext = "30412-01.html";
						}
						else if (hasQuestItems(player, SYLPH_CHARM))
						{
							if ((getQuestItemsCount(player, HARPYS_FEATHER) >= 20) && (getQuestItemsCount(player, WYRMS_WINGBONE) >= 10) && (getQuestItemsCount(player, WINDSUS_MANE) >= 10))
							{
								takeItems(player, HARPYS_FEATHER, -1);
								takeItems(player, WYRMS_WINGBONE, -1);
								takeItems(player, WINDSUS_MANE, -1);
								giveItems(player, TONE_OF_WIND, 1);
								takeItems(player, SYLPH_CHARM, 1);
								if (hasQuestItems(player, TONE_OF_WATER, TONE_OF_FIRE, TONE_OF_EARTH))
								{
									qs.setCond(6, true);
								}
								htmltext = "30412-04.html";
							}
							else
							{
								htmltext = "30412-03.html";
							}
						}
						else if (hasQuestItems(player, TONE_OF_WIND) && !hasQuestItems(player, SYLPH_CHARM))
						{
							htmltext = "30412-05.html";
						}
					}
					break;
				}
				case WATER_UNDINE:
				{
					if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						if (!hasAtLeastOneQuestItem(player, TONE_OF_WATER, UNDINE_CHARM))
						{
							htmltext = "30413-01.html";
							giveItems(player, UNDINE_CHARM, 1);
						}
						else if (hasQuestItems(player, UNDINE_CHARM))
						{
							if (getQuestItemsCount(player, DAZZLING_DROP) < 20)
							{
								htmltext = "30413-02.html";
							}
							else
							{
								takeItems(player, DAZZLING_DROP, -1);
								giveItems(player, TONE_OF_WATER, 1);
								takeItems(player, UNDINE_CHARM, 1);
								if (hasQuestItems(player, TONE_OF_FIRE, TONE_OF_WIND, TONE_OF_EARTH))
								{
									qs.setCond(6, true);
								}
								htmltext = "30413-03.html";
							}
						}
						else if (hasQuestItems(player, TONE_OF_WATER) && !hasQuestItems(player, UNDINE_CHARM))
						{
							htmltext = "30413-04.html";
						}
					}
					break;
				}
				case ELDER_CASIAN:
				{
					if (hasQuestItems(player, PARINAS_LETTER))
					{
						htmltext = "30612-01.html";
					}
					else if (hasQuestItems(player, LILAC_CHARM))
					{
						if (hasQuestItems(player, GOLDEN_SEED_1ST, GOLDEN_SEED_2ND, GOLDEN_SEED_3RD))
						{
							htmltext = "30612-04.html";
						}
						else
						{
							htmltext = "30612-03.html";
						}
					}
					else if (hasQuestItems(player, SCORE_OF_ELEMENTS))
					{
						htmltext = "30612-05.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == BARD_RUKAL)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}