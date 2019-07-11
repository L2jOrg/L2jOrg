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
package quests.Q00218_TestimonyOfLife;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Testimony Of Life (218)
 * @author ivantotov
 */
public final class Q00218_TestimonyOfLife extends Quest
{
	// NPCs
	private static final int HIERARCH_ASTERIOS = 30154;
	private static final int BLACKSMITH_PUSHKIN = 30300;
	private static final int THALIA = 30371;
	private static final int PRIEST_ADONIUS = 30375;
	private static final int ARKENIA = 30419;
	private static final int MASTER_CARDIEN = 30460;
	private static final int ISAEL_SILVERSHADOW = 30655;
	// Items
	private static final int TALINS_SPEAR = 3026;
	private static final int CARDIENS_LETTER = 3141;
	private static final int CAMOMILE_CHARM = 3142;
	private static final int HIERARCHS_LETTER = 3143;
	private static final int MOONFLOWER_CHARM = 3144;
	private static final int GRAIL_DIAGRAM = 3145;
	private static final int THALIAS_1ST_LETTER = 3146;
	private static final int THALIAS_2ND_LETTER = 3147;
	private static final int THALIAS_INSTRUCTIONS = 3148;
	private static final int PUSHKINS_LIST = 3149;
	private static final int PURE_MITHRIL_CUP = 3150;
	private static final int ARKENIAS_CONTRACT = 3151;
	private static final int ARKENIAS_INSTRUCTIONS = 3152;
	private static final int ADONIUS_LIST = 3153;
	private static final int ANDARIEL_SCRIPTURE_COPY = 3154;
	private static final int STARDUST = 3155;
	private static final int ISAELS_INSTRUCTIONS = 3156;
	private static final int ISAELS_LETTER = 3157;
	private static final int GRAIL_OF_PURITY = 3158;
	private static final int TEARS_OF_UNICORN = 3159;
	private static final int WATER_OF_LIFE = 3160;
	private static final int PURE_MITHRIL_ORE = 3161;
	private static final int ANT_SOLDIER_ACID = 3162;
	private static final int WYRMS_TALON = 3163;
	private static final int SPIDER_ICHOR = 3164;
	private static final int HARPYS_DOWN = 3165;
	private static final int TALINS_SPEAR_BLADE = 3166;
	private static final int TALINS_SPEAR_SHAFT = 3167;
	private static final int TALINS_RUBY = 3168;
	private static final int TALINS_AQUAMARINE = 3169;
	private static final int TALINS_AMETHYST = 3170;
	private static final int TALINS_PERIDOT = 3171;
	// Reward
	private static final int MARK_OF_LIFE = 3140;
	// Monster
	private static final int ANT_RECRUIT = 20082;
	private static final int ANT_PATROL = 20084;
	private static final int ANT_GUARD = 20086;
	private static final int ANT_SOLDIER = 20087;
	private static final int ANT_WARRIOR_CAPTAIN = 20088;
	private static final int HARPY = 20145;
	private static final int WYRM = 20176;
	private static final int MARSH_SPIDER = 20233;
	private static final int GUARDIAN_BASILISK = 20550;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	// Quest Monster
	private static final int UNICORN_OF_EVA = 27077;
	// Misc
	private static final int MIN_LEVEL = 37;
	private static final int LEVEL = 38;
	
	public Q00218_TestimonyOfLife()
	{
		super(218);
		addStartNpc(MASTER_CARDIEN);
		addTalkId(MASTER_CARDIEN, HIERARCH_ASTERIOS, BLACKSMITH_PUSHKIN, THALIA, PRIEST_ADONIUS, ARKENIA, ISAEL_SILVERSHADOW);
		addKillId(ANT_RECRUIT, ANT_PATROL, ANT_GUARD, ANT_SOLDIER, ANT_WARRIOR_CAPTAIN, HARPY, WYRM, MARSH_SPIDER, GUARDIAN_BASILISK, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, UNICORN_OF_EVA);
		registerQuestItems(TALINS_SPEAR, CARDIENS_LETTER, CAMOMILE_CHARM, HIERARCHS_LETTER, MOONFLOWER_CHARM, GRAIL_DIAGRAM, THALIAS_1ST_LETTER, THALIAS_2ND_LETTER, THALIAS_INSTRUCTIONS, PUSHKINS_LIST, PURE_MITHRIL_CUP, ARKENIAS_CONTRACT, ARKENIAS_INSTRUCTIONS, ADONIUS_LIST, ANDARIEL_SCRIPTURE_COPY, STARDUST, ISAELS_INSTRUCTIONS, ISAELS_LETTER, GRAIL_OF_PURITY, TEARS_OF_UNICORN, WATER_OF_LIFE, PURE_MITHRIL_ORE, ANT_SOLDIER_ACID, WYRMS_TALON, SPIDER_ICHOR, HARPYS_DOWN, TALINS_SPEAR_BLADE, TALINS_SPEAR_SHAFT, TALINS_RUBY, TALINS_AQUAMARINE, TALINS_AMETHYST, TALINS_PERIDOT);
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
				if (qs.isCreated())
				{
					qs.startQuest();
					if (!hasQuestItems(player, CARDIENS_LETTER))
					{
						giveItems(player, CARDIENS_LETTER, 1);
					}
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					htmltext = "30460-04.htm";
				}
				break;
			}
			case "30154-02.html":
			case "30154-03.html":
			case "30154-04.html":
			case "30154-05.html":
			case "30154-06.html":
			case "30300-02.html":
			case "30300-03.html":
			case "30300-04.html":
			case "30300-05.html":
			case "30300-09.html":
			case "30300-07a.html":
			case "30371-02.html":
			case "30371-10.html":
			case "30419-02.html":
			case "30419-03.html":
			{
				htmltext = event;
				break;
			}
			case "30154-07.html":
			{
				if (hasQuestItems(player, CARDIENS_LETTER))
				{
					takeItems(player, CARDIENS_LETTER, 1);
					giveItems(player, HIERARCHS_LETTER, 1);
					giveItems(player, MOONFLOWER_CHARM, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30300-06.html":
			{
				if (hasQuestItems(player, GRAIL_DIAGRAM))
				{
					takeItems(player, GRAIL_DIAGRAM, 1);
					giveItems(player, PUSHKINS_LIST, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30300-10.html":
			{
				if (hasQuestItems(player, PUSHKINS_LIST))
				{
					takeItems(player, PUSHKINS_LIST, 1);
					giveItems(player, PURE_MITHRIL_CUP, 1);
					takeItems(player, PURE_MITHRIL_ORE, -1);
					takeItems(player, ANT_SOLDIER_ACID, -1);
					takeItems(player, WYRMS_TALON, -1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30371-03.html":
			{
				if (hasQuestItems(player, HIERARCHS_LETTER))
				{
					takeItems(player, HIERARCHS_LETTER, 1);
					giveItems(player, GRAIL_DIAGRAM, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30371-11.html":
			{
				if (hasQuestItems(player, STARDUST))
				{
					giveItems(player, THALIAS_2ND_LETTER, 1);
					takeItems(player, STARDUST, 1);
					qs.setCond(14, true);
					htmltext = event;
				}
				break;
			}
			case "30419-04.html":
			{
				if (hasQuestItems(player, THALIAS_1ST_LETTER))
				{
					takeItems(player, THALIAS_1ST_LETTER, 1);
					giveItems(player, ARKENIAS_CONTRACT, 1);
					giveItems(player, ARKENIAS_INSTRUCTIONS, 1);
					qs.setCond(8, true);
					htmltext = event;
				}
				break;
			}
			case "30375-02.html":
			{
				if (hasQuestItems(player, ARKENIAS_INSTRUCTIONS))
				{
					takeItems(player, ARKENIAS_INSTRUCTIONS, 1);
					giveItems(player, ADONIUS_LIST, 1);
					qs.setCond(9, true);
					htmltext = event;
				}
				break;
			}
			case "30655-02.html":
			{
				if (hasQuestItems(player, THALIAS_2ND_LETTER))
				{
					takeItems(player, THALIAS_2ND_LETTER, 1);
					giveItems(player, ISAELS_INSTRUCTIONS, 1);
					qs.setCond(15, true);
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
				case ANT_RECRUIT:
				case ANT_PATROL:
				case ANT_GUARD:
				case ANT_SOLDIER:
				case ANT_WARRIOR_CAPTAIN:
				{
					if (hasQuestItems(killer, MOONFLOWER_CHARM, PUSHKINS_LIST) && (getQuestItemsCount(killer, ANT_SOLDIER_ACID) < 20))
					{
						giveItems(killer, ANT_SOLDIER_ACID, 2);
						if (getQuestItemsCount(killer, ANT_SOLDIER_ACID) == 20)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, PURE_MITHRIL_ORE) >= 10) && (getQuestItemsCount(killer, WYRMS_TALON) >= 20))
							{
								qs.setCond(5);
							}
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case HARPY:
				{
					if (hasQuestItems(killer, MOONFLOWER_CHARM, ADONIUS_LIST) && (getQuestItemsCount(killer, HARPYS_DOWN) < 20))
					{
						giveItems(killer, HARPYS_DOWN, 4);
						if (getQuestItemsCount(killer, HARPYS_DOWN) == 20)
						{
							
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, SPIDER_ICHOR) >= 20)
							{
								qs.setCond(10);
							}
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
					if (hasQuestItems(killer, MOONFLOWER_CHARM, PUSHKINS_LIST) && (getQuestItemsCount(killer, WYRMS_TALON) < 20))
					{
						giveItems(killer, WYRMS_TALON, 4);
						if (getQuestItemsCount(killer, WYRMS_TALON) == 20)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, PURE_MITHRIL_ORE) >= 10) && (getQuestItemsCount(killer, ANT_SOLDIER_ACID) >= 20))
							{
								qs.setCond(5);
							}
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_SPIDER:
				{
					if (hasQuestItems(killer, MOONFLOWER_CHARM, ADONIUS_LIST) && (getQuestItemsCount(killer, SPIDER_ICHOR) < 20))
					{
						giveItems(killer, SPIDER_ICHOR, 4);
						if (getQuestItemsCount(killer, SPIDER_ICHOR) == 20)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, HARPYS_DOWN) >= 20)
							{
								qs.setCond(10);
							}
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case GUARDIAN_BASILISK:
				{
					if (hasQuestItems(killer, MOONFLOWER_CHARM, PUSHKINS_LIST) && (getQuestItemsCount(killer, PURE_MITHRIL_ORE) < 10))
					{
						giveItems(killer, PURE_MITHRIL_ORE, 2);
						if (getQuestItemsCount(killer, PURE_MITHRIL_ORE) == 10)
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, WYRMS_TALON) >= 20) && (getQuestItemsCount(killer, ANT_SOLDIER_ACID) >= 20))
							{
								qs.setCond(5);
							}
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (hasQuestItems(killer, ISAELS_INSTRUCTIONS))
					{
						if (!hasQuestItems(killer, TALINS_SPEAR_BLADE))
						{
							giveItems(killer, TALINS_SPEAR_BLADE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TALINS_SPEAR_SHAFT))
						{
							giveItems(killer, TALINS_SPEAR_SHAFT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TALINS_RUBY))
						{
							giveItems(killer, TALINS_RUBY, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TALINS_AQUAMARINE))
						{
							giveItems(killer, TALINS_AQUAMARINE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TALINS_AMETHYST))
						{
							giveItems(killer, TALINS_AMETHYST, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TALINS_PERIDOT))
						{
							giveItems(killer, TALINS_PERIDOT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case UNICORN_OF_EVA:
				{
					if (!hasQuestItems(killer, TEARS_OF_UNICORN) && hasQuestItems(killer, MOONFLOWER_CHARM, TALINS_SPEAR, GRAIL_OF_PURITY))
					{
						if (npc.getKillingBlowWeapon() == TALINS_SPEAR)
						{
							takeItems(killer, TALINS_SPEAR, 1);
							takeItems(killer, GRAIL_OF_PURITY, 1);
							giveItems(killer, TEARS_OF_UNICORN, 1);
							qs.setCond(19, true);
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
			if (npc.getId() == MASTER_CARDIEN)
			{
				if (player.getRace() == Race.ELF)
				{
					if ((player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.ELF_2ND_GROUP))
					{
						htmltext = "30460-03.htm";
					}
					else if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30460-01a.html";
					}
					else
					{
						htmltext = "30460-02.html";
					}
				}
				else
				{
					htmltext = "30460-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_CARDIEN:
				{
					if (hasQuestItems(player, CARDIENS_LETTER))
					{
						htmltext = "30460-05.html";
					}
					else if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						htmltext = "30460-06.html";
					}
					else if (hasQuestItems(player, CAMOMILE_CHARM))
					{
						giveAdena(player, 342288, true);
						giveItems(player, MARK_OF_LIFE, 1);
						addExpAndSp(player, 1886832, 125918);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30460-07.html";
					}
					break;
				}
				case HIERARCH_ASTERIOS:
				{
					if (hasQuestItems(player, CARDIENS_LETTER))
					{
						htmltext = "30154-01.html";
					}
					else if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (!hasQuestItems(player, WATER_OF_LIFE))
						{
							htmltext = "30154-08.html";
						}
						else
						{
							giveItems(player, CAMOMILE_CHARM, 1);
							takeItems(player, MOONFLOWER_CHARM, 1);
							takeItems(player, WATER_OF_LIFE, 1);
							qs.setCond(21, true);
							htmltext = "30154-09.html";
						}
					}
					else if (hasQuestItems(player, CAMOMILE_CHARM))
					{
						htmltext = "30154-10.html";
					}
					break;
				}
				case BLACKSMITH_PUSHKIN:
				{
					if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (hasQuestItems(player, GRAIL_DIAGRAM))
						{
							htmltext = "30300-01.html";
						}
						else if (hasQuestItems(player, PUSHKINS_LIST))
						{
							if ((getQuestItemsCount(player, PURE_MITHRIL_ORE) >= 10) && (getQuestItemsCount(player, ANT_SOLDIER_ACID) >= 20) && (getQuestItemsCount(player, WYRMS_TALON) >= 20))
							{
								htmltext = "30300-08.html";
							}
							else
							{
								htmltext = "30300-07.html";
							}
						}
						else if (hasQuestItems(player, PURE_MITHRIL_CUP))
						{
							htmltext = "30300-11.html";
						}
						else if (!hasAtLeastOneQuestItem(player, GRAIL_DIAGRAM, PUSHKINS_LIST, PURE_MITHRIL_CUP))
						{
							htmltext = "30300-12.html";
						}
					}
					break;
				}
				case THALIA:
				{
					if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (hasQuestItems(player, HIERARCHS_LETTER))
						{
							htmltext = "30371-01.html";
						}
						else if (hasQuestItems(player, GRAIL_DIAGRAM))
						{
							htmltext = "30371-04.html";
						}
						else if (hasQuestItems(player, PUSHKINS_LIST))
						{
							htmltext = "30371-05.html";
						}
						else if (hasQuestItems(player, PURE_MITHRIL_CUP))
						{
							giveItems(player, THALIAS_1ST_LETTER, 1);
							takeItems(player, PURE_MITHRIL_CUP, 1);
							qs.setCond(7, true);
							htmltext = "30371-06.html";
						}
						else if (hasQuestItems(player, THALIAS_1ST_LETTER))
						{
							htmltext = "30371-07.html";
						}
						else if (hasQuestItems(player, ARKENIAS_CONTRACT))
						{
							htmltext = "30371-08.html";
						}
						else if (hasQuestItems(player, STARDUST))
						{
							htmltext = "30371-09.html";
						}
						else if (hasQuestItems(player, THALIAS_INSTRUCTIONS))
						{
							if (player.getLevel() >= LEVEL)
							{
								takeItems(player, THALIAS_INSTRUCTIONS, 1);
								giveItems(player, THALIAS_2ND_LETTER, 1);
								qs.setCond(14, true);
								htmltext = "30371-13.html";
							}
							else
							{
								htmltext = "30371-12.html";
							}
						}
						else if (hasQuestItems(player, THALIAS_2ND_LETTER))
						{
							htmltext = "30371-14.html";
						}
						else if (hasQuestItems(player, ISAELS_INSTRUCTIONS))
						{
							htmltext = "30371-15.html";
						}
						else if (hasQuestItems(player, TALINS_SPEAR, ISAELS_LETTER))
						{
							takeItems(player, ISAELS_LETTER, 1);
							giveItems(player, GRAIL_OF_PURITY, 1);
							qs.setCond(18, true);
							htmltext = "30371-16.html";
						}
						else if (hasQuestItems(player, TALINS_SPEAR, GRAIL_OF_PURITY))
						{
							htmltext = "30371-17.html";
						}
						else if (hasQuestItems(player, TEARS_OF_UNICORN))
						{
							takeItems(player, TEARS_OF_UNICORN, 1);
							giveItems(player, WATER_OF_LIFE, 1);
							qs.setCond(20, true);
							htmltext = "30371-18.html";
						}
						else if (hasAtLeastOneQuestItem(player, CAMOMILE_CHARM, WATER_OF_LIFE))
						{
							htmltext = "30371-19.html";
						}
					}
					break;
				}
				case ARKENIA:
				{
					if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (hasQuestItems(player, THALIAS_1ST_LETTER))
						{
							htmltext = "30419-01.html";
						}
						else if (hasAtLeastOneQuestItem(player, ARKENIAS_INSTRUCTIONS, ADONIUS_LIST))
						{
							htmltext = "30419-05.html";
						}
						else if (hasQuestItems(player, ANDARIEL_SCRIPTURE_COPY))
						{
							takeItems(player, ARKENIAS_CONTRACT, 1);
							takeItems(player, ANDARIEL_SCRIPTURE_COPY, 1);
							giveItems(player, STARDUST, 1);
							qs.setCond(12, true);
							htmltext = "30419-06.html";
						}
						else if (hasQuestItems(player, STARDUST))
						{
							htmltext = "30419-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, THALIAS_1ST_LETTER, ARKENIAS_CONTRACT, ANDARIEL_SCRIPTURE_COPY, STARDUST))
						{
							htmltext = "30419-08.html";
						}
					}
					break;
				}
				case PRIEST_ADONIUS:
				{
					if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (hasQuestItems(player, ARKENIAS_INSTRUCTIONS))
						{
							htmltext = "30375-01.html";
						}
						else if (hasQuestItems(player, ADONIUS_LIST))
						{
							if ((getQuestItemsCount(player, SPIDER_ICHOR) >= 20) && (getQuestItemsCount(player, HARPYS_DOWN) >= 20))
							{
								takeItems(player, ADONIUS_LIST, 1);
								giveItems(player, ANDARIEL_SCRIPTURE_COPY, 1);
								takeItems(player, SPIDER_ICHOR, -1);
								takeItems(player, HARPYS_DOWN, -1);
								qs.setCond(11, true);
								htmltext = "30375-04.html";
							}
							else
							{
								htmltext = "30375-03.html";
							}
						}
						else if (hasQuestItems(player, ANDARIEL_SCRIPTURE_COPY))
						{
							htmltext = "30375-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, ARKENIAS_INSTRUCTIONS, ADONIUS_LIST, ANDARIEL_SCRIPTURE_COPY))
						{
							htmltext = "30375-06.html";
						}
					}
					break;
				}
				case ISAEL_SILVERSHADOW:
				{
					if (hasQuestItems(player, MOONFLOWER_CHARM))
					{
						if (hasQuestItems(player, THALIAS_2ND_LETTER))
						{
							htmltext = "30655-01.html";
						}
						else if (hasQuestItems(player, ISAELS_INSTRUCTIONS))
						{
							if (hasQuestItems(player, TALINS_SPEAR_BLADE, TALINS_SPEAR_SHAFT, TALINS_RUBY, TALINS_AQUAMARINE, TALINS_AMETHYST, TALINS_PERIDOT))
							{
								giveItems(player, TALINS_SPEAR, 1);
								takeItems(player, ISAELS_INSTRUCTIONS, 1);
								giveItems(player, ISAELS_LETTER, 1);
								takeItems(player, TALINS_SPEAR_BLADE, 1);
								takeItems(player, TALINS_SPEAR_SHAFT, 1);
								takeItems(player, TALINS_RUBY, 1);
								takeItems(player, TALINS_AQUAMARINE, 1);
								takeItems(player, TALINS_AMETHYST, 1);
								takeItems(player, TALINS_PERIDOT, 1);
								qs.setCond(17, true);
								htmltext = "30655-04.html";
							}
							else
							{
								htmltext = "30655-03.html";
							}
						}
						else if (hasQuestItems(player, TALINS_SPEAR, ISAELS_LETTER))
						{
							htmltext = "30655-05.html";
						}
						else if (hasAtLeastOneQuestItem(player, GRAIL_OF_PURITY, WATER_OF_LIFE, CAMOMILE_CHARM))
						{
							htmltext = "30655-06.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_CARDIEN)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}