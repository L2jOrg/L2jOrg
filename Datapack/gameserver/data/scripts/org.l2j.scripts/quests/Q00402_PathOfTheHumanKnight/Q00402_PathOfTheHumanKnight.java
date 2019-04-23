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
package quests.Q00402_PathOfTheHumanKnight;

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
 * Path Of The Human Knight (402)
 * @author ivantotov
 */
public final class Q00402_PathOfTheHumanKnight extends Quest
{
	// NPCs
	private static final int HIGH_PRIEST_BIOTIN = 30031;
	private static final int LEVIAN = 30037;
	private static final int CAPTAIN_GILBERT = 30039;
	private static final int HIGH_PRIEST_RAYMOND = 30289;
	private static final int CAPTAIN_BATHIS = 30332;
	private static final int CAPTAIN_BEZIQUE = 30379;
	private static final int SIR_KLAUS_VASPER = 30417;
	private static final int SIR_ARON_TANFORD = 30653;
	// Items
	private static final int SQUIRES_MARK = 1271;
	private static final int COIN_OF_LORDS1 = 1162;
	private static final int COIN_OF_LORDS2 = 1163;
	private static final int COIN_OF_LORDS3 = 1164;
	private static final int COIN_OF_LORDS4 = 1165;
	private static final int COIN_OF_LORDS5 = 1166;
	private static final int COIN_OF_LORDS6 = 1167;
	private static final int GLUDIO_GUARDS_1ST_BADGE = 1168;
	private static final int BUGBEAR_NECKLACE = 1169;
	private static final int EINHASADS_1ST_TEMPLE_BADGE = 1170;
	private static final int EINHASAD_CRUCIFIX = 1171;
	private static final int GLUDIO_GUARDS_2ND_BADGE = 1172;
	private static final int VENOMOUS_SPIDERS_LEG = 1173;
	private static final int EINHASADS_2ND_TEMPLE_BADGE = 1174;
	private static final int LIZARDMANS_TOTEM = 1175;
	private static final int GLUDIO_GUARDS_3RD_BADGE = 1176;
	private static final int GIANT_SPIDERS_HUSK = 1177;
	private static final int EINHASADS_3RD_TEMPLE_BADGE = 1178;
	private static final int SKULL_OF_SILENT_HORROR = 1179;
	// Reward
	private static final int SWORD_OF_RITUAL = 1161;
	// Monster
	private static final int LANGK_LIZARDMAN_WARRIOR = 20024;
	private static final int LANGK_LIZARDMAN_SCOUT = 20027;
	private static final int LANGK_LIZARDMAN = 20030;
	private static final int VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_TRACKER = 20043;
	private static final int ARACHNID_PREDATOR = 20050;
	private static final int GIANT_SPIDER = 20103;
	private static final int TALON_SPIDER = 20106;
	private static final int BLADE_SPIDER = 20108;
	private static final int SILENT_HORROR = 20404;
	private static final int BUGBEAR_RAIDER = 20775;
	// Quest Monster
	private static final int UNDEAD_PRIEST = 27024;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00402_PathOfTheHumanKnight()
	{
		super(402);
		addStartNpc(SIR_KLAUS_VASPER);
		addTalkId(SIR_KLAUS_VASPER, HIGH_PRIEST_BIOTIN, LEVIAN, HIGH_PRIEST_RAYMOND, CAPTAIN_GILBERT, CAPTAIN_BATHIS, CAPTAIN_BEZIQUE, SIR_ARON_TANFORD);
		addKillId(LANGK_LIZARDMAN_WARRIOR, LANGK_LIZARDMAN_SCOUT, LANGK_LIZARDMAN, VENOMOUS_SPIDER, ARACHNID_TRACKER, ARACHNID_PREDATOR, GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER, SILENT_HORROR, BUGBEAR_RAIDER, UNDEAD_PRIEST);
		registerQuestItems(SQUIRES_MARK, COIN_OF_LORDS1, COIN_OF_LORDS2, COIN_OF_LORDS3, COIN_OF_LORDS4, COIN_OF_LORDS5, COIN_OF_LORDS6, GLUDIO_GUARDS_1ST_BADGE, BUGBEAR_NECKLACE, EINHASADS_1ST_TEMPLE_BADGE, EINHASAD_CRUCIFIX, GLUDIO_GUARDS_2ND_BADGE, VENOMOUS_SPIDERS_LEG, EINHASADS_2ND_TEMPLE_BADGE, LIZARDMANS_TOTEM, GLUDIO_GUARDS_3RD_BADGE, GIANT_SPIDERS_HUSK, EINHASADS_3RD_TEMPLE_BADGE, SKULL_OF_SILENT_HORROR);
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
		final long CoinCount = getQuestItemsCount(player, COIN_OF_LORDS1, COIN_OF_LORDS2, COIN_OF_LORDS3, COIN_OF_LORDS4, COIN_OF_LORDS5, COIN_OF_LORDS6);
		switch (event)
		{
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, SWORD_OF_RITUAL))
						{
							htmltext = "30417-04.htm";
						}
						else
						{
							htmltext = "30417-05.htm";
						}
					}
					else
					{
						htmltext = "30417-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.KNIGHT)
				{
					htmltext = "30417-02a.htm";
				}
				else
				{
					htmltext = "30417-03.htm";
				}
				break;
			}
			case "30417-08.htm":
			{
				qs.startQuest();
				giveItems(player, SQUIRES_MARK, 1);
				htmltext = event;
				break;
			}
			case "30289-02.html":
			case "30417-06.html":
			case "30417-07.htm":
			case "30417-15.html":
			{
				htmltext = event;
				break;
			}
			case "30417-13.html":
			{
				if (hasQuestItems(player, SQUIRES_MARK) && ((CoinCount) == 3))
				{
					giveItems(player, SWORD_OF_RITUAL, 1);
					takeItems(player, COIN_OF_LORDS1, 1);
					takeItems(player, COIN_OF_LORDS2, 1);
					takeItems(player, COIN_OF_LORDS3, 1);
					takeItems(player, COIN_OF_LORDS4, 1);
					takeItems(player, COIN_OF_LORDS5, 1);
					takeItems(player, COIN_OF_LORDS6, 1);
					takeItems(player, GLUDIO_GUARDS_1ST_BADGE, 1);
					takeItems(player, GLUDIO_GUARDS_2ND_BADGE, 1);
					takeItems(player, GLUDIO_GUARDS_3RD_BADGE, 1);
					takeItems(player, EINHASADS_1ST_TEMPLE_BADGE, 1);
					takeItems(player, EINHASADS_2ND_TEMPLE_BADGE, 1);
					takeItems(player, EINHASADS_3RD_TEMPLE_BADGE, 1);
					takeItems(player, BUGBEAR_NECKLACE, 1);
					takeItems(player, EINHASAD_CRUCIFIX, 1);
					takeItems(player, VENOMOUS_SPIDERS_LEG, 1);
					takeItems(player, LIZARDMANS_TOTEM, 1);
					takeItems(player, GIANT_SPIDERS_HUSK, 1);
					takeItems(player, SKULL_OF_SILENT_HORROR, 1);
					takeItems(player, SQUIRES_MARK, 1);
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
			case "30417-14.html":
			{
				if (hasQuestItems(player, SQUIRES_MARK) && ((CoinCount) > 3) && ((CoinCount) < 6))
				{
					giveItems(player, SWORD_OF_RITUAL, 1);
					takeItems(player, COIN_OF_LORDS1, 1);
					takeItems(player, COIN_OF_LORDS2, 1);
					takeItems(player, COIN_OF_LORDS3, 1);
					takeItems(player, COIN_OF_LORDS4, 1);
					takeItems(player, COIN_OF_LORDS5, 1);
					takeItems(player, COIN_OF_LORDS6, 1);
					takeItems(player, GLUDIO_GUARDS_1ST_BADGE, 1);
					takeItems(player, GLUDIO_GUARDS_2ND_BADGE, 1);
					takeItems(player, GLUDIO_GUARDS_3RD_BADGE, 1);
					takeItems(player, EINHASADS_1ST_TEMPLE_BADGE, 1);
					takeItems(player, EINHASADS_2ND_TEMPLE_BADGE, 1);
					takeItems(player, EINHASADS_3RD_TEMPLE_BADGE, 1);
					takeItems(player, BUGBEAR_NECKLACE, 1);
					takeItems(player, EINHASAD_CRUCIFIX, 1);
					takeItems(player, VENOMOUS_SPIDERS_LEG, 1);
					takeItems(player, LIZARDMANS_TOTEM, 1);
					takeItems(player, GIANT_SPIDERS_HUSK, 1);
					takeItems(player, SKULL_OF_SILENT_HORROR, 1);
					takeItems(player, SQUIRES_MARK, 1);
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
			case "30031-02.html":
			{
				giveItems(player, EINHASADS_3RD_TEMPLE_BADGE, 1);
				htmltext = event;
				break;
			}
			case "30037-02.html":
			{
				giveItems(player, EINHASADS_2ND_TEMPLE_BADGE, 1);
				htmltext = event;
				break;
			}
			case "30289-03.html":
			{
				giveItems(player, EINHASADS_1ST_TEMPLE_BADGE, 1);
				htmltext = event;
				break;
			}
			case "30039-02.html":
			{
				giveItems(player, GLUDIO_GUARDS_3RD_BADGE, 1);
				htmltext = event;
				break;
			}
			case "30379-02.html":
			{
				giveItems(player, GLUDIO_GUARDS_2ND_BADGE, 1);
				htmltext = event;
				break;
			}
			case "30332-02.html":
			{
				giveItems(player, GLUDIO_GUARDS_1ST_BADGE, 1);
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
				case LANGK_LIZARDMAN_WARRIOR:
				case LANGK_LIZARDMAN_SCOUT:
				case LANGK_LIZARDMAN:
				{
					if (hasQuestItems(killer, EINHASADS_2ND_TEMPLE_BADGE) && (getQuestItemsCount(killer, LIZARDMANS_TOTEM) < 20) && (getRandom(10) < 5))
					{
						giveItems(killer, LIZARDMANS_TOTEM, 1);
						if (getQuestItemsCount(killer, LIZARDMANS_TOTEM) == 20)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case VENOMOUS_SPIDER:
				case ARACHNID_TRACKER:
				case ARACHNID_PREDATOR:
				{
					if (hasQuestItems(killer, GLUDIO_GUARDS_2ND_BADGE) && (getQuestItemsCount(killer, VENOMOUS_SPIDERS_LEG) < 20))
					{
						giveItems(killer, VENOMOUS_SPIDERS_LEG, 1);
						if (getQuestItemsCount(killer, VENOMOUS_SPIDERS_LEG) == 20)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case GIANT_SPIDER:
				case TALON_SPIDER:
				case BLADE_SPIDER:
				{
					if (hasQuestItems(killer, GLUDIO_GUARDS_3RD_BADGE) && (getQuestItemsCount(killer, GIANT_SPIDERS_HUSK) < 20) && (getRandom(10) < 4))
					{
						giveItems(killer, GIANT_SPIDERS_HUSK, 1);
						if (getQuestItemsCount(killer, GIANT_SPIDERS_HUSK) == 20)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case SILENT_HORROR:
				{
					if (hasQuestItems(killer, EINHASADS_3RD_TEMPLE_BADGE) && (getQuestItemsCount(killer, SKULL_OF_SILENT_HORROR) < 10) && (getRandom(10) < 4))
					{
						giveItems(killer, SKULL_OF_SILENT_HORROR, 1);
						if (getQuestItemsCount(killer, SKULL_OF_SILENT_HORROR) == 10)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BUGBEAR_RAIDER:
				{
					if (hasQuestItems(killer, GLUDIO_GUARDS_1ST_BADGE) && (getQuestItemsCount(killer, BUGBEAR_NECKLACE) < 10))
					{
						giveItems(killer, BUGBEAR_NECKLACE, 1);
						if (getQuestItemsCount(killer, BUGBEAR_NECKLACE) == 10)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case UNDEAD_PRIEST:
				{
					if (hasQuestItems(killer, EINHASADS_1ST_TEMPLE_BADGE) && (getQuestItemsCount(killer, EINHASAD_CRUCIFIX) < 12) && (getRandom(10) < 5))
					{
						giveItems(killer, EINHASAD_CRUCIFIX, 1);
						if (getQuestItemsCount(killer, EINHASAD_CRUCIFIX) == 12)
						{
							playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			if (npc.getId() == SIR_KLAUS_VASPER)
			{
				htmltext = "30417-01.htm";
			}
		}
		if (qs.isCompleted())
		{
			if (npc.getId() == SIR_KLAUS_VASPER)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case SIR_KLAUS_VASPER:
				{
					final long CoinCount = getQuestItemsCount(player, COIN_OF_LORDS1, COIN_OF_LORDS2, COIN_OF_LORDS3, COIN_OF_LORDS4, COIN_OF_LORDS5, COIN_OF_LORDS6);
					if (hasQuestItems(player, SQUIRES_MARK))
					{
						if ((CoinCount) < 3)
						{
							htmltext = "30417-09.html";
						}
						else if ((CoinCount) == 3)
						{
							htmltext = "30417-10.html";
						}
						else if (((CoinCount) > 3) && ((CoinCount) < 6))
						{
							htmltext = "30417-11.html";
						}
						else
						{
							giveItems(player, SWORD_OF_RITUAL, 1);
							takeItems(player, COIN_OF_LORDS1, 1);
							takeItems(player, COIN_OF_LORDS2, 1);
							takeItems(player, COIN_OF_LORDS3, 1);
							takeItems(player, COIN_OF_LORDS4, 1);
							takeItems(player, COIN_OF_LORDS5, 1);
							takeItems(player, COIN_OF_LORDS6, 1);
							takeItems(player, SQUIRES_MARK, 1);
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
							htmltext = "30417-12.html";
						}
					}
					break;
				}
				case HIGH_PRIEST_BIOTIN:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, EINHASADS_3RD_TEMPLE_BADGE, COIN_OF_LORDS6))
					{
						htmltext = "30031-01.html";
					}
					else if (hasQuestItems(player, EINHASADS_3RD_TEMPLE_BADGE))
					{
						if (getQuestItemsCount(player, SKULL_OF_SILENT_HORROR) < 10)
						{
							htmltext = "30031-03.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS6, 1);
							takeItems(player, EINHASADS_3RD_TEMPLE_BADGE, 1);
							takeItems(player, SKULL_OF_SILENT_HORROR, -1);
							htmltext = "30031-04.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS6))
					{
						htmltext = "30031-05.html";
					}
					break;
				}
				case LEVIAN:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, EINHASADS_2ND_TEMPLE_BADGE, COIN_OF_LORDS4))
					{
						htmltext = "30037-01.html";
					}
					else if (hasQuestItems(player, EINHASADS_2ND_TEMPLE_BADGE))
					{
						if (getQuestItemsCount(player, LIZARDMANS_TOTEM) < 20)
						{
							htmltext = "30037-03.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS4, 1);
							takeItems(player, EINHASADS_2ND_TEMPLE_BADGE, 1);
							takeItems(player, LIZARDMANS_TOTEM, -1);
							htmltext = "30037-04.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS4))
					{
						htmltext = "30037-05.html";
					}
					break;
				}
				case HIGH_PRIEST_RAYMOND:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, EINHASADS_1ST_TEMPLE_BADGE, COIN_OF_LORDS2))
					{
						htmltext = "30289-01.html";
					}
					else if (hasQuestItems(player, EINHASADS_1ST_TEMPLE_BADGE))
					{
						if (getQuestItemsCount(player, EINHASAD_CRUCIFIX) < 12)
						{
							htmltext = "30289-04.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS2, 1);
							takeItems(player, EINHASADS_1ST_TEMPLE_BADGE, 1);
							takeItems(player, EINHASAD_CRUCIFIX, -1);
							htmltext = "30289-05.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS2))
					{
						htmltext = "30289-06.html";
					}
					break;
				}
				case CAPTAIN_GILBERT:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, GLUDIO_GUARDS_3RD_BADGE, COIN_OF_LORDS5))
					{
						htmltext = "30039-01.html";
					}
					else if (hasQuestItems(player, GLUDIO_GUARDS_3RD_BADGE))
					{
						if (getQuestItemsCount(player, GIANT_SPIDERS_HUSK) < 20)
						{
							htmltext = "30039-03.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS5, 1);
							takeItems(player, GLUDIO_GUARDS_3RD_BADGE, 1);
							takeItems(player, GIANT_SPIDERS_HUSK, -1);
							htmltext = "30039-04.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS5))
					{
						htmltext = "30039-05.html";
					}
					break;
				}
				case CAPTAIN_BEZIQUE:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, GLUDIO_GUARDS_2ND_BADGE, COIN_OF_LORDS3))
					{
						htmltext = "30379-01.html";
					}
					else if (hasQuestItems(player, GLUDIO_GUARDS_2ND_BADGE))
					{
						if (getQuestItemsCount(player, VENOMOUS_SPIDERS_LEG) < 20)
						{
							htmltext = "30379-03.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS3, 1);
							takeItems(player, GLUDIO_GUARDS_2ND_BADGE, 1);
							takeItems(player, VENOMOUS_SPIDERS_LEG, -1);
							htmltext = "30379-04.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS3))
					{
						htmltext = "30379-05.html";
					}
					break;
				}
				case CAPTAIN_BATHIS:
				{
					if (hasQuestItems(player, SQUIRES_MARK) && !hasAtLeastOneQuestItem(player, GLUDIO_GUARDS_1ST_BADGE, COIN_OF_LORDS1))
					{
						htmltext = "30332-01.html";
					}
					else if (hasQuestItems(player, GLUDIO_GUARDS_1ST_BADGE))
					{
						if (getQuestItemsCount(player, BUGBEAR_NECKLACE) < 10)
						{
							htmltext = "30332-03.html";
						}
						else
						{
							giveItems(player, COIN_OF_LORDS1, 1);
							takeItems(player, GLUDIO_GUARDS_1ST_BADGE, 1);
							takeItems(player, BUGBEAR_NECKLACE, -1);
							htmltext = "30332-04.html";
						}
					}
					else if (hasQuestItems(player, COIN_OF_LORDS1))
					{
						htmltext = "30332-05.html";
					}
					break;
				}
				case SIR_ARON_TANFORD:
				{
					if (hasQuestItems(player, SQUIRES_MARK))
					{
						htmltext = "30653-01.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}