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
package quests.Q00214_TrialOfTheScholar;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Trial Of The Scholar (214)
 * @author ivantotov
 */
public final class Q00214_TrialOfTheScholar extends Quest
{
	// NPCs
	private static final int HIGH_PRIEST_SYLVAIN = 30070;
	private static final int CAPTAIN_LUCAS = 30071;
	private static final int WAREHOUSE_KEEPER_VALKON = 30103;
	private static final int MAGISTER_DIETER = 30111;
	private static final int GRAND_MAGISTER_JUREK = 30115;
	private static final int TRADER_EDROC = 30230;
	private static final int WAREHOUSE_KEEPER_RAUT = 30316;
	private static final int BLACKSMITH_POITAN = 30458;
	private static final int MAGISTER_MIRIEN = 30461;
	private static final int MARIA = 30608;
	private static final int ASTROLOGER_CRETA = 30609;
	private static final int ELDER_CRONOS = 30610;
	private static final int DRUNKARD_TRIFF = 30611;
	private static final int ELDER_CASIAN = 30612;
	// Items
	private static final int MIRIENS_1ST_SIGIL = 2675;
	private static final int MIRIENS_2ND_SIGIL = 2676;
	private static final int MIRIENS_3RD_SIGIL = 2677;
	private static final int MIRIENS_INSTRUCTION = 2678;
	private static final int MARIAS_1ST_LETTER = 2679;
	private static final int MARIAS_2ND_LETTER = 2680;
	private static final int LUCASS_LETTER = 2681;
	private static final int LUCILLAS_HANDBAG = 2682;
	private static final int CRETAS_1ST_LETTER = 2683;
	private static final int CRERAS_PAINTING1 = 2684;
	private static final int CRERAS_PAINTING2 = 2685;
	private static final int CRERAS_PAINTING3 = 2686;
	private static final int BROWN_SCROLL_SCRAP = 2687;
	private static final int CRYSTAL_OF_PURITY1 = 2688;
	private static final int HIGH_PRIESTS_SIGIL = 2689;
	private static final int GRAND_MAGISTER_SIGIL = 2690;
	private static final int CRONOS_SIGIL = 2691;
	private static final int SYLVAINS_LETTER = 2692;
	private static final int SYMBOL_OF_SYLVAIN = 2693;
	private static final int JUREKS_LIST = 2694;
	private static final int MONSTER_EYE_DESTROYER_SKIN = 2695;
	private static final int SHAMANS_NECKLACE = 2696;
	private static final int SHACKLES_SCALP = 2697;
	private static final int SYMBOL_OF_JUREK = 2698;
	private static final int CRONOS_LETTER = 2699;
	private static final int DIETERS_KEY = 2700;
	private static final int CRETAS_2ND_LETTER = 2701;
	private static final int DIETERS_LETTER = 2702;
	private static final int DIETERS_DIARY = 2703;
	private static final int RAUTS_LETTER_ENVELOPE = 2704;
	private static final int TRIFFS_RING = 2705;
	private static final int SCRIPTURE_CHAPTER_1 = 2706;
	private static final int SCRIPTURE_CHAPTER_2 = 2707;
	private static final int SCRIPTURE_CHAPTER_3 = 2708;
	private static final int SCRIPTURE_CHAPTER_4 = 2709;
	private static final int VALKONS_REQUEST = 2710;
	private static final int POITANS_NOTES = 2711;
	private static final int STRONG_LIGUOR = 2713;
	private static final int CRYSTAL_OF_PURITY2 = 2714;
	private static final int CASIANS_LIST = 2715;
	private static final int GHOULS_SKIN = 2716;
	private static final int MEDUSAS_BLOOD = 2717;
	private static final int FETTERED_SOULS_ICHOR = 2718;
	private static final int ENCHANTED_GARGOYLES_NAIL = 2719;
	private static final int SYMBOL_OF_CRONOS = 2720;
	// Reward
	private static final int MARK_OF_SCHOLAR = 2674;
	// Monsters
	private static final int MONSTER_EYE_DESTREOYER = 20068;
	private static final int MEDUSA = 20158;
	private static final int GHOUL = 20201;
	private static final int SHACKLE1 = 20235;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int SHACKLE2 = 20279;
	private static final int FETTERED_SOUL = 20552;
	private static final int GRANDIS = 20554;
	private static final int ENCHANTED_GARGOYLE = 20567;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	// Misc
	private static final int MIN_LVL = 35;
	private static final int LEVEL = 36;
	
	public Q00214_TrialOfTheScholar()
	{
		super(214);
		addStartNpc(MAGISTER_MIRIEN);
		addTalkId(MAGISTER_MIRIEN, HIGH_PRIEST_SYLVAIN, CAPTAIN_LUCAS, WAREHOUSE_KEEPER_VALKON, MAGISTER_DIETER, GRAND_MAGISTER_JUREK, TRADER_EDROC, WAREHOUSE_KEEPER_RAUT, BLACKSMITH_POITAN, MARIA, ASTROLOGER_CRETA, ELDER_CRONOS, DRUNKARD_TRIFF, ELDER_CASIAN);
		addKillId(MONSTER_EYE_DESTREOYER, MEDUSA, GHOUL, SHACKLE1, BREKA_ORC_SHAMAN, SHACKLE2, FETTERED_SOUL, GRANDIS, ENCHANTED_GARGOYLE, LETO_LIZARDMAN_WARRIOR);
		registerQuestItems(MIRIENS_1ST_SIGIL, MIRIENS_2ND_SIGIL, MIRIENS_3RD_SIGIL, MIRIENS_INSTRUCTION, MARIAS_1ST_LETTER, MARIAS_2ND_LETTER, LUCASS_LETTER, LUCILLAS_HANDBAG, CRETAS_1ST_LETTER, CRERAS_PAINTING1, CRERAS_PAINTING1, CRERAS_PAINTING3, BROWN_SCROLL_SCRAP, CRYSTAL_OF_PURITY1, HIGH_PRIESTS_SIGIL, GRAND_MAGISTER_SIGIL, CRONOS_SIGIL, SYLVAINS_LETTER, SYMBOL_OF_SYLVAIN, JUREKS_LIST, MONSTER_EYE_DESTROYER_SKIN, SHAMANS_NECKLACE, SHACKLES_SCALP, SYMBOL_OF_JUREK, CRONOS_LETTER, DIETERS_KEY, CRETAS_2ND_LETTER, DIETERS_LETTER, DIETERS_DIARY, RAUTS_LETTER_ENVELOPE, TRIFFS_RING, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3, SCRIPTURE_CHAPTER_4, VALKONS_REQUEST, POITANS_NOTES, STRONG_LIGUOR, CRYSTAL_OF_PURITY2, CASIANS_LIST, GHOULS_SKIN, MEDUSAS_BLOOD, FETTERED_SOULS_ICHOR, ENCHANTED_GARGOYLES_NAIL, SYMBOL_OF_CRONOS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
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
					if (!hasQuestItems(player, MIRIENS_1ST_SIGIL))
					{
						giveItems(player, MIRIENS_1ST_SIGIL, 1);
					}
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30103-02.html":
			case "30103-03.html":
			case "30111-02.html":
			case "30111-03.html":
			case "30111-04.html":
			case "30111-08.html":
			case "30111-14.html":
			case "30115-02.html":
			case "30316-03.html":
			case "30461-09.html":
			case "30608-07.html":
			case "30609-02.html":
			case "30609-03.html":
			case "30609-04.html":
			case "30609-08.html":
			case "30609-13.html":
			case "30610-02.html":
			case "30610-03.html":
			case "30610-04.html":
			case "30610-05.html":
			case "30610-06.html":
			case "30610-07.html":
			case "30610-08.html":
			case "30610-09.html":
			case "30610-13.html":
			case "30611-02.html":
			case "30611-03.html":
			case "30611-06.html":
			case "30612-03.html":
			{
				htmltext = event;
				break;
			}
			case "30461-10.html":
			{
				if (hasQuestItems(player, MIRIENS_2ND_SIGIL, SYMBOL_OF_JUREK))
				{
					takeItems(player, MIRIENS_2ND_SIGIL, 1);
					giveItems(player, MIRIENS_3RD_SIGIL, 1);
					takeItems(player, SYMBOL_OF_JUREK, 1);
					qs.setCond(19, true);
					htmltext = event;
				}
				break;
			}
			case "30070-02.html":
			{
				giveItems(player, HIGH_PRIESTS_SIGIL, 1);
				giveItems(player, SYLVAINS_LETTER, 1);
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30071-04.html":
			{
				if (hasQuestItems(player, CRERAS_PAINTING2))
				{
					takeItems(player, CRERAS_PAINTING2, 1);
					giveItems(player, CRERAS_PAINTING3, 1);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "30103-04.html":
			{
				giveItems(player, VALKONS_REQUEST, 1);
				htmltext = event;
				break;
			}
			case "30111-05.html":
			{
				if (hasQuestItems(player, CRONOS_LETTER))
				{
					takeItems(player, CRONOS_LETTER, 1);
					giveItems(player, DIETERS_KEY, 1);
					qs.setCond(21, true);
					htmltext = event;
				}
				break;
			}
			case "30111-09.html":
			{
				if (hasQuestItems(player, CRETAS_2ND_LETTER))
				{
					takeItems(player, CRETAS_2ND_LETTER, 1);
					giveItems(player, DIETERS_LETTER, 1);
					giveItems(player, DIETERS_DIARY, 1);
					qs.setCond(23, true);
					htmltext = event;
				}
				break;
			}
			case "30115-03.html":
			{
				giveItems(player, JUREKS_LIST, 1);
				giveItems(player, GRAND_MAGISTER_SIGIL, 1);
				qs.setCond(16, true);
				htmltext = event;
				break;
			}
			case "30230-02.html":
			{
				if (hasQuestItems(player, DIETERS_LETTER))
				{
					takeItems(player, DIETERS_LETTER, 1);
					giveItems(player, RAUTS_LETTER_ENVELOPE, 1);
					qs.setCond(24, true);
					htmltext = event;
				}
				break;
			}
			case "30316-02.html":
			{
				if (hasQuestItems(player, RAUTS_LETTER_ENVELOPE))
				{
					takeItems(player, RAUTS_LETTER_ENVELOPE, 1);
					giveItems(player, SCRIPTURE_CHAPTER_1, 1);
					giveItems(player, STRONG_LIGUOR, 1);
					qs.setCond(25, true);
					htmltext = event;
				}
				break;
			}
			case "30608-02.html":
			{
				if (hasQuestItems(player, SYLVAINS_LETTER))
				{
					giveItems(player, MARIAS_1ST_LETTER, 1);
					takeItems(player, SYLVAINS_LETTER, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30608-08.html":
			{
				if (hasQuestItems(player, CRETAS_1ST_LETTER))
				{
					giveItems(player, LUCILLAS_HANDBAG, 1);
					takeItems(player, CRETAS_1ST_LETTER, 1);
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "30608-14.html":
			{
				if (hasQuestItems(player, CRERAS_PAINTING3))
				{
					takeItems(player, CRERAS_PAINTING3, 1);
					takeItems(player, BROWN_SCROLL_SCRAP, -1);
					giveItems(player, CRYSTAL_OF_PURITY1, 1);
					qs.setCond(13, true);
					htmltext = event;
				}
				break;
			}
			case "30609-05.html":
			{
				if (hasQuestItems(player, MARIAS_2ND_LETTER))
				{
					takeItems(player, MARIAS_2ND_LETTER, 1);
					giveItems(player, CRETAS_1ST_LETTER, 1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30609-09.html":
			{
				if (hasQuestItems(player, LUCILLAS_HANDBAG))
				{
					takeItems(player, LUCILLAS_HANDBAG, 1);
					giveItems(player, CRERAS_PAINTING1, 1);
					qs.setCond(8, true);
					htmltext = event;
				}
				break;
			}
			case "30609-14.html":
			{
				if (hasQuestItems(player, DIETERS_KEY))
				{
					takeItems(player, DIETERS_KEY, 1);
					giveItems(player, CRETAS_2ND_LETTER, 1);
					qs.setCond(22, true);
					htmltext = event;
				}
				break;
			}
			case "30610-10.html":
			{
				giveItems(player, CRONOS_SIGIL, 1);
				giveItems(player, CRONOS_LETTER, 1);
				qs.setCond(20, true);
				htmltext = event;
				break;
			}
			case "30610-14.html":
			{
				if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3, SCRIPTURE_CHAPTER_4))
				{
					takeItems(player, CRONOS_SIGIL, 1);
					takeItems(player, DIETERS_DIARY, 1);
					takeItems(player, TRIFFS_RING, 1);
					takeItems(player, SCRIPTURE_CHAPTER_1, 1);
					takeItems(player, SCRIPTURE_CHAPTER_2, 1);
					takeItems(player, SCRIPTURE_CHAPTER_3, 1);
					takeItems(player, SCRIPTURE_CHAPTER_4, 1);
					giveItems(player, SYMBOL_OF_CRONOS, 1);
					qs.setCond(31, true);
					htmltext = event;
				}
				break;
			}
			case "30611-04.html":
			{
				if (hasQuestItems(player, STRONG_LIGUOR))
				{
					giveItems(player, TRIFFS_RING, 1);
					takeItems(player, STRONG_LIGUOR, 1);
					qs.setCond(26, true);
					htmltext = event;
				}
				break;
			}
			case "30612-04.html":
			{
				giveItems(player, CASIANS_LIST, 1);
				qs.setCond(28, true);
				htmltext = event;
				break;
			}
			case "30612-07.html":
			{
				giveItems(player, SCRIPTURE_CHAPTER_4, 1);
				takeItems(player, POITANS_NOTES, 1);
				takeItems(player, CASIANS_LIST, 1);
				takeItems(player, GHOULS_SKIN, -1);
				takeItems(player, MEDUSAS_BLOOD, -1);
				takeItems(player, FETTERED_SOULS_ICHOR, -1);
				takeItems(player, ENCHANTED_GARGOYLES_NAIL, -1);
				qs.setCond(30, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case MONSTER_EYE_DESTREOYER:
				{
					if (hasQuestItems(killer, MIRIENS_2ND_SIGIL, GRAND_MAGISTER_SIGIL, JUREKS_LIST) && (getQuestItemsCount(killer, MONSTER_EYE_DESTROYER_SKIN) < 5))
					{
						giveItems(killer, MONSTER_EYE_DESTROYER_SKIN, 1);
						if ((getQuestItemsCount(killer, MONSTER_EYE_DESTROYER_SKIN) == 5) && (getQuestItemsCount(killer, SHAMANS_NECKLACE) >= 5) && (getQuestItemsCount(killer, SHACKLES_SCALP) >= 2))
						{
							qs.setCond(17, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MEDUSA:
				{
					if (hasQuestItems(killer, TRIFFS_RING, POITANS_NOTES, CASIANS_LIST) && (getQuestItemsCount(killer, MEDUSAS_BLOOD) < 12))
					{
						giveItems(killer, MEDUSAS_BLOOD, 1);
						if (getQuestItemsCount(killer, MEDUSAS_BLOOD) == 12)
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
				case GHOUL:
				{
					if (hasQuestItems(killer, TRIFFS_RING, POITANS_NOTES, CASIANS_LIST) && (getQuestItemsCount(killer, GHOULS_SKIN) < 10))
					{
						giveItems(killer, GHOULS_SKIN, 1);
						if (getQuestItemsCount(killer, GHOULS_SKIN) == 10)
						{
							qs.setCond(29, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case SHACKLE1:
				case SHACKLE2:
				{
					if (hasQuestItems(killer, MIRIENS_2ND_SIGIL, GRAND_MAGISTER_SIGIL, JUREKS_LIST) && (getQuestItemsCount(killer, SHACKLES_SCALP) < 2))
					{
						giveItems(killer, SHACKLES_SCALP, 1);
						if ((getQuestItemsCount(killer, MONSTER_EYE_DESTROYER_SKIN) >= 5) && (getQuestItemsCount(killer, SHAMANS_NECKLACE) >= 5) && (getQuestItemsCount(killer, SHACKLES_SCALP) == 2))
						{
							qs.setCond(17, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BREKA_ORC_SHAMAN:
				{
					if (hasQuestItems(killer, MIRIENS_2ND_SIGIL, GRAND_MAGISTER_SIGIL, JUREKS_LIST) && (getQuestItemsCount(killer, SHAMANS_NECKLACE) < 5))
					{
						giveItems(killer, SHAMANS_NECKLACE, 1);
						if ((getQuestItemsCount(killer, MONSTER_EYE_DESTROYER_SKIN) >= 5) && (getQuestItemsCount(killer, SHAMANS_NECKLACE) == 5) && (getQuestItemsCount(killer, SHACKLES_SCALP) >= 2))
						{
							qs.setCond(17, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case FETTERED_SOUL:
				{
					if (hasQuestItems(killer, TRIFFS_RING, POITANS_NOTES, CASIANS_LIST) && (getQuestItemsCount(killer, FETTERED_SOULS_ICHOR) < 5))
					{
						giveItems(killer, FETTERED_SOULS_ICHOR, 1);
						if (getQuestItemsCount(killer, FETTERED_SOULS_ICHOR) >= 5)
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
				case GRANDIS:
				{
					if (hasQuestItems(killer, MIRIENS_3RD_SIGIL, CRONOS_SIGIL, TRIFFS_RING) && !hasQuestItems(killer, SCRIPTURE_CHAPTER_3))
					{
						giveItems(killer, SCRIPTURE_CHAPTER_3, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					break;
				}
				case ENCHANTED_GARGOYLE:
				{
					if (hasQuestItems(killer, TRIFFS_RING, POITANS_NOTES, CASIANS_LIST) && (getQuestItemsCount(killer, ENCHANTED_GARGOYLES_NAIL) < 5))
					{
						giveItems(killer, ENCHANTED_GARGOYLES_NAIL, 1);
						if (getQuestItemsCount(killer, ENCHANTED_GARGOYLES_NAIL) >= 5)
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
				case LETO_LIZARDMAN_WARRIOR:
				{
					if (hasQuestItems(killer, MIRIENS_1ST_SIGIL, HIGH_PRIESTS_SIGIL, CRERAS_PAINTING3) && (getQuestItemsCount(killer, BROWN_SCROLL_SCRAP) < 5))
					{
						giveItems(killer, BROWN_SCROLL_SCRAP, 1);
						if (getQuestItemsCount(killer, BROWN_SCROLL_SCRAP) == 5)
						{
							qs.setCond(12, true);
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
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == MAGISTER_MIRIEN)
			{
				if ((player.getClassId() == ClassId.WIZARD) || (player.getClassId() == ClassId.ELVEN_WIZARD) || ((player.getClassId() == ClassId.DARK_WIZARD)))
				{
					if (player.getLevel() < MIN_LVL)
					{
						htmltext = "30461-02.html";
					}
					else
					{
						htmltext = "30461-03.htm";
					}
				}
				else
				{
					htmltext = "30461-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MAGISTER_MIRIEN:
				{
					if (hasQuestItems(player, MIRIENS_1ST_SIGIL))
					{
						if (!hasQuestItems(player, SYMBOL_OF_SYLVAIN))
						{
							htmltext = "30461-05.html";
						}
						else
						{
							takeItems(player, MIRIENS_1ST_SIGIL, 1);
							giveItems(player, MIRIENS_2ND_SIGIL, 1);
							takeItems(player, SYMBOL_OF_SYLVAIN, 1);
							qs.setCond(15, true);
							htmltext = "30461-06.html";
						}
					}
					else if (hasQuestItems(player, MIRIENS_2ND_SIGIL))
					{
						if (!hasQuestItems(player, SYMBOL_OF_JUREK))
						{
							htmltext = "30461-07.html";
						}
						else
						{
							htmltext = "30461-08.html";
						}
					}
					else if (hasQuestItems(player, MIRIENS_INSTRUCTION))
					{
						if (player.getLevel() < LEVEL)
						{
							htmltext = "30461-11.html";
						}
						else
						{
							takeItems(player, MIRIENS_INSTRUCTION, 1);
							giveItems(player, MIRIENS_3RD_SIGIL, 1);
							qs.setCond(19, true);
							htmltext = "30461-12.html";
						}
					}
					else if (hasQuestItems(player, MIRIENS_3RD_SIGIL))
					{
						if (!hasQuestItems(player, SYMBOL_OF_CRONOS))
						{
							htmltext = "30461-13.html";
						}
						else
						{
							giveAdena(player, 319628, true);
							giveItems(player, MARK_OF_SCHOLAR, 1);
							addExpAndSp(player, 1753926, 113754);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30461-14.html";
						}
					}
					break;
				}
				case HIGH_PRIEST_SYLVAIN:
				{
					if (hasQuestItems(player, MIRIENS_1ST_SIGIL) && !hasAtLeastOneQuestItem(player, HIGH_PRIESTS_SIGIL, SYMBOL_OF_SYLVAIN))
					{
						htmltext = "30070-01.html";
					}
					else if (!hasQuestItems(player, CRYSTAL_OF_PURITY1) && hasQuestItems(player, HIGH_PRIESTS_SIGIL, MIRIENS_1ST_SIGIL))
					{
						htmltext = "30070-03.html";
					}
					else if (hasQuestItems(player, HIGH_PRIESTS_SIGIL, MIRIENS_1ST_SIGIL, CRYSTAL_OF_PURITY1))
					{
						takeItems(player, CRYSTAL_OF_PURITY1, 1);
						takeItems(player, HIGH_PRIESTS_SIGIL, 1);
						giveItems(player, SYMBOL_OF_SYLVAIN, 1);
						qs.setCond(14, true);
						htmltext = "30070-04.html";
					}
					else if (hasQuestItems(player, MIRIENS_1ST_SIGIL, SYMBOL_OF_SYLVAIN) && !hasQuestItems(player, HIGH_PRIESTS_SIGIL))
					{
						htmltext = "30070-05.html";
					}
					else if (hasAtLeastOneQuestItem(player, MIRIENS_2ND_SIGIL, MIRIENS_3RD_SIGIL))
					{
						htmltext = "30070-06.html";
					}
					break;
				}
				case CAPTAIN_LUCAS:
				{
					if (hasQuestItems(player, MIRIENS_1ST_SIGIL, HIGH_PRIESTS_SIGIL))
					{
						if (hasQuestItems(player, MARIAS_1ST_LETTER))
						{
							takeItems(player, MARIAS_1ST_LETTER, 1);
							giveItems(player, LUCASS_LETTER, 1);
							qs.setCond(4, true);
							htmltext = "30071-01.html";
						}
						else if (hasAtLeastOneQuestItem(player, MARIAS_2ND_LETTER, CRETAS_1ST_LETTER, LUCILLAS_HANDBAG, CRERAS_PAINTING1, LUCASS_LETTER))
						{
							htmltext = "30071-02.html";
						}
						else if (hasQuestItems(player, CRERAS_PAINTING2))
						{
							htmltext = "30071-03.html";
						}
						else if (hasQuestItems(player, CRERAS_PAINTING3))
						{
							if (getQuestItemsCount(player, BROWN_SCROLL_SCRAP) < 5)
							{
								htmltext = "30071-05.html";
							}
							else
							{
								htmltext = "30071-06.html";
							}
						}
					}
					else if (hasAtLeastOneQuestItem(player, SYMBOL_OF_SYLVAIN, MIRIENS_2ND_SIGIL, MIRIENS_3RD_SIGIL, CRYSTAL_OF_PURITY1))
					{
						htmltext = "30071-07.html";
					}
					break;
				}
				case WAREHOUSE_KEEPER_VALKON:
				{
					if (hasQuestItems(player, TRIFFS_RING))
					{
						if (!hasAtLeastOneQuestItem(player, VALKONS_REQUEST, CRYSTAL_OF_PURITY2, SCRIPTURE_CHAPTER_2))
						{
							htmltext = "30103-01.html";
						}
						else if (hasQuestItems(player, VALKONS_REQUEST) && !hasAtLeastOneQuestItem(player, CRYSTAL_OF_PURITY2, SCRIPTURE_CHAPTER_2))
						{
							htmltext = "30103-05.html";
						}
						else if (hasQuestItems(player, CRYSTAL_OF_PURITY2) && !hasAtLeastOneQuestItem(player, VALKONS_REQUEST, SCRIPTURE_CHAPTER_2))
						{
							giveItems(player, SCRIPTURE_CHAPTER_2, 1);
							takeItems(player, CRYSTAL_OF_PURITY2, 1);
							htmltext = "30103-06.html";
						}
						else if (hasQuestItems(player, SCRIPTURE_CHAPTER_2) && !hasAtLeastOneQuestItem(player, VALKONS_REQUEST, CRYSTAL_OF_PURITY2))
						{
							htmltext = "30103-07.html";
						}
					}
					break;
				}
				case MAGISTER_DIETER:
				{
					if (hasQuestItems(player, MIRIENS_3RD_SIGIL, CRONOS_SIGIL))
					{
						if (hasQuestItems(player, CRONOS_LETTER))
						{
							htmltext = "30111-01.html";
						}
						else if (hasQuestItems(player, DIETERS_KEY))
						{
							htmltext = "30111-06.html";
						}
						else if (hasQuestItems(player, CRETAS_2ND_LETTER))
						{
							htmltext = "30111-07.html";
						}
						else if (hasQuestItems(player, DIETERS_DIARY, DIETERS_LETTER))
						{
							htmltext = "30111-10.html";
						}
						else if (hasQuestItems(player, DIETERS_DIARY, RAUTS_LETTER_ENVELOPE))
						{
							htmltext = "30111-11.html";
						}
						else if (hasQuestItems(player, DIETERS_DIARY) && !hasAtLeastOneQuestItem(player, DIETERS_LETTER, RAUTS_LETTER_ENVELOPE))
						{
							if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3, SCRIPTURE_CHAPTER_4))
							{
								htmltext = "30111-13.html";
							}
							else
							{
								htmltext = "30111-12.html";
							}
						}
					}
					else if (hasQuestItems(player, SYMBOL_OF_CRONOS))
					{
						htmltext = "30111-15.html";
					}
					break;
				}
				case GRAND_MAGISTER_JUREK:
				{
					if (hasQuestItems(player, MIRIENS_2ND_SIGIL))
					{
						if (!hasAtLeastOneQuestItem(player, GRAND_MAGISTER_SIGIL, SYMBOL_OF_JUREK))
						{
							htmltext = "30115-01.html";
						}
						else if (hasQuestItems(player, JUREKS_LIST))
						{
							if ((getQuestItemsCount(player, MONSTER_EYE_DESTROYER_SKIN) + getQuestItemsCount(player, SHAMANS_NECKLACE) + getQuestItemsCount(player, SHACKLES_SCALP)) < 12)
							{
								htmltext = "30115-04.html";
							}
							else
							{
								takeItems(player, GRAND_MAGISTER_SIGIL, 1);
								takeItems(player, JUREKS_LIST, 1);
								takeItems(player, MONSTER_EYE_DESTROYER_SKIN, -1);
								takeItems(player, SHAMANS_NECKLACE, -1);
								takeItems(player, SHACKLES_SCALP, -1);
								giveItems(player, SYMBOL_OF_JUREK, 1);
								qs.setCond(18, true);
								htmltext = "30115-05.html";
							}
						}
						else if (hasQuestItems(player, SYMBOL_OF_JUREK) && !hasQuestItems(player, GRAND_MAGISTER_SIGIL))
						{
							htmltext = "30115-06.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, MIRIENS_1ST_SIGIL, MIRIENS_3RD_SIGIL))
					{
						htmltext = "30115-07.html";
					}
					break;
				}
				case TRADER_EDROC:
				{
					if (hasQuestItems(player, DIETERS_DIARY))
					{
						if (hasQuestItems(player, DIETERS_LETTER))
						{
							htmltext = "30230-01.html";
						}
						else if (hasQuestItems(player, RAUTS_LETTER_ENVELOPE))
						{
							htmltext = "30230-03.html";
						}
						else if (hasAtLeastOneQuestItem(player, STRONG_LIGUOR, TRIFFS_RING))
						{
							htmltext = "30230-04.html";
						}
					}
					break;
				}
				case WAREHOUSE_KEEPER_RAUT:
				{
					if (hasQuestItems(player, DIETERS_DIARY))
					{
						if (hasQuestItems(player, RAUTS_LETTER_ENVELOPE))
						{
							htmltext = "30316-01.html";
						}
						else if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, STRONG_LIGUOR))
						{
							htmltext = "30316-04.html";
						}
						else if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, TRIFFS_RING))
						{
							htmltext = "30316-05.html";
						}
					}
					break;
				}
				case BLACKSMITH_POITAN:
				{
					if (hasQuestItems(player, TRIFFS_RING))
					{
						if (!hasAtLeastOneQuestItem(player, POITANS_NOTES, CASIANS_LIST, SCRIPTURE_CHAPTER_4))
						{
							giveItems(player, POITANS_NOTES, 1);
							htmltext = "30458-01.html";
						}
						else if (hasQuestItems(player, POITANS_NOTES) && !hasAtLeastOneQuestItem(player, CASIANS_LIST, SCRIPTURE_CHAPTER_4))
						{
							htmltext = "30458-02.html";
						}
						else if (hasQuestItems(player, POITANS_NOTES, CASIANS_LIST) && !hasQuestItems(player, SCRIPTURE_CHAPTER_4))
						{
							htmltext = "30458-03.html";
						}
						else if (hasQuestItems(player, SCRIPTURE_CHAPTER_4) && !hasAtLeastOneQuestItem(player, POITANS_NOTES, CASIANS_LIST))
						{
							htmltext = "30458-04.html";
						}
					}
					break;
				}
				case MARIA:
				{
					if (hasQuestItems(player, MIRIENS_1ST_SIGIL, HIGH_PRIESTS_SIGIL))
					{
						if (hasQuestItems(player, SYLVAINS_LETTER))
						{
							htmltext = "30608-01.html";
						}
						else if (hasQuestItems(player, MARIAS_1ST_LETTER))
						{
							htmltext = "30608-03.html";
						}
						else if (hasQuestItems(player, LUCASS_LETTER))
						{
							giveItems(player, MARIAS_2ND_LETTER, 1);
							takeItems(player, LUCASS_LETTER, 1);
							qs.setCond(5, true);
							htmltext = "30608-04.html";
						}
						else if (hasQuestItems(player, MARIAS_2ND_LETTER))
						{
							htmltext = "30608-05.html";
						}
						else if (hasQuestItems(player, CRETAS_1ST_LETTER))
						{
							htmltext = "30608-06.html";
						}
						else if (hasQuestItems(player, LUCILLAS_HANDBAG))
						{
							htmltext = "30608-09.html";
						}
						else if (hasQuestItems(player, CRERAS_PAINTING1))
						{
							takeItems(player, CRERAS_PAINTING1, 1);
							giveItems(player, CRERAS_PAINTING2, 1);
							qs.setCond(9, true);
							htmltext = "30608-10.html";
						}
						else if (hasQuestItems(player, CRERAS_PAINTING2))
						{
							htmltext = "30608-11.html";
						}
						else if (hasQuestItems(player, CRERAS_PAINTING3))
						{
							if (getQuestItemsCount(player, BROWN_SCROLL_SCRAP) < 5)
							{
								qs.setCond(11, true);
								htmltext = "30608-12.html";
							}
							else
							{
								htmltext = "30608-13.html";
							}
						}
						else if (hasQuestItems(player, CRYSTAL_OF_PURITY1))
						{
							htmltext = "30608-15.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, SYMBOL_OF_SYLVAIN, MIRIENS_2ND_SIGIL))
					{
						htmltext = "30608-16.html";
					}
					else if (hasQuestItems(player, MIRIENS_3RD_SIGIL))
					{
						if (!hasQuestItems(player, VALKONS_REQUEST))
						{
							htmltext = "30608-17.html";
						}
						else
						{
							takeItems(player, VALKONS_REQUEST, 1);
							giveItems(player, CRYSTAL_OF_PURITY2, 1);
							htmltext = "30608-18.html";
						}
					}
					break;
				}
				case ASTROLOGER_CRETA:
				{
					if (hasQuestItems(player, MIRIENS_1ST_SIGIL, HIGH_PRIESTS_SIGIL))
					{
						if (hasQuestItems(player, MARIAS_2ND_LETTER))
						{
							htmltext = "30609-01.html";
						}
						else if (hasQuestItems(player, CRETAS_1ST_LETTER))
						{
							htmltext = "30609-06.html";
						}
						else if (hasQuestItems(player, LUCILLAS_HANDBAG))
						{
							htmltext = "30609-07.html";
						}
						else if (hasAtLeastOneQuestItem(player, CRERAS_PAINTING1, CRERAS_PAINTING2, CRERAS_PAINTING3))
						{
							htmltext = "30609-10.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, CRYSTAL_OF_PURITY1, SYMBOL_OF_SYLVAIN, MIRIENS_2ND_SIGIL))
					{
						htmltext = "30609-11.html";
					}
					else if (hasQuestItems(player, MIRIENS_3RD_SIGIL))
					{
						if (hasQuestItems(player, DIETERS_KEY))
						{
							htmltext = "30609-12.html";
						}
						else
						{
							htmltext = "30609-15.html";
						}
					}
					break;
				}
				case ELDER_CRONOS:
				{
					if (hasQuestItems(player, MIRIENS_3RD_SIGIL))
					{
						if (!hasAtLeastOneQuestItem(player, CRONOS_SIGIL, SYMBOL_OF_CRONOS))
						{
							htmltext = "30610-01.html";
						}
						else if (hasQuestItems(player, CRONOS_SIGIL))
						{
							if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3, SCRIPTURE_CHAPTER_4))
							{
								htmltext = "30610-12.html";
							}
							else
							{
								htmltext = "30610-11.html";
							}
						}
						else if (hasQuestItems(player, SYMBOL_OF_CRONOS) && !hasQuestItems(player, CRONOS_SIGIL))
						{
							htmltext = "30610-15.html";
						}
					}
					break;
				}
				case DRUNKARD_TRIFF:
				{
					if (hasQuestItems(player, DIETERS_DIARY, SCRIPTURE_CHAPTER_1, STRONG_LIGUOR))
					{
						htmltext = "30611-01.html";
					}
					else if (hasAtLeastOneQuestItem(player, TRIFFS_RING, SYMBOL_OF_CRONOS))
					{
						htmltext = "30611-05.html";
					}
					break;
				}
				case ELDER_CASIAN:
				{
					if (hasQuestItems(player, TRIFFS_RING, POITANS_NOTES))
					{
						if (!hasQuestItems(player, CASIANS_LIST))
						{
							if (hasQuestItems(player, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3))
							{
								htmltext = "30612-02.html";
							}
							else
							{
								htmltext = "30612-01.html";
							}
						}
						else
						{
							if ((getQuestItemsCount(player, GHOULS_SKIN) + getQuestItemsCount(player, MEDUSAS_BLOOD) + getQuestItemsCount(player, FETTERED_SOULS_ICHOR) + getQuestItemsCount(player, ENCHANTED_GARGOYLES_NAIL)) < 32)
							{
								htmltext = "30612-05.html";
							}
							else
							{
								htmltext = "30612-06.html";
							}
						}
					}
					else if (hasQuestItems(player, TRIFFS_RING, SCRIPTURE_CHAPTER_1, SCRIPTURE_CHAPTER_2, SCRIPTURE_CHAPTER_3, SCRIPTURE_CHAPTER_4) && !hasAtLeastOneQuestItem(player, POITANS_NOTES, CASIANS_LIST))
					{
						htmltext = "30612-08.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MAGISTER_MIRIEN)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
