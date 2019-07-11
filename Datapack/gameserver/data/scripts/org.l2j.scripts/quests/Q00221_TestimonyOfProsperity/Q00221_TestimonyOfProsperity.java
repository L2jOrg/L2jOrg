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
package quests.Q00221_TestimonyOfProsperity;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Testimony Of Prosperity (221)
 * @author ivantotov
 */
public final class Q00221_TestimonyOfProsperity extends Quest
{
	// NPCs
	private static final int WAREHOUSE_KEEPER_WILFORD = 30005;
	private static final int WAREHOUSE_KEEPER_PARMAN = 30104;
	private static final int LILITH = 30368;
	private static final int GUARD_BRIGHT = 30466;
	private static final int TRADER_SHARI = 30517;
	private static final int TRADER_MION = 30519;
	private static final int IRON_GATES_LOCKIRIN = 30531;
	private static final int GOLDEN_WHEELS_SPIRON = 30532;
	private static final int SILVER_SCALES_BALANKI = 30533;
	private static final int BRONZE_KEYS_KEEF = 30534;
	private static final int GRAY_PILLAR_MEMBER_FILAUR = 30535;
	private static final int BLACK_ANVILS_ARIN = 30536;
	private static final int MARYSE_REDBONNET = 30553;
	private static final int MINER_BOLTER = 30554;
	private static final int CARRIER_TOROCCO = 30555;
	private static final int MASTER_TOMA = 30556;
	private static final int PIOTUR = 30597;
	private static final int EMILY = 30620;
	private static final int MAESTRO_NIKOLA = 30621;
	private static final int BOX_OF_TITAN = 30622;
	// Items
	private static final int ADENA = 57;
	private static final int ANIMAL_SKIN = 1867;
	private static final int RECIPE_TITAN_KEY = 3023;
	private static final int KEY_OF_TITAN = 3030;
	private static final int RING_OF_TESTIMONY_1ST = 3239;
	private static final int RING_OF_TESTIMONY_2ND = 3240;
	private static final int OLD_ACCOUNT_BOOK = 3241;
	private static final int BLESSED_SEED = 3242;
	private static final int EMILYS_RECIPE = 3243;
	private static final int LILITHS_ELVEN_WAFER = 3244;
	private static final int MAPHR_TABLET_FRAGMENT = 3245;
	private static final int COLLECTION_LICENSE = 3246;
	private static final int LOCKIRINS_1ST_NOTICE = 3247;
	private static final int LOCKIRINS_2ND_NOTICE = 3248;
	private static final int LOCKIRINS_3RD_NOTICE = 3249;
	private static final int LOCKIRINS_4TH_NOTICE = 3250;
	private static final int LOCKIRINS_5TH_NOTICE = 3251;
	private static final int CONTRIBUTION_OF_SHARI = 3252;
	private static final int CONTRIBUTION_OF_MION = 3253;
	private static final int CONTRIBUTION_OF_MARYSE = 3254;
	private static final int MARYSES_REQUEST = 3255;
	private static final int CONTRIBUTION_OF_TOMA = 3256;
	private static final int RECEIPT_OF_BOLTER = 3257;
	private static final int RECEIPT_OF_CONTRIBUTION_1ST = 3258;
	private static final int RECEIPT_OF_CONTRIBUTION_2ND = 3259;
	private static final int RECEIPT_OF_CONTRIBUTION_3RD = 3260;
	private static final int RECEIPT_OF_CONTRIBUTION_4TH = 3261;
	private static final int RECEIPT_OF_CONTRIBUTION_5TH = 3262;
	private static final int PROCURATION_OF_TOROCCO = 3263;
	private static final int BRIGHTS_LIST = 3264;
	private static final int MANDRAGORA_PETAL = 3265;
	private static final int CRIMSON_MOSS = 3266;
	private static final int MANDRAGORA_BOUGUET = 3267;
	private static final int PARMANS_INSTRUCTIONS = 3268;
	private static final int PARMANS_LETTER = 3269;
	private static final int CLAY_DOUGH = 3270;
	private static final int PATTERN_OF_KEYHOLE = 3271;
	private static final int NIKOLAS_LIST = 3272;
	private static final int STAKATO_SHELL = 3273;
	private static final int TOAD_LORD_SAC = 3274;
	private static final int MARSH_SPIDER_THORN = 3275;
	private static final int CRYSTAL_BROOCH = 3428;
	// Reward
	private static final int MARK_OF_PROSPERITY = 3238;
	// Monster
	private static final int MANDRAGORA_SPROUT1 = 20154;
	private static final int MANDRAGORA_SAPLING = 20155;
	private static final int MANDRAGORA_BLOSSOM = 20156;
	private static final int MARSH_STAKATO = 20157;
	private static final int MANDRAGORA_SPROUT2 = 20223;
	private static final int GIANT_CRIMSON_ANT = 20228;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int TOAD_LORD = 20231;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_SPIDER = 20233;
	private static final int MARSH_STAKATO_DRONE = 20234;
	// Misc
	private static final int MIN_LEVEL = 37;
	
	public Q00221_TestimonyOfProsperity()
	{
		super(221);
		addStartNpc(WAREHOUSE_KEEPER_PARMAN);
		addTalkId(WAREHOUSE_KEEPER_PARMAN, WAREHOUSE_KEEPER_WILFORD, LILITH, GUARD_BRIGHT, TRADER_SHARI, TRADER_MION, IRON_GATES_LOCKIRIN, GOLDEN_WHEELS_SPIRON, SILVER_SCALES_BALANKI, BRONZE_KEYS_KEEF, GRAY_PILLAR_MEMBER_FILAUR, BLACK_ANVILS_ARIN, MARYSE_REDBONNET, MINER_BOLTER, CARRIER_TOROCCO, MASTER_TOMA, PIOTUR, EMILY, MAESTRO_NIKOLA, BOX_OF_TITAN);
		addKillId(MANDRAGORA_SPROUT1, MANDRAGORA_SAPLING, MANDRAGORA_BLOSSOM, MARSH_STAKATO, MANDRAGORA_SPROUT2, GIANT_CRIMSON_ANT, MARSH_STAKATO_WORKER, TOAD_LORD, MARSH_STAKATO_SOLDIER, MARSH_SPIDER, MARSH_STAKATO_DRONE);
		registerQuestItems(RECIPE_TITAN_KEY, KEY_OF_TITAN, RING_OF_TESTIMONY_1ST, RING_OF_TESTIMONY_2ND, OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILYS_RECIPE, LILITHS_ELVEN_WAFER, MAPHR_TABLET_FRAGMENT, COLLECTION_LICENSE, LOCKIRINS_1ST_NOTICE, LOCKIRINS_2ND_NOTICE, LOCKIRINS_3RD_NOTICE, LOCKIRINS_4TH_NOTICE, LOCKIRINS_5TH_NOTICE, CONTRIBUTION_OF_SHARI, CONTRIBUTION_OF_MION, CONTRIBUTION_OF_MARYSE, MARYSES_REQUEST, CONTRIBUTION_OF_TOMA, RECEIPT_OF_BOLTER, RECEIPT_OF_CONTRIBUTION_1ST, RECEIPT_OF_CONTRIBUTION_2ND, RECEIPT_OF_CONTRIBUTION_3RD, RECEIPT_OF_CONTRIBUTION_4TH, RECEIPT_OF_CONTRIBUTION_5TH, PROCURATION_OF_TOROCCO, BRIGHTS_LIST, MANDRAGORA_PETAL, CRIMSON_MOSS, MANDRAGORA_BOUGUET, PARMANS_INSTRUCTIONS, PARMANS_LETTER, CLAY_DOUGH, PATTERN_OF_KEYHOLE, NIKOLAS_LIST, STAKATO_SHELL, TOAD_LORD_SAC, MARSH_SPIDER_THORN, CRYSTAL_BROOCH);
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
					if (!hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						giveItems(player, RING_OF_TESTIMONY_1ST, 1);
					}
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30104-08.html":
			{
				takeItems(player, RING_OF_TESTIMONY_1ST, 1);
				giveItems(player, RING_OF_TESTIMONY_2ND, 1);
				takeItems(player, OLD_ACCOUNT_BOOK, 1);
				takeItems(player, BLESSED_SEED, 1);
				takeItems(player, EMILYS_RECIPE, 1);
				takeItems(player, LILITHS_ELVEN_WAFER, 1);
				giveItems(player, PARMANS_LETTER, 1);
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "30104-04a.html":
			case "30104-04b.html":
			case "30104-04c.html":
			case "30104-04d.html":
			case "30104-05.html":
			case "30104-08a.html":
			case "30104-08b.html":
			case "30104-08c.html":
			case "30005-02.html":
			case "30005-03.html":
			case "30368-02.html":
			case "30466-02.html":
			case "30531-02.html":
			case "30620-02.html":
			case "30621-02.html":
			case "30621-03.html":
			{
				htmltext = event;
				break;
			}
			case "30005-04.html":
			{
				giveItems(player, CRYSTAL_BROOCH, 1);
				htmltext = event;
				break;
			}
			case "30368-03.html":
			{
				if (hasQuestItems(player, CRYSTAL_BROOCH))
				{
					giveItems(player, LILITHS_ELVEN_WAFER, 1);
					takeItems(player, CRYSTAL_BROOCH, 1);
					if (hasQuestItems(player, OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILYS_RECIPE))
					{
						qs.setCond(2, true);
					}
					htmltext = event;
				}
				break;
			}
			case "30466-03.html":
			{
				giveItems(player, BRIGHTS_LIST, 1);
				htmltext = event;
				break;
			}
			case "30531-03.html":
			{
				giveItems(player, COLLECTION_LICENSE, 1);
				giveItems(player, LOCKIRINS_1ST_NOTICE, 1);
				giveItems(player, LOCKIRINS_2ND_NOTICE, 1);
				giveItems(player, LOCKIRINS_3RD_NOTICE, 1);
				giveItems(player, LOCKIRINS_4TH_NOTICE, 1);
				giveItems(player, LOCKIRINS_5TH_NOTICE, 1);
				htmltext = event;
				break;
			}
			case "30534-03a.html":
			{
				if (getQuestItemsCount(player, ADENA) < 5000)
				{
					htmltext = event;
				}
				else if (hasQuestItems(player, PROCURATION_OF_TOROCCO))
				{
					takeItems(player, ADENA, 5000);
					giveItems(player, RECEIPT_OF_CONTRIBUTION_3RD, 1);
					takeItems(player, PROCURATION_OF_TOROCCO, 1);
					htmltext = "30534-03b.html";
				}
				break;
			}
			case "30555-02.html":
			{
				giveItems(player, PROCURATION_OF_TOROCCO, 1);
				htmltext = event;
				break;
			}
			case "30597-02.html":
			{
				giveItems(player, BLESSED_SEED, 1);
				if (hasQuestItems(player, OLD_ACCOUNT_BOOK, EMILYS_RECIPE, LILITHS_ELVEN_WAFER))
				{
					qs.setCond(2, true);
				}
				htmltext = event;
				break;
			}
			case "30620-03.html":
			{
				if (hasQuestItems(player, MANDRAGORA_BOUGUET))
				{
					giveItems(player, EMILYS_RECIPE, 1);
					takeItems(player, MANDRAGORA_BOUGUET, 1);
					if (hasQuestItems(player, OLD_ACCOUNT_BOOK, BLESSED_SEED, LILITHS_ELVEN_WAFER))
					{
						qs.setCond(2, true);
					}
					htmltext = event;
				}
				break;
			}
			case "30621-04.html":
			{
				giveItems(player, CLAY_DOUGH, 1);
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30622-02.html":
			{
				if (hasQuestItems(player, CLAY_DOUGH))
				{
					takeItems(player, CLAY_DOUGH, 1);
					giveItems(player, PATTERN_OF_KEYHOLE, 1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30622-04.html":
			{
				if (hasQuestItems(player, KEY_OF_TITAN))
				{
					takeItems(player, KEY_OF_TITAN, 1);
					giveItems(player, MAPHR_TABLET_FRAGMENT, 1);
					takeItems(player, NIKOLAS_LIST, 1);
					takeItems(player, RECIPE_TITAN_KEY, 1);
					takeItems(player, STAKATO_SHELL, -1);
					takeItems(player, TOAD_LORD_SAC, -1);
					takeItems(player, MARSH_SPIDER_THORN, -1);
					qs.setCond(9, true);
					htmltext = event;
				}
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
				case MANDRAGORA_SPROUT1:
				case MANDRAGORA_SAPLING:
				case MANDRAGORA_BLOSSOM:
				case MANDRAGORA_SPROUT2:
				{
					if (hasQuestItems(killer, RING_OF_TESTIMONY_1ST, BRIGHTS_LIST) && !hasQuestItems(killer, EMILYS_RECIPE))
					{
						if ((getQuestItemsCount(killer, MANDRAGORA_PETAL) < 20))
						{
							giveItems(killer, MANDRAGORA_PETAL, 1);
							if (getQuestItemsCount(killer, MANDRAGORA_PETAL) == 20)
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
				case MARSH_STAKATO:
				case MARSH_STAKATO_WORKER:
				case MARSH_STAKATO_SOLDIER:
				case MARSH_STAKATO_DRONE:
				{
					if (hasQuestItems(killer, RING_OF_TESTIMONY_2ND, NIKOLAS_LIST) && !hasAtLeastOneQuestItem(killer, CLAY_DOUGH, PATTERN_OF_KEYHOLE))
					{
						if ((getQuestItemsCount(killer, STAKATO_SHELL) < 20))
						{
							giveItems(killer, STAKATO_SHELL, 1);
							if (getQuestItemsCount(killer, STAKATO_SHELL) == 20)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if ((getQuestItemsCount(killer, TOAD_LORD_SAC) >= 10) && (getQuestItemsCount(killer, MARSH_SPIDER_THORN) >= 10))
								{
									qs.setCond(8);
								}
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case GIANT_CRIMSON_ANT:
				{
					if (hasQuestItems(killer, RING_OF_TESTIMONY_1ST, BRIGHTS_LIST) && !hasQuestItems(killer, EMILYS_RECIPE))
					{
						if ((getQuestItemsCount(killer, CRIMSON_MOSS) < 10))
						{
							giveItems(killer, CRIMSON_MOSS, 1);
							if (getQuestItemsCount(killer, CRIMSON_MOSS) == 10)
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
				case TOAD_LORD:
				{
					if (hasQuestItems(killer, RING_OF_TESTIMONY_2ND, NIKOLAS_LIST) && !hasAtLeastOneQuestItem(killer, CLAY_DOUGH, PATTERN_OF_KEYHOLE))
					{
						if ((getQuestItemsCount(killer, TOAD_LORD_SAC) < 10))
						{
							giveItems(killer, TOAD_LORD_SAC, 1);
							if (getQuestItemsCount(killer, TOAD_LORD_SAC) == 10)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if ((getQuestItemsCount(killer, STAKATO_SHELL) >= 20) && (getQuestItemsCount(killer, MARSH_SPIDER_THORN) >= 10))
								{
									qs.setCond(8);
								}
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case MARSH_SPIDER:
				{
					if (hasQuestItems(killer, RING_OF_TESTIMONY_2ND, NIKOLAS_LIST) && !hasAtLeastOneQuestItem(killer, CLAY_DOUGH, PATTERN_OF_KEYHOLE))
					{
						if ((getQuestItemsCount(killer, MARSH_SPIDER_THORN) < 10))
						{
							giveItems(killer, MARSH_SPIDER_THORN, 1);
							if (getQuestItemsCount(killer, MARSH_SPIDER_THORN) == 10)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if ((getQuestItemsCount(killer, STAKATO_SHELL) >= 20) && (getQuestItemsCount(killer, TOAD_LORD_SAC) >= 10))
								{
									qs.setCond(8);
								}
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
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == WAREHOUSE_KEEPER_PARMAN)
			{
				if ((player.getRace() == Race.DWARF) && (player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.DWARF_2ND_GROUP))
				{
					htmltext = "30104-03.htm";
				}
				else if ((player.getRace() == Race.DWARF) && (player.getLevel() >= MIN_LEVEL))
				{
					htmltext = "30104-01a.html";
				}
				else if ((player.getRace() == Race.DWARF))
				{
					htmltext = "30104-02.html";
				}
				else
				{
					htmltext = "30104-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case WAREHOUSE_KEEPER_PARMAN:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (hasQuestItems(player, OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILYS_RECIPE, LILITHS_ELVEN_WAFER))
						{
							htmltext = "30104-06.html";
						}
						else
						{
							htmltext = "30104-05.html";
						}
					}
					else if (hasQuestItems(player, PARMANS_INSTRUCTIONS))
					{
						takeItems(player, PARMANS_INSTRUCTIONS, 1);
						giveItems(player, RING_OF_TESTIMONY_2ND, 1);
						giveItems(player, PARMANS_LETTER, 1);
						qs.setCond(4, true);
						htmltext = "30104-10.html";
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						if (hasQuestItems(player, PARMANS_LETTER))
						{
							htmltext = "30104-11.html";
						}
						else if (hasAtLeastOneQuestItem(player, CLAY_DOUGH, PATTERN_OF_KEYHOLE, NIKOLAS_LIST))
						{
							htmltext = "30104-12.html";
						}
						else if (hasQuestItems(player, MAPHR_TABLET_FRAGMENT))
						{
							giveAdena(player, 217682, true);
							giveItems(player, MARK_OF_PROSPERITY, 1);
							addExpAndSp(player, 1199958, 80080);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30104-13.html";
						}
					}
					break;
				}
				case WAREHOUSE_KEEPER_WILFORD:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (!hasAtLeastOneQuestItem(player, LILITHS_ELVEN_WAFER, CRYSTAL_BROOCH))
						{
							htmltext = "30005-01.html";
						}
						else if (hasQuestItems(player, CRYSTAL_BROOCH) && !hasQuestItems(player, LILITHS_ELVEN_WAFER))
						{
							htmltext = "30005-05.html";
						}
						else if (hasQuestItems(player, LILITHS_ELVEN_WAFER))
						{
							htmltext = "30005-06.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30005-07.html";
					}
					break;
				}
				case LILITH:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (hasQuestItems(player, CRYSTAL_BROOCH) && !hasQuestItems(player, LILITHS_ELVEN_WAFER))
						{
							htmltext = "30368-01.html";
						}
						else
						{
							htmltext = "30368-04.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30368-05.html";
					}
					break;
				}
				case GUARD_BRIGHT:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (!hasAtLeastOneQuestItem(player, EMILYS_RECIPE, BRIGHTS_LIST, MANDRAGORA_BOUGUET))
						{
							htmltext = "30466-01.html";
						}
						else if (hasQuestItems(player, BRIGHTS_LIST) && !hasQuestItems(player, EMILYS_RECIPE))
						{
							if ((getQuestItemsCount(player, MANDRAGORA_PETAL) < 20) || (getQuestItemsCount(player, CRIMSON_MOSS) < 10))
							{
								htmltext = "30466-04.html";
							}
							else
							{
								takeItems(player, BRIGHTS_LIST, 1);
								takeItems(player, MANDRAGORA_PETAL, -1);
								takeItems(player, CRIMSON_MOSS, -1);
								giveItems(player, MANDRAGORA_BOUGUET, 1);
								htmltext = "30466-05.html";
							}
						}
						else if (hasQuestItems(player, MANDRAGORA_BOUGUET) && !hasAtLeastOneQuestItem(player, EMILYS_RECIPE, BRIGHTS_LIST))
						{
							htmltext = "30466-06.html";
						}
						else if (hasQuestItems(player, EMILYS_RECIPE))
						{
							htmltext = "30466-07.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30466-08.html";
					}
					break;
				}
				case TRADER_SHARI:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_1ST, CONTRIBUTION_OF_SHARI, LOCKIRINS_1ST_NOTICE))
						{
							giveItems(player, CONTRIBUTION_OF_SHARI, 1);
							htmltext = "30517-01.html";
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_SHARI) && !hasAtLeastOneQuestItem(player, LOCKIRINS_1ST_NOTICE, RECEIPT_OF_CONTRIBUTION_1ST))
						{
							htmltext = "30517-02.html";
						}
					}
					break;
				}
				case TRADER_MION:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_2ND, CONTRIBUTION_OF_MION, LOCKIRINS_2ND_NOTICE))
						{
							giveItems(player, CONTRIBUTION_OF_MION, 1);
							htmltext = "30519-01.html";
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_MION) && !hasAtLeastOneQuestItem(player, LOCKIRINS_2ND_NOTICE, RECEIPT_OF_CONTRIBUTION_2ND))
						{
							htmltext = "30519-02.html";
						}
					}
					break;
				}
				case IRON_GATES_LOCKIRIN:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (!hasAtLeastOneQuestItem(player, COLLECTION_LICENSE, OLD_ACCOUNT_BOOK))
						{
							htmltext = "30531-01.html";
						}
						else if (hasQuestItems(player, COLLECTION_LICENSE))
						{
							if (hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_1ST, RECEIPT_OF_CONTRIBUTION_2ND, RECEIPT_OF_CONTRIBUTION_3RD, RECEIPT_OF_CONTRIBUTION_4TH, RECEIPT_OF_CONTRIBUTION_5TH))
							{
								giveItems(player, OLD_ACCOUNT_BOOK, 1);
								takeItems(player, COLLECTION_LICENSE, 1);
								takeItems(player, RECEIPT_OF_CONTRIBUTION_1ST, 1);
								takeItems(player, RECEIPT_OF_CONTRIBUTION_2ND, 1);
								takeItems(player, RECEIPT_OF_CONTRIBUTION_3RD, 1);
								takeItems(player, RECEIPT_OF_CONTRIBUTION_4TH, 1);
								takeItems(player, RECEIPT_OF_CONTRIBUTION_5TH, 1);
								playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if (hasQuestItems(player, BLESSED_SEED, EMILYS_RECIPE, LILITHS_ELVEN_WAFER))
								{
									qs.setCond(2, true);
								}
								htmltext = "30531-05.html";
							}
							else
							{
								htmltext = "30531-04.html";
							}
						}
						else if (hasQuestItems(player, OLD_ACCOUNT_BOOK) && !hasQuestItems(player, COLLECTION_LICENSE))
						{
							htmltext = "30531-06.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30531-07.html";
					}
					break;
				}
				case GOLDEN_WHEELS_SPIRON:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (hasQuestItems(player, LOCKIRINS_1ST_NOTICE) && !hasAtLeastOneQuestItem(player, CONTRIBUTION_OF_SHARI, RECEIPT_OF_CONTRIBUTION_1ST))
						{
							takeItems(player, LOCKIRINS_1ST_NOTICE, 1);
							htmltext = "30532-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_1ST, CONTRIBUTION_OF_SHARI, LOCKIRINS_1ST_NOTICE))
						{
							htmltext = "30532-02.html";
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_SHARI) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_1ST, LOCKIRINS_1ST_NOTICE))
						{
							takeItems(player, CONTRIBUTION_OF_SHARI, 1);
							giveItems(player, RECEIPT_OF_CONTRIBUTION_1ST, 1);
							htmltext = "30532-03.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_1ST) && !hasAtLeastOneQuestItem(player, CONTRIBUTION_OF_SHARI, LOCKIRINS_1ST_NOTICE))
						{
							htmltext = "30532-04.html";
						}
					}
					break;
				}
				case SILVER_SCALES_BALANKI:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (hasQuestItems(player, LOCKIRINS_2ND_NOTICE) && !hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_2ND) && ((getQuestItemsCount(player, CONTRIBUTION_OF_MION) + getQuestItemsCount(player, CONTRIBUTION_OF_MARYSE)) < 2))
						{
							takeItems(player, LOCKIRINS_2ND_NOTICE, 1);
							htmltext = "30533-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, LOCKIRINS_2ND_NOTICE, RECEIPT_OF_CONTRIBUTION_2ND) && ((getQuestItemsCount(player, CONTRIBUTION_OF_MION) + getQuestItemsCount(player, CONTRIBUTION_OF_MARYSE)) < 2))
						{
							htmltext = "30533-02.html";
						}
						else if (!hasAtLeastOneQuestItem(player, LOCKIRINS_2ND_NOTICE, RECEIPT_OF_CONTRIBUTION_2ND) && hasQuestItems(player, CONTRIBUTION_OF_MION, CONTRIBUTION_OF_MARYSE))
						{
							takeItems(player, CONTRIBUTION_OF_MION, 1);
							takeItems(player, CONTRIBUTION_OF_MARYSE, 1);
							giveItems(player, RECEIPT_OF_CONTRIBUTION_2ND, 1);
							htmltext = "30533-03.html";
						}
						else if (!hasQuestItems(player, LOCKIRINS_2ND_NOTICE) && hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_2ND) && !hasQuestItems(player, CONTRIBUTION_OF_MION, CONTRIBUTION_OF_MARYSE))
						{
							htmltext = "30533-04.html";
						}
					}
					break;
				}
				case BRONZE_KEYS_KEEF:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (hasQuestItems(player, LOCKIRINS_3RD_NOTICE) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_3RD, PROCURATION_OF_TOROCCO))
						{
							takeItems(player, LOCKIRINS_3RD_NOTICE, 1);
							htmltext = "30534-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_3RD, PROCURATION_OF_TOROCCO, LOCKIRINS_3RD_NOTICE))
						{
							htmltext = "30534-02.html";
						}
						else if (hasQuestItems(player, PROCURATION_OF_TOROCCO) && !hasAtLeastOneQuestItem(player, LOCKIRINS_3RD_NOTICE, RECEIPT_OF_CONTRIBUTION_3RD))
						{
							htmltext = "30534-03.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_3RD) && !hasAtLeastOneQuestItem(player, PROCURATION_OF_TOROCCO, LOCKIRINS_3RD_NOTICE))
						{
							htmltext = "30534-04.html";
						}
					}
					break;
				}
				case GRAY_PILLAR_MEMBER_FILAUR:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (hasQuestItems(player, LOCKIRINS_4TH_NOTICE) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_4TH, RECEIPT_OF_BOLTER))
						{
							takeItems(player, LOCKIRINS_4TH_NOTICE, 1);
							htmltext = "30535-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_4TH, RECEIPT_OF_BOLTER, LOCKIRINS_4TH_NOTICE))
						{
							htmltext = "30535-02.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_BOLTER) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_4TH, LOCKIRINS_4TH_NOTICE))
						{
							takeItems(player, RECEIPT_OF_BOLTER, 1);
							giveItems(player, RECEIPT_OF_CONTRIBUTION_4TH, 1);
							htmltext = "30535-03.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_4TH) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_BOLTER, LOCKIRINS_4TH_NOTICE))
						{
							htmltext = "30535-04.html";
						}
					}
					break;
				}
				case BLACK_ANVILS_ARIN:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (hasQuestItems(player, LOCKIRINS_5TH_NOTICE) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_5TH, CONTRIBUTION_OF_TOMA))
						{
							takeItems(player, LOCKIRINS_5TH_NOTICE, 1);
							htmltext = "30536-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_5TH, CONTRIBUTION_OF_TOMA, LOCKIRINS_5TH_NOTICE))
						{
							htmltext = "30536-02.html";
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_TOMA) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_5TH, LOCKIRINS_5TH_NOTICE))
						{
							takeItems(player, CONTRIBUTION_OF_TOMA, 1);
							giveItems(player, RECEIPT_OF_CONTRIBUTION_5TH, 1);
							htmltext = "30536-03.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_CONTRIBUTION_5TH) && !hasAtLeastOneQuestItem(player, CONTRIBUTION_OF_TOMA, LOCKIRINS_5TH_NOTICE))
						{
							htmltext = "30536-04.html";
						}
					}
					break;
				}
				case MARYSE_REDBONNET:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_2ND, CONTRIBUTION_OF_MARYSE, LOCKIRINS_2ND_NOTICE, MARYSES_REQUEST))
						{
							giveItems(player, MARYSES_REQUEST, 1);
							htmltext = "30553-01.html";
						}
						else if (hasQuestItems(player, MARYSES_REQUEST) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_2ND, CONTRIBUTION_OF_MARYSE, LOCKIRINS_2ND_NOTICE))
						{
							if (getQuestItemsCount(player, ANIMAL_SKIN) < 10)
							{
								htmltext = "30553-02.html";
							}
							else
							{
								takeItems(player, ANIMAL_SKIN, 10);
								giveItems(player, CONTRIBUTION_OF_MARYSE, 1);
								takeItems(player, MARYSES_REQUEST, 1);
								htmltext = "30553-03.html";
							}
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_MARYSE) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_2ND, LOCKIRINS_2ND_NOTICE, MARYSES_REQUEST))
						{
							htmltext = "30553-04.html";
						}
					}
					break;
				}
				case MINER_BOLTER:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_4TH, RECEIPT_OF_BOLTER, LOCKIRINS_4TH_NOTICE))
						{
							giveItems(player, RECEIPT_OF_BOLTER, 1);
							htmltext = "30554-01.html";
						}
						else if (hasQuestItems(player, RECEIPT_OF_BOLTER) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_4TH, LOCKIRINS_4TH_NOTICE))
						{
							htmltext = "30554-02.html";
						}
					}
					break;
				}
				case CARRIER_TOROCCO:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_3RD, PROCURATION_OF_TOROCCO, LOCKIRINS_3RD_NOTICE))
						{
							htmltext = "30555-01.html";
						}
						else if (hasQuestItems(player, PROCURATION_OF_TOROCCO) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_3RD, LOCKIRINS_3RD_NOTICE))
						{
							htmltext = "30555-03.html";
						}
					}
					break;
				}
				case MASTER_TOMA:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST, COLLECTION_LICENSE))
					{
						if (!hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_5TH, CONTRIBUTION_OF_TOMA, LOCKIRINS_5TH_NOTICE))
						{
							giveItems(player, CONTRIBUTION_OF_TOMA, 1);
							htmltext = "30556-01.html";
						}
						else if (hasQuestItems(player, CONTRIBUTION_OF_TOMA) && !hasAtLeastOneQuestItem(player, RECEIPT_OF_CONTRIBUTION_5TH, LOCKIRINS_5TH_NOTICE))
						{
							htmltext = "30556-02.html";
						}
					}
					break;
				}
				case PIOTUR:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (!hasQuestItems(player, BLESSED_SEED))
						{
							htmltext = "30597-01.html";
						}
						else
						{
							htmltext = "30597-03.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30597-04.html";
					}
					break;
				}
				case EMILY:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_1ST))
					{
						if (hasQuestItems(player, MANDRAGORA_BOUGUET) && !hasAtLeastOneQuestItem(player, EMILYS_RECIPE, BRIGHTS_LIST))
						{
							htmltext = "30620-01.html";
						}
						else if (hasQuestItems(player, EMILYS_RECIPE))
						{
							htmltext = "30620-04.html";
						}
					}
					else if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						htmltext = "30620-05.html";
					}
					break;
				}
				case MAESTRO_NIKOLA:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						if (!hasAtLeastOneQuestItem(player, CLAY_DOUGH, PATTERN_OF_KEYHOLE, NIKOLAS_LIST, MAPHR_TABLET_FRAGMENT))
						{
							takeItems(player, PARMANS_LETTER, 1);
							htmltext = "30621-01.html";
						}
						else if (hasQuestItems(player, CLAY_DOUGH) && !hasAtLeastOneQuestItem(player, PATTERN_OF_KEYHOLE, NIKOLAS_LIST, MAPHR_TABLET_FRAGMENT))
						{
							htmltext = "30621-05.html";
						}
						else if (hasQuestItems(player, PATTERN_OF_KEYHOLE) && !hasAtLeastOneQuestItem(player, CLAY_DOUGH, NIKOLAS_LIST, MAPHR_TABLET_FRAGMENT))
						{
							giveItems(player, RECIPE_TITAN_KEY, 1);
							takeItems(player, PATTERN_OF_KEYHOLE, 1);
							giveItems(player, NIKOLAS_LIST, 1);
							qs.setCond(7, true);
							htmltext = "30621-06.html";
						}
						else if (hasQuestItems(player, NIKOLAS_LIST) && !hasAtLeastOneQuestItem(player, CLAY_DOUGH, PATTERN_OF_KEYHOLE, MAPHR_TABLET_FRAGMENT, KEY_OF_TITAN))
						{
							htmltext = "30621-07.html";
						}
						else if (hasQuestItems(player, NIKOLAS_LIST, KEY_OF_TITAN) && !hasAtLeastOneQuestItem(player, CLAY_DOUGH, PATTERN_OF_KEYHOLE, MAPHR_TABLET_FRAGMENT))
						{
							htmltext = "30621-08.html";
						}
						else if (hasQuestItems(player, MAPHR_TABLET_FRAGMENT) && !hasAtLeastOneQuestItem(player, CLAY_DOUGH, PATTERN_OF_KEYHOLE, NIKOLAS_LIST))
						{
							htmltext = "30621-09.html";
						}
					}
					break;
				}
				case BOX_OF_TITAN:
				{
					if (hasQuestItems(player, RING_OF_TESTIMONY_2ND))
					{
						if (hasQuestItems(player, CLAY_DOUGH) && !hasQuestItems(player, PATTERN_OF_KEYHOLE))
						{
							htmltext = "30622-01.html";
						}
						else if (hasQuestItems(player, KEY_OF_TITAN) && !hasQuestItems(player, MAPHR_TABLET_FRAGMENT))
						{
							htmltext = "30622-03.html";
						}
						else if (!hasAtLeastOneQuestItem(player, KEY_OF_TITAN, CLAY_DOUGH))
						{
							htmltext = "30622-05.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == WAREHOUSE_KEEPER_PARMAN)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}