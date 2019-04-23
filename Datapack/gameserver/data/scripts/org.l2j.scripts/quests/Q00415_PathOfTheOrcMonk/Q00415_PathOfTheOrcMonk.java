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
package quests.Q00415_PathOfTheOrcMonk;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Path Of The Orc Monk (415)
 * @author ivantotov
 */
public final class Q00415_PathOfTheOrcMonk extends Quest
{
	// NPCs
	private static final int PREFECT_KASMAN = 30501;
	private static final int GANTAKI_ZU_URUTU = 30587;
	private static final int KHAVATARI_ROSHEEK = 30590;
	private static final int KHAVATARI_TORUKU = 30591;
	// Items
	private static final int POMEGRANATE = 1593;
	private static final int LEATHER_POUCH_1ST = 1594;
	private static final int LEATHER_POUCH_2ND = 1595;
	private static final int LEATHER_POUCH_3RD = 1596;
	private static final int LEATHER_POUCH_1ST_FULL = 1597;
	private static final int LEATHER_POUCH_2ND_FULL = 1598;
	private static final int LEATHER_POUCH_3RD_FULL = 1599;
	private static final int KASHA_BEAR_CLAW = 1600;
	private static final int KASHA_BLADE_SPIDER_TALON = 1601;
	private static final int SCARLET_SALAMANDER_SCALE = 1602;
	private static final int FIERY_SPIRIT_SCROLL = 1603;
	private static final int ROSHEEKS_LETTER = 1604;
	private static final int GANTAKIS_LETTRT_OF_RECOMMENDATION = 1605;
	private static final int FIG = 1606;
	private static final int LEATHER_POUCH_4TF = 1607;
	private static final int LEATHER_POUCH_4TF_FULL = 1608;
	private static final int VUKU_ORK_TUSK = 1609;
	private static final int RATMAN_FANG = 1610;
	private static final int LANGK_LIZARDMAN_TOOTH = 1611;
	private static final int FELIM_LIZARDMAN_TOOTH = 1612;
	private static final int IRON_WILL_SCROLL = 1613;
	private static final int TORUKUS_LETTER = 1614;
	private static final int KASHA_SPIDERS_TOOTH = 8545;
	private static final int HORN_OF_BAAR_DRE_VANUL = 8546;
	// Reward
	private static final int KHAVATARI_TOTEM = 1615;
	// Monster
	private static final int FELIM_LIZARDMAN_WARRIOR = 20014;
	private static final int VUKU_ORC_FIGHTER = 20017;
	private static final int LANGK_LIZZARDMAN_WARRIOR = 20024;
	private static final int RATMAN_WARRIOR = 20359;
	private static final int SCARLET_SALAMANDER = 20415;
	private static final int KASHA_FANG_SPIDER = 20476;
	private static final int KASHA_BLADE_SPIDER = 20478;
	private static final int KASHA_BEAR = 20479;
	private static final int BAAR_DRE_VANUL = 21118;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00415_PathOfTheOrcMonk()
	{
		super(415);
		addStartNpc(GANTAKI_ZU_URUTU);
		addTalkId(GANTAKI_ZU_URUTU, PREFECT_KASMAN, KHAVATARI_ROSHEEK, KHAVATARI_TORUKU);
		addAttackId(FELIM_LIZARDMAN_WARRIOR, VUKU_ORC_FIGHTER, LANGK_LIZZARDMAN_WARRIOR, RATMAN_WARRIOR, SCARLET_SALAMANDER, KASHA_FANG_SPIDER, KASHA_BLADE_SPIDER, KASHA_BEAR, BAAR_DRE_VANUL);
		addKillId(FELIM_LIZARDMAN_WARRIOR, VUKU_ORC_FIGHTER, LANGK_LIZZARDMAN_WARRIOR, RATMAN_WARRIOR, SCARLET_SALAMANDER, KASHA_FANG_SPIDER, KASHA_BLADE_SPIDER, KASHA_BEAR, BAAR_DRE_VANUL);
		registerQuestItems(POMEGRANATE, LEATHER_POUCH_1ST, LEATHER_POUCH_2ND, LEATHER_POUCH_3RD, LEATHER_POUCH_1ST_FULL, LEATHER_POUCH_2ND_FULL, LEATHER_POUCH_3RD_FULL, KASHA_BEAR_CLAW, KASHA_BLADE_SPIDER_TALON, SCARLET_SALAMANDER_SCALE, FIERY_SPIRIT_SCROLL, ROSHEEKS_LETTER, GANTAKIS_LETTRT_OF_RECOMMENDATION, FIG, LEATHER_POUCH_4TF, LEATHER_POUCH_4TF_FULL, VUKU_ORK_TUSK, RATMAN_FANG, LANGK_LIZARDMAN_TOOTH, FELIM_LIZARDMAN_TOOTH, IRON_WILL_SCROLL, TORUKUS_LETTER, KASHA_SPIDERS_TOOTH, HORN_OF_BAAR_DRE_VANUL);
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
				if (player.getClassId() == ClassId.ORC_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, KHAVATARI_TOTEM))
						{
							htmltext = "30587-04.htm";
						}
						else
						{
							htmltext = "30587-05.htm";
						}
					}
					else
					{
						htmltext = "30587-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.ORC_MONK)
				{
					htmltext = "30587-02a.htm";
				}
				else
				{
					htmltext = "30587-02.htm";
				}
				break;
			}
			case "30587-06.htm":
			{
				qs.startQuest();
				giveItems(player, POMEGRANATE, 1);
				htmltext = event;
				break;
			}
			case "30587-09b.html":
			{
				if (hasQuestItems(player, FIERY_SPIRIT_SCROLL, ROSHEEKS_LETTER))
				{
					takeItems(player, ROSHEEKS_LETTER, 1);
					giveItems(player, GANTAKIS_LETTRT_OF_RECOMMENDATION, 1);
					qs.setCond(9);
					htmltext = event;
				}
				break;
			}
			case "30587-09c.html":
			{
				if (hasQuestItems(player, FIERY_SPIRIT_SCROLL, ROSHEEKS_LETTER))
				{
					takeItems(player, ROSHEEKS_LETTER, 1);
					qs.setMemoState(2);
					qs.setCond(14);
					htmltext = event;
				}
				break;
			}
			case "31979-02.html":
			{
				if (qs.isMemoState(5))
				{
					htmltext = event;
				}
				break;
			}
			case "31979-03.html":
			{
				if (qs.isMemoState(5))
				{
					giveItems(player, KHAVATARI_TOTEM, 1);
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
					htmltext = event;
				}
				break;
			}
			case "31979-04.html":
			{
				if (qs.isMemoState(5))
				{
					qs.setCond(20);
					htmltext = event;
				}
				break;
			}
			case "32056-02.html":
			{
				if (qs.isMemoState(2))
				{
					htmltext = event;
				}
				break;
			}
			case "32056-03.html":
			{
				if (qs.isMemoState(2))
				{
					qs.setMemoState(3);
					qs.setCond(15);
					htmltext = event;
				}
				break;
			}
			case "32056-08.html":
			{
				if (qs.isMemoState(4) && (getQuestItemsCount(player, HORN_OF_BAAR_DRE_VANUL) >= 1))
				{
					takeItems(player, HORN_OF_BAAR_DRE_VANUL, -1);
					qs.setMemoState(5);
					qs.setCond(19);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getScriptValue())
			{
				case 0:
				{
					if (!checkWeapon(attacker))
					{
						npc.setScriptValue(2);
					}
					else
					{
						npc.setScriptValue(1);
						npc.getVariables().set("Q00415_last_attacker", attacker.getObjectId());
					}
					break;
				}
				case 1:
				{
					if ((npc.getVariables().getInt("Q00415_last_attacker") != attacker.getObjectId()) || !checkWeapon(attacker))
					{
						npc.setScriptValue(2);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && npc.isScriptValue(1) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			final long ItemCount = getQuestItemsCount(killer, RATMAN_FANG, LANGK_LIZARDMAN_TOOTH, FELIM_LIZARDMAN_TOOTH, VUKU_ORK_TUSK);
			switch (npc.getId())
			{
				case FELIM_LIZARDMAN_WARRIOR:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_4TF) && (getQuestItemsCount(killer, FELIM_LIZARDMAN_TOOTH) < 3))
					{
						if (ItemCount >= 11)
						{
							takeItems(killer, LEATHER_POUCH_4TF, 1);
							giveItems(killer, LEATHER_POUCH_4TF_FULL, 1);
							takeItems(killer, VUKU_ORK_TUSK, -1);
							takeItems(killer, RATMAN_FANG, -1);
							takeItems(killer, LANGK_LIZARDMAN_TOOTH, -1);
							takeItems(killer, FELIM_LIZARDMAN_TOOTH, -1);
							qs.setCond(12, true);
						}
						else
						{
							giveItems(killer, FELIM_LIZARDMAN_TOOTH, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case VUKU_ORC_FIGHTER:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_4TF) && (getQuestItemsCount(killer, VUKU_ORK_TUSK) < 3))
					{
						if (ItemCount >= 11)
						{
							takeItems(killer, LEATHER_POUCH_4TF, 1);
							giveItems(killer, LEATHER_POUCH_4TF_FULL, 1);
							takeItems(killer, VUKU_ORK_TUSK, -1);
							takeItems(killer, RATMAN_FANG, -1);
							takeItems(killer, LANGK_LIZARDMAN_TOOTH, -1);
							takeItems(killer, FELIM_LIZARDMAN_TOOTH, -1);
							qs.setCond(12, true);
						}
						else
						{
							giveItems(killer, VUKU_ORK_TUSK, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LANGK_LIZZARDMAN_WARRIOR:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_4TF) && (getQuestItemsCount(killer, LANGK_LIZARDMAN_TOOTH) < 3))
					{
						if (ItemCount >= 11)
						{
							takeItems(killer, LEATHER_POUCH_4TF, 1);
							giveItems(killer, LEATHER_POUCH_4TF_FULL, 1);
							takeItems(killer, VUKU_ORK_TUSK, -1);
							takeItems(killer, RATMAN_FANG, -1);
							takeItems(killer, LANGK_LIZARDMAN_TOOTH, -1);
							takeItems(killer, FELIM_LIZARDMAN_TOOTH, -1);
							qs.setCond(12, true);
						}
						else
						{
							giveItems(killer, LANGK_LIZARDMAN_TOOTH, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case RATMAN_WARRIOR:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_4TF) && (getQuestItemsCount(killer, RATMAN_FANG) < 3))
					{
						if (ItemCount >= 11)
						{
							takeItems(killer, LEATHER_POUCH_4TF, 1);
							giveItems(killer, LEATHER_POUCH_4TF_FULL, 1);
							takeItems(killer, VUKU_ORK_TUSK, -1);
							takeItems(killer, RATMAN_FANG, -1);
							takeItems(killer, LANGK_LIZARDMAN_TOOTH, -1);
							takeItems(killer, FELIM_LIZARDMAN_TOOTH, -1);
							qs.setCond(12, true);
						}
						else
						{
							giveItems(killer, RATMAN_FANG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case SCARLET_SALAMANDER:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_3RD))
					{
						if (getQuestItemsCount(killer, SCARLET_SALAMANDER_SCALE) == 4)
						{
							takeItems(killer, LEATHER_POUCH_3RD, 1);
							giveItems(killer, LEATHER_POUCH_3RD_FULL, 1);
							takeItems(killer, SCARLET_SALAMANDER_SCALE, -1);
							qs.setCond(7, true);
						}
						else
						{
							giveItems(killer, SCARLET_SALAMANDER_SCALE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case KASHA_FANG_SPIDER:
				{
					if (qs.isMemoState(3) && (getQuestItemsCount(killer, KASHA_SPIDERS_TOOTH) < 6))
					{
						if (getRandom(100) < 70)
						{
							giveItems(killer, KASHA_SPIDERS_TOOTH, 1);
							if (getQuestItemsCount(killer, KASHA_SPIDERS_TOOTH) >= 6)
							{
								qs.setCond(16, true);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case KASHA_BLADE_SPIDER:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_2ND))
					{
						if (getQuestItemsCount(killer, KASHA_BLADE_SPIDER_TALON) == 4)
						{
							takeItems(killer, LEATHER_POUCH_2ND, 1);
							giveItems(killer, LEATHER_POUCH_2ND_FULL, 1);
							takeItems(killer, KASHA_BLADE_SPIDER_TALON, -1);
							qs.setCond(5, true);
						}
						else
						{
							giveItems(killer, KASHA_BLADE_SPIDER_TALON, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					else if (qs.isMemoState(3) && (getQuestItemsCount(killer, KASHA_SPIDERS_TOOTH) < 6))
					{
						if (getRandom(100) < 70)
						{
							giveItems(killer, KASHA_SPIDERS_TOOTH, 1);
							if (getQuestItemsCount(killer, KASHA_SPIDERS_TOOTH) == 6)
							{
								qs.setCond(16, true);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case KASHA_BEAR:
				{
					if (hasQuestItems(killer, LEATHER_POUCH_1ST))
					{
						if (getQuestItemsCount(killer, KASHA_BEAR_CLAW) == 4)
						{
							takeItems(killer, LEATHER_POUCH_1ST, 1);
							giveItems(killer, LEATHER_POUCH_1ST_FULL, 1);
							takeItems(killer, KASHA_BEAR_CLAW, -1);
							qs.setCond(3, true);
						}
						else
						{
							giveItems(killer, KASHA_BEAR_CLAW, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BAAR_DRE_VANUL:
				{
					if (qs.isMemoState(4) && !hasQuestItems(killer, HORN_OF_BAAR_DRE_VANUL))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, HORN_OF_BAAR_DRE_VANUL, 1);
							qs.setCond(18, true);
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
			if (npc.getId() == GANTAKI_ZU_URUTU)
			{
				htmltext = "30587-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == GANTAKI_ZU_URUTU)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case GANTAKI_ZU_URUTU:
				{
					final long letterCount = getQuestItemsCount(player, LEATHER_POUCH_1ST, LEATHER_POUCH_2ND, LEATHER_POUCH_3RD, LEATHER_POUCH_1ST_FULL, LEATHER_POUCH_2ND_FULL, LEATHER_POUCH_3RD_FULL);
					if (memoState == 2)
					{
						htmltext = "30587-09c.html";
					}
					else if (hasQuestItems(player, POMEGRANATE) && !hasAtLeastOneQuestItem(player, FIERY_SPIRIT_SCROLL, GANTAKIS_LETTRT_OF_RECOMMENDATION, ROSHEEKS_LETTER) && (letterCount == 0))
					{
						htmltext = "30587-07.html";
					}
					else if (!hasAtLeastOneQuestItem(player, FIERY_SPIRIT_SCROLL, POMEGRANATE, GANTAKIS_LETTRT_OF_RECOMMENDATION, ROSHEEKS_LETTER) && (letterCount == 1))
					{
						htmltext = "30587-08.html";
					}
					else if (hasQuestItems(player, FIERY_SPIRIT_SCROLL, ROSHEEKS_LETTER) && !hasAtLeastOneQuestItem(player, POMEGRANATE, GANTAKIS_LETTRT_OF_RECOMMENDATION) && (letterCount == 0))
					{
						htmltext = "30587-09a.html";
					}
					else if (memoState < 2)
					{
						if (hasQuestItems(player, FIERY_SPIRIT_SCROLL, GANTAKIS_LETTRT_OF_RECOMMENDATION) && !hasAtLeastOneQuestItem(player, POMEGRANATE, ROSHEEKS_LETTER) && (letterCount == 0))
						{
							htmltext = "30587-10.html";
						}
						else if (hasQuestItems(player, FIERY_SPIRIT_SCROLL) && !hasAtLeastOneQuestItem(player, POMEGRANATE, GANTAKIS_LETTRT_OF_RECOMMENDATION, ROSHEEKS_LETTER) && (letterCount == 0))
						{
							htmltext = "30587-11.html";
						}
					}
					break;
				}
				case PREFECT_KASMAN:
				{
					if (hasQuestItems(player, GANTAKIS_LETTRT_OF_RECOMMENDATION))
					{
						takeItems(player, GANTAKIS_LETTRT_OF_RECOMMENDATION, 1);
						giveItems(player, FIG, 1);
						qs.setCond(10);
						htmltext = "30501-01.html";
					}
					else if (hasQuestItems(player, FIG) && !hasAtLeastOneQuestItem(player, LEATHER_POUCH_4TF, LEATHER_POUCH_4TF_FULL))
					{
						htmltext = "30501-02.html";
					}
					else if (!hasQuestItems(player, FIG) && hasAtLeastOneQuestItem(player, LEATHER_POUCH_4TF, LEATHER_POUCH_4TF_FULL))
					{
						htmltext = "30501-03.html";
					}
					else if (hasQuestItems(player, IRON_WILL_SCROLL))
					{
						giveItems(player, KHAVATARI_TOTEM, 1);
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
						htmltext = "30501-04.html";
					}
					break;
				}
				case KHAVATARI_ROSHEEK:
				{
					if (hasQuestItems(player, POMEGRANATE))
					{
						takeItems(player, POMEGRANATE, 1);
						giveItems(player, LEATHER_POUCH_1ST, 1);
						qs.setCond(2);
						htmltext = "30590-01.html";
					}
					else if (hasQuestItems(player, LEATHER_POUCH_1ST) && !hasQuestItems(player, LEATHER_POUCH_1ST_FULL))
					{
						htmltext = "30590-02.html";
					}
					else if (!hasQuestItems(player, LEATHER_POUCH_1ST) && hasQuestItems(player, LEATHER_POUCH_1ST_FULL))
					{
						giveItems(player, LEATHER_POUCH_2ND, 1);
						takeItems(player, LEATHER_POUCH_1ST_FULL, 1);
						qs.setCond(4);
						htmltext = "30590-03.html";
					}
					else if (hasQuestItems(player, LEATHER_POUCH_2ND) && !hasQuestItems(player, LEATHER_POUCH_2ND_FULL))
					{
						htmltext = "30590-04.html";
					}
					else if (!hasQuestItems(player, LEATHER_POUCH_2ND) && hasQuestItems(player, LEATHER_POUCH_2ND_FULL))
					{
						giveItems(player, LEATHER_POUCH_3RD, 1);
						takeItems(player, LEATHER_POUCH_2ND_FULL, 1);
						qs.setCond(6);
						htmltext = "30590-05.html";
					}
					else if (hasQuestItems(player, LEATHER_POUCH_3RD) && !hasQuestItems(player, LEATHER_POUCH_3RD_FULL))
					{
						htmltext = "30590-06.html";
					}
					else if (!hasQuestItems(player, LEATHER_POUCH_3RD) && hasQuestItems(player, LEATHER_POUCH_3RD_FULL))
					{
						takeItems(player, LEATHER_POUCH_3RD_FULL, 1);
						giveItems(player, FIERY_SPIRIT_SCROLL, 1);
						giveItems(player, ROSHEEKS_LETTER, 1);
						qs.setCond(8);
						htmltext = "30590-07.html";
					}
					else if (hasQuestItems(player, ROSHEEKS_LETTER, FIERY_SPIRIT_SCROLL))
					{
						htmltext = "30590-08.html";
					}
					else if (!hasQuestItems(player, ROSHEEKS_LETTER) && hasQuestItems(player, FIERY_SPIRIT_SCROLL))
					{
						htmltext = "30590-09.html";
					}
					break;
				}
				case KHAVATARI_TORUKU:
				{
					if (hasQuestItems(player, FIG))
					{
						takeItems(player, FIG, 1);
						giveItems(player, LEATHER_POUCH_4TF, 1);
						qs.setCond(11);
						htmltext = "30591-01.html";
					}
					else if (hasQuestItems(player, LEATHER_POUCH_4TF) && !hasQuestItems(player, LEATHER_POUCH_4TF_FULL))
					{
						htmltext = "30591-02.html";
					}
					else if (!hasQuestItems(player, LEATHER_POUCH_4TF) && hasQuestItems(player, LEATHER_POUCH_4TF_FULL))
					{
						takeItems(player, LEATHER_POUCH_4TF_FULL, 1);
						giveItems(player, IRON_WILL_SCROLL, 1);
						giveItems(player, TORUKUS_LETTER, 1);
						qs.setCond(13);
						htmltext = "30591-03.html";
					}
					else if (hasQuestItems(player, IRON_WILL_SCROLL, TORUKUS_LETTER))
					{
						htmltext = "30591-04.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	private static boolean checkWeapon(L2PcInstance player)
	{
		L2ItemInstance weapon = player.getActiveWeaponInstance();
		return ((weapon == null) || (weapon.getItemType() == WeaponType.FIST) || (weapon.getItemType() == WeaponType.DUALFIST));
	}
}