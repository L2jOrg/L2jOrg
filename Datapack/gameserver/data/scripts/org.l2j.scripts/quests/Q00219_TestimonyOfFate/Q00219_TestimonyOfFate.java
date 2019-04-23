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
package quests.Q00219_TestimonyOfFate;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Testimony Of Fate (219)
 * @author ivantotov
 */
public final class Q00219_TestimonyOfFate extends Quest
{
	// NPCs
	private static final int MAGISTER_ROA = 30114;
	private static final int WAREHOUSE_KEEPER_NORMAN = 30210;
	private static final int TETRARCH_THIFIELL = 30358;
	private static final int ARKENIA = 30419;
	private static final int MASTER_IXIA = 30463;
	private static final int MAGISTER_KAIRA = 30476;
	private static final int ALDERS_SPIRIT = 30613;
	private static final int BROTHER_METHEUS = 30614;
	private static final int BLOODY_PIXY = 31845;
	private static final int BLIGHT_TREANT = 31850;
	// Items
	private static final int KAIRAS_LETTER = 3173;
	private static final int METHEUSS_FUNERAL_JAR = 3174;
	private static final int KASANDRAS_REMAINS = 3175;
	private static final int HERBALISM_TEXTBOOK = 3176;
	private static final int IXIAS_LIST = 3177;
	private static final int MEDUSAS_ICHOR = 3178;
	private static final int MARSH_SPIDER_FLUIDS = 3179;
	private static final int DEAD_SEEKER_DUNG = 3180;
	private static final int TYRANTS_BLOOD = 3181;
	private static final int NIGHTSHADE_ROOT = 3182;
	private static final int BELLADONNA = 3183;
	private static final int ALDERS_SKULL1 = 3184;
	private static final int ALDERS_SKULL2 = 3185;
	private static final int ALDERS_RECEIPT = 3186;
	private static final int REVELATIONS_MANUSCRIPT = 3187;
	private static final int KAIRAS_RECOMMENDATION = 3189;
	private static final int KAIRAS_INSTRUCTIONS = 3188;
	private static final int PALUS_CHARM = 3190;
	private static final int THIFIELLS_LETTER = 3191;
	private static final int ARKENIAS_NOTE = 3192;
	private static final int PIXY_GARNET = 3193;
	private static final int GRANDISS_SKULL = 3194;
	private static final int KARUL_BUGBEAR_SKULL = 3195;
	private static final int BREKA_OVERLORD_SKULL = 3196;
	private static final int LETO_OVERLORD_SKULL = 3197;
	private static final int RED_FAIRY_DUST = 3198;
	private static final int TIMIRIRAN_SEED = 3199;
	private static final int BLACK_WILLOW_LEAF = 3200;
	private static final int BLIGHT_TREANT_SAP = 3201;
	private static final int ARKENIAS_LETTER = 3202;
	// Reward
	private static final int MARK_OF_FATE = 3172;
	// Monster
	private static final int HANGMAN_TREE = 20144;
	private static final int MARSH_STAKATO = 20157;
	private static final int MEDUSA = 20158;
	private static final int TYRANT = 20192;
	private static final int TYRANT_KINGPIN = 20193;
	private static final int DEAD_SEEKER = 20202;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_SPIDER = 20233;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int GRANDIS = 20554;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int KARUL_BUGBEAR = 20600;
	// Quest Monster
	private static final int BLACK_WILLOW_LURKER = 27079;
	// Misc
	private static final int MIN_LEVEL = 37;
	
	public Q00219_TestimonyOfFate()
	{
		super(219);
		addStartNpc(MAGISTER_KAIRA);
		addTalkId(MAGISTER_KAIRA, MAGISTER_ROA, WAREHOUSE_KEEPER_NORMAN, TETRARCH_THIFIELL, ARKENIA, MASTER_IXIA, ALDERS_SPIRIT, BROTHER_METHEUS, BLOODY_PIXY, BLIGHT_TREANT);
		addKillId(HANGMAN_TREE, MARSH_STAKATO, MEDUSA, TYRANT, TYRANT_KINGPIN, DEAD_SEEKER, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_SPIDER, MARSH_STAKATO_DRONE, BREKA_ORC_OVERLORD, GRANDIS, LETO_LIZARDMAN_OVERLORD, KARUL_BUGBEAR, BLACK_WILLOW_LURKER);
		registerQuestItems(KAIRAS_LETTER, METHEUSS_FUNERAL_JAR, KASANDRAS_REMAINS, HERBALISM_TEXTBOOK, IXIAS_LIST, MEDUSAS_ICHOR, MARSH_SPIDER_FLUIDS, DEAD_SEEKER_DUNG, TYRANTS_BLOOD, NIGHTSHADE_ROOT, BELLADONNA, ALDERS_SKULL1, ALDERS_SKULL2, ALDERS_RECEIPT, REVELATIONS_MANUSCRIPT, KAIRAS_RECOMMENDATION, KAIRAS_INSTRUCTIONS, PALUS_CHARM, THIFIELLS_LETTER, ARKENIAS_NOTE, PIXY_GARNET, GRANDISS_SKULL, KARUL_BUGBEAR_SKULL, BREKA_OVERLORD_SKULL, LETO_OVERLORD_SKULL, RED_FAIRY_DUST, TIMIRIRAN_SEED, BLACK_WILLOW_LEAF, BLIGHT_TREANT_SAP, ARKENIAS_LETTER);
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
					giveItems(player, KAIRAS_LETTER, 1);
				}
				break;
			}
			case "30476-04.htm":
			case "30476-13.html":
			case "30476-14.html":
			case "30114-02.html":
			case "30114-03.html":
			case "30463-02a.html":
			{
				htmltext = event;
				break;
			}
			case "30476-12.html":
			{
				if (hasQuestItems(player, REVELATIONS_MANUSCRIPT))
				{
					takeItems(player, REVELATIONS_MANUSCRIPT, 1);
					giveItems(player, KAIRAS_RECOMMENDATION, 1);
					qs.setCond(15, true);
					htmltext = event;
				}
				break;
			}
			case "30114-04.html":
			{
				if (hasQuestItems(player, ALDERS_SKULL2))
				{
					takeItems(player, ALDERS_SKULL2, 1);
					giveItems(player, ALDERS_RECEIPT, 1);
					qs.setCond(12, true);
					htmltext = event;
				}
				break;
			}
			case "30419-02.html":
			{
				if (hasQuestItems(player, THIFIELLS_LETTER))
				{
					takeItems(player, THIFIELLS_LETTER, 1);
					giveItems(player, ARKENIAS_NOTE, 1);
					qs.setCond(17, true);
					htmltext = event;
				}
				break;
			}
			case "30419-05.html":
			{
				if (hasQuestItems(player, ARKENIAS_NOTE, RED_FAIRY_DUST, BLIGHT_TREANT_SAP))
				{
					takeItems(player, ARKENIAS_NOTE, 1);
					takeItems(player, RED_FAIRY_DUST, 1);
					takeItems(player, BLIGHT_TREANT_SAP, 1);
					giveItems(player, ARKENIAS_LETTER, 1);
					qs.setCond(18, true);
					htmltext = event;
				}
				break;
			}
			case "31845-02.html":
			{
				giveItems(player, PIXY_GARNET, 1);
				htmltext = event;
				break;
			}
			case "31850-02.html":
			{
				giveItems(player, TIMIRIRAN_SEED, 1);
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
				case HANGMAN_TREE:
				{
					if (hasQuestItems(killer, METHEUSS_FUNERAL_JAR) && !hasQuestItems(killer, KASANDRAS_REMAINS))
					{
						takeItems(killer, METHEUSS_FUNERAL_JAR, 1);
						giveItems(killer, KASANDRAS_REMAINS, 1);
						qs.setCond(3, true);
					}
				}
				case MARSH_STAKATO:
				case MARSH_STAKATO_WORKER:
				case MARSH_STAKATO_SOLDIER:
				case MARSH_STAKATO_DRONE:
				{
					if (hasQuestItems(killer, IXIAS_LIST) && (getQuestItemsCount(killer, NIGHTSHADE_ROOT) < 10))
					{
						if (getQuestItemsCount(killer, NIGHTSHADE_ROOT) == 9)
						{
							giveItems(killer, NIGHTSHADE_ROOT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MEDUSAS_ICHOR) >= 10) && (getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) >= 10) && (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) >= 10) && (getQuestItemsCount(killer, TYRANTS_BLOOD) >= 10))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, NIGHTSHADE_ROOT, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MEDUSA:
				{
					if (hasQuestItems(killer, IXIAS_LIST) && (getQuestItemsCount(killer, MEDUSAS_ICHOR) < 10))
					{
						if (getQuestItemsCount(killer, MEDUSAS_ICHOR) == 9)
						{
							giveItems(killer, MEDUSAS_ICHOR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) >= 10) && (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) >= 10) && (getQuestItemsCount(killer, TYRANTS_BLOOD) >= 10) && (getQuestItemsCount(killer, NIGHTSHADE_ROOT) >= 10))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, MEDUSAS_ICHOR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case TYRANT:
				case TYRANT_KINGPIN:
				{
					if (hasQuestItems(killer, IXIAS_LIST) && (getQuestItemsCount(killer, TYRANTS_BLOOD) < 10))
					{
						if (getQuestItemsCount(killer, TYRANTS_BLOOD) == 9)
						{
							giveItems(killer, TYRANTS_BLOOD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MEDUSAS_ICHOR) >= 10) && (getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) >= 10) && (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) >= 10) && (getQuestItemsCount(killer, NIGHTSHADE_ROOT) >= 10))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, TYRANTS_BLOOD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case DEAD_SEEKER:
				{
					if (hasQuestItems(killer, IXIAS_LIST) && (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) < 10))
					{
						if (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) == 9)
						{
							giveItems(killer, DEAD_SEEKER_DUNG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MEDUSAS_ICHOR) >= 10) && (getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) >= 10) && (getQuestItemsCount(killer, TYRANTS_BLOOD) >= 10) && (getQuestItemsCount(killer, NIGHTSHADE_ROOT) >= 10))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, DEAD_SEEKER_DUNG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_SPIDER:
				{
					if (hasQuestItems(killer, IXIAS_LIST) && (getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) < 10))
					{
						if (getQuestItemsCount(killer, MARSH_SPIDER_FLUIDS) == 9)
						{
							giveItems(killer, MARSH_SPIDER_FLUIDS, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MEDUSAS_ICHOR) >= 10) && (getQuestItemsCount(killer, DEAD_SEEKER_DUNG) >= 10) && (getQuestItemsCount(killer, TYRANTS_BLOOD) >= 10) && (getQuestItemsCount(killer, NIGHTSHADE_ROOT) >= 10))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, MARSH_SPIDER_FLUIDS, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BREKA_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, PALUS_CHARM, ARKENIAS_NOTE, PIXY_GARNET) && !hasQuestItems(killer, RED_FAIRY_DUST, BREKA_OVERLORD_SKULL))
					{
						if (!hasQuestItems(killer, BREKA_OVERLORD_SKULL))
						{
							giveItems(killer, BREKA_OVERLORD_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case GRANDIS:
				{
					if (hasQuestItems(killer, PALUS_CHARM, ARKENIAS_NOTE, PIXY_GARNET) && !hasQuestItems(killer, RED_FAIRY_DUST, GRANDISS_SKULL))
					{
						if (!hasQuestItems(killer, GRANDISS_SKULL))
						{
							giveItems(killer, GRANDISS_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (hasQuestItems(killer, PALUS_CHARM, ARKENIAS_NOTE, PIXY_GARNET) && !hasQuestItems(killer, RED_FAIRY_DUST, LETO_OVERLORD_SKULL))
					{
						if (!hasQuestItems(killer, LETO_OVERLORD_SKULL))
						{
							giveItems(killer, LETO_OVERLORD_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case KARUL_BUGBEAR:
				{
					if (hasQuestItems(killer, PALUS_CHARM, ARKENIAS_NOTE, PIXY_GARNET) && !hasQuestItems(killer, RED_FAIRY_DUST, KARUL_BUGBEAR_SKULL))
					{
						if (!hasQuestItems(killer, KARUL_BUGBEAR_SKULL))
						{
							giveItems(killer, KARUL_BUGBEAR_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case BLACK_WILLOW_LURKER:
				{
					if (hasQuestItems(killer, PALUS_CHARM, ARKENIAS_NOTE, TIMIRIRAN_SEED) && !hasQuestItems(killer, BLIGHT_TREANT_SAP, BLACK_WILLOW_LEAF))
					{
						giveItems(killer, BLACK_WILLOW_LEAF, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			if (npc.getId() == MAGISTER_KAIRA)
			{
				if (player.getRace() == Race.DARK_ELF)
				{
					if ((player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.DELF_2ND_GROUP))
					{
						htmltext = "30476-03.htm";
					}
					else if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30476-01a.html";
					}
					else
					{
						htmltext = "30476-02.html";
					}
				}
				else
				{
					htmltext = "30476-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MAGISTER_KAIRA:
				{
					if (hasQuestItems(player, KAIRAS_LETTER))
					{
						htmltext = "30476-06.html";
					}
					else if (hasAtLeastOneQuestItem(player, METHEUSS_FUNERAL_JAR, KASANDRAS_REMAINS))
					{
						htmltext = "30476-07.html";
					}
					else if (hasAtLeastOneQuestItem(player, HERBALISM_TEXTBOOK, IXIAS_LIST))
					{
						qs.setCond(5, true);
						htmltext = "30476-08.html";
					}
					else if (hasQuestItems(player, ALDERS_SKULL1))
					{
						takeItems(player, ALDERS_SKULL1, 1);
						giveItems(player, ALDERS_SKULL2, 1);
						addSpawn(ALDERS_SPIRIT, 78977, 149036, -3597, 0, false, 200000, false);
						qs.setCond(10, true);
						htmltext = "30476-09.html";
					}
					else if (hasAtLeastOneQuestItem(player, ALDERS_SKULL2, ALDERS_RECEIPT))
					{
						qs.setCond(11, true);
						htmltext = "30476-10.html";
					}
					else if (hasQuestItems(player, REVELATIONS_MANUSCRIPT))
					{
						htmltext = "30476-11.html";
					}
					else if (hasQuestItems(player, KAIRAS_INSTRUCTIONS))
					{
						giveItems(player, KAIRAS_RECOMMENDATION, 1);
						takeItems(player, KAIRAS_INSTRUCTIONS, 1);
						qs.setCond(15, true);
						htmltext = "30476-15.html";
					}
					else if (hasQuestItems(player, KAIRAS_RECOMMENDATION))
					{
						htmltext = "30476-16.html";
					}
					else if (hasQuestItems(player, PALUS_CHARM))
					{
						htmltext = "30476-17.html";
					}
					break;
				}
				case BROTHER_METHEUS:
				{
					if (hasQuestItems(player, KAIRAS_LETTER))
					{
						takeItems(player, KAIRAS_LETTER, 1);
						giveItems(player, METHEUSS_FUNERAL_JAR, 1);
						qs.setCond(2, true);
						htmltext = "30614-01.html";
					}
					else if (hasQuestItems(player, METHEUSS_FUNERAL_JAR) && !hasQuestItems(player, KASANDRAS_REMAINS))
					{
						htmltext = "30614-02.html";
					}
					else if (hasQuestItems(player, KASANDRAS_REMAINS) && !hasQuestItems(player, METHEUSS_FUNERAL_JAR))
					{
						takeItems(player, KASANDRAS_REMAINS, 1);
						giveItems(player, HERBALISM_TEXTBOOK, 1);
						qs.setCond(4, true);
						htmltext = "30614-03.html";
					}
					else if (hasAtLeastOneQuestItem(player, HERBALISM_TEXTBOOK, IXIAS_LIST))
					{
						qs.setCond(5, true);
						htmltext = "30614-04.html";
					}
					else if (hasQuestItems(player, BELLADONNA))
					{
						takeItems(player, BELLADONNA, 1);
						giveItems(player, ALDERS_SKULL1, 1);
						qs.setCond(9, true);
						htmltext = "30614-05.html";
					}
					else if (hasAtLeastOneQuestItem(player, ALDERS_SKULL1, ALDERS_SKULL2, ALDERS_RECEIPT, REVELATIONS_MANUSCRIPT, KAIRAS_INSTRUCTIONS, KAIRAS_RECOMMENDATION))
					{
						htmltext = "30614-06.html";
					}
					break;
				}
				case MASTER_IXIA:
				{
					if (hasQuestItems(player, HERBALISM_TEXTBOOK))
					{
						takeItems(player, HERBALISM_TEXTBOOK, 1);
						giveItems(player, IXIAS_LIST, 1);
						qs.setCond(6, true);
						htmltext = "30463-01.html";
					}
					else if (hasQuestItems(player, IXIAS_LIST))
					{
						if ((getQuestItemsCount(player, MEDUSAS_ICHOR) >= 10) && (getQuestItemsCount(player, MARSH_SPIDER_FLUIDS) >= 10) && (getQuestItemsCount(player, DEAD_SEEKER_DUNG) >= 10) && (getQuestItemsCount(player, TYRANTS_BLOOD) >= 10) && (getQuestItemsCount(player, NIGHTSHADE_ROOT) >= 10))
						{
							takeItems(player, IXIAS_LIST, 1);
							takeItems(player, MEDUSAS_ICHOR, -1);
							takeItems(player, MARSH_SPIDER_FLUIDS, -1);
							takeItems(player, DEAD_SEEKER_DUNG, -1);
							takeItems(player, TYRANTS_BLOOD, -1);
							takeItems(player, NIGHTSHADE_ROOT, -1);
							giveItems(player, BELLADONNA, 1);
							qs.setCond(8, true);
							htmltext = "30463-03.html";
						}
						else
						{
							htmltext = "30463-02.html";
						}
					}
					else if (hasQuestItems(player, BELLADONNA))
					{
						htmltext = "30463-04.html";
					}
					else if (hasAtLeastOneQuestItem(player, ALDERS_SKULL1, ALDERS_SKULL2, ALDERS_RECEIPT, REVELATIONS_MANUSCRIPT, KAIRAS_INSTRUCTIONS, KAIRAS_RECOMMENDATION))
					{
						htmltext = "30463-05.html";
					}
					break;
				}
				case MAGISTER_ROA:
				{
					if (hasQuestItems(player, ALDERS_SKULL2))
					{
						htmltext = "30114-01.html";
					}
					else if (hasQuestItems(player, ALDERS_RECEIPT))
					{
						htmltext = "30114-05.html";
					}
					else if (hasAtLeastOneQuestItem(player, REVELATIONS_MANUSCRIPT, KAIRAS_INSTRUCTIONS, KAIRAS_RECOMMENDATION))
					{
						htmltext = "30114-06.html";
					}
					break;
				}
				case WAREHOUSE_KEEPER_NORMAN:
				{
					if (hasQuestItems(player, ALDERS_RECEIPT))
					{
						takeItems(player, ALDERS_RECEIPT, 1);
						giveItems(player, REVELATIONS_MANUSCRIPT, 1);
						qs.setCond(13, true);
						htmltext = "30210-01.html";
					}
					else if (hasQuestItems(player, REVELATIONS_MANUSCRIPT))
					{
						htmltext = "30210-02.html";
					}
				}
				case TETRARCH_THIFIELL:
				{
					if (hasQuestItems(player, KAIRAS_RECOMMENDATION))
					{
						takeItems(player, KAIRAS_RECOMMENDATION, 1);
						giveItems(player, PALUS_CHARM, 1);
						giveItems(player, THIFIELLS_LETTER, 1);
						qs.setCond(16, true);
						htmltext = "30358-01.html";
					}
					else if (hasQuestItems(player, PALUS_CHARM))
					{
						if (hasQuestItems(player, THIFIELLS_LETTER))
						{
							htmltext = "30358-02.html";
						}
						else if (hasQuestItems(player, ARKENIAS_NOTE))
						{
							htmltext = "30358-03.html";
						}
						else if (hasQuestItems(player, ARKENIAS_LETTER))
						{
							giveAdena(player, 247708, true);
							giveItems(player, MARK_OF_FATE, 1);
							addExpAndSp(player, 1365470, 91124);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30358-04.html";
						}
					}
					break;
				}
				case ARKENIA:
				{
					if (hasQuestItems(player, PALUS_CHARM))
					{
						if (hasQuestItems(player, THIFIELLS_LETTER))
						{
							htmltext = "30419-01.html";
						}
						else if (hasQuestItems(player, ARKENIAS_NOTE) && !hasQuestItems(player, RED_FAIRY_DUST, BLIGHT_TREANT_SAP))
						{
							htmltext = "30419-03.html";
						}
						else if (hasQuestItems(player, ARKENIAS_NOTE, RED_FAIRY_DUST, BLIGHT_TREANT_SAP))
						{
							htmltext = "30419-04.html";
						}
						else if (hasQuestItems(player, ARKENIAS_LETTER))
						{
							htmltext = "30419-06.html";
						}
					}
					break;
				}
				case ALDERS_SPIRIT:
				{
					if (hasAtLeastOneQuestItem(player, ALDERS_SKULL1, ALDERS_SKULL2))
					{
						htmltext = "30613-01.html";
					}
					break;
				}
				case BLOODY_PIXY:
				{
					if (hasQuestItems(player, PALUS_CHARM, ARKENIAS_NOTE))
					{
						if (!hasAtLeastOneQuestItem(player, RED_FAIRY_DUST, PIXY_GARNET))
						{
							htmltext = "31845-01.html";
						}
						else if (!hasQuestItems(player, RED_FAIRY_DUST) && hasQuestItems(player, PIXY_GARNET) && !hasAtLeastOneQuestItem(player, GRANDISS_SKULL, KARUL_BUGBEAR_SKULL, BREKA_OVERLORD_SKULL, LETO_OVERLORD_SKULL))
						{
							htmltext = "31845-03.html";
						}
						else if (!hasQuestItems(player, RED_FAIRY_DUST) && hasQuestItems(player, PIXY_GARNET, GRANDISS_SKULL, KARUL_BUGBEAR_SKULL, BREKA_OVERLORD_SKULL, LETO_OVERLORD_SKULL))
						{
							takeItems(player, PIXY_GARNET, 1);
							takeItems(player, GRANDISS_SKULL, 1);
							takeItems(player, KARUL_BUGBEAR_SKULL, 1);
							takeItems(player, BREKA_OVERLORD_SKULL, 1);
							takeItems(player, LETO_OVERLORD_SKULL, 1);
							giveItems(player, RED_FAIRY_DUST, 1);
							htmltext = "31845-04.html";
						}
						else if (!hasQuestItems(player, PIXY_GARNET) && hasQuestItems(player, PALUS_CHARM, ARKENIAS_NOTE, RED_FAIRY_DUST))
						{
							htmltext = "31845-05.html";
						}
					}
					break;
				}
				case BLIGHT_TREANT:
				{
					if (hasQuestItems(player, PALUS_CHARM, ARKENIAS_NOTE))
					{
						if (!hasAtLeastOneQuestItem(player, BLIGHT_TREANT_SAP, TIMIRIRAN_SEED))
						{
							htmltext = "31850-01.html";
						}
						else if (hasQuestItems(player, TIMIRIRAN_SEED) && !hasAtLeastOneQuestItem(player, BLIGHT_TREANT_SAP, BLACK_WILLOW_LEAF))
						{
							htmltext = "31850-03.html";
						}
						else if (hasQuestItems(player, TIMIRIRAN_SEED, BLACK_WILLOW_LEAF) && !hasQuestItems(player, BLIGHT_TREANT_SAP))
						{
							takeItems(player, TIMIRIRAN_SEED, 1);
							takeItems(player, BLACK_WILLOW_LEAF, 1);
							giveItems(player, BLIGHT_TREANT_SAP, 1);
							htmltext = "31850-04.html";
						}
						else if (hasQuestItems(player, BLIGHT_TREANT_SAP) && !hasQuestItems(player, TIMIRIRAN_SEED))
						{
							htmltext = "31850-05.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MAGISTER_KAIRA)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}