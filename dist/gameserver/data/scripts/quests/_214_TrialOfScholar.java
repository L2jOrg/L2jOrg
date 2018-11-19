package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _214_TrialOfScholar extends Quest
{
	// NPCs
	private static final int Sylvain = 30070;
	private static final int Lucas = 30071;
	private static final int Valkon = 30103;
	private static final int Dieter = 30111;
	private static final int Jurek = 30115;
	private static final int Edroc = 30230;
	private static final int Raut = 30316;
	private static final int Poitan = 30458;
	private static final int Mirien = 30461;
	private static final int Maria = 30608;
	private static final int Creta = 30609;
	private static final int Cronos = 30610;
	private static final int Triff = 30611;
	private static final int Casian = 30612;
	// Mobs
	private static final int Monster_Eye_Destroyer = 20068;
	private static final int Medusa = 20158;
	private static final int Ghoul = 20201;
	private static final int Shackle = 20235;
	private static final int Breka_Orc_Shaman = 20269;
	private static final int Fettered_Soul = 20552;
	private static final int Grandis = 20554;
	private static final int Enchanted_Gargoyle = 20567;
	private static final int Leto_Lizardman_Warrior = 20580;
	// Items
	private static final int Mark_of_Scholar = 2674;
	private static final int Miriens_1st_Sigil = 2675;
	private static final int Miriens_2nd_Sigil = 2676;
	private static final int Miriens_3rd_Sigil = 2677;
	private static final int Miriens_Instruction = 2678;
	private static final int Marias_1st_Letter = 2679;
	private static final int Marias_2nd_Letter = 2680;
	private static final int Lucass_Letter = 2681;
	private static final int Lucillas_Handbag = 2682;
	private static final int Cretas_1st_Letter = 2683;
	private static final int Cretas_Painting1 = 2684;
	private static final int Cretas_Painting2 = 2685;
	private static final int Cretas_Painting3 = 2686;
	private static final int Brown_Scroll_Scrap = 2687;
	private static final int Crystal_of_Purity1 = 2688;
	private static final int High_Priests_Sigil = 2689;
	private static final int Grand_Magisters_Sigil = 2690;
	private static final int Cronos_Sigil = 2691;
	private static final int Sylvains_Letter = 2692;
	private static final int Symbol_of_Sylvain = 2693;
	private static final int Jureks_List = 2694;
	private static final int Monster_Eye_Destroyer_Skin = 2695;
	private static final int Shamans_Necklace = 2696;
	private static final int Shackles_Scalp = 2697;
	private static final int Symbol_of_Jurek = 2698;
	private static final int Cronos_Letter = 2699;
	private static final int Dieters_Key = 2700;
	private static final int Cretas_2nd_Letter = 2701;
	private static final int Dieters_Letter = 2702;
	private static final int Dieters_Diary = 2703;
	private static final int Rauts_Letter_Envelope = 2704;
	private static final int Triffs_Ring = 2705;
	private static final int Scripture_Chapter_1 = 2706;
	private static final int Scripture_Chapter_2 = 2707;
	private static final int Scripture_Chapter_3 = 2708;
	private static final int Scripture_Chapter_4 = 2709;
	private static final int Valkons_Request = 2710;
	private static final int Poitans_Notes = 2711;
	private static final int Strong_Liquor = 2713;
	private static final int Crystal_of_Purity2 = 2714;
	private static final int Casians_List = 2715;
	private static final int Ghouls_Skin = 2716;
	private static final int Medusas_Blood = 2717;
	private static final int Fettered_Souls_Ichor = 2718;
	private static final int Enchanted_Gargoyles_Nail = 2719;
	private static final int Symbol_of_Cronos = 2720;

	public _214_TrialOfScholar()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Mirien);
		addTalkId(Sylvain);
		addTalkId(Lucas);
		addTalkId(Valkon);
		addTalkId(Dieter);
		addTalkId(Jurek);
		addTalkId(Edroc);
		addTalkId(Raut);
		addTalkId(Poitan);
		addTalkId(Maria);
		addTalkId(Creta);
		addTalkId(Cronos);
		addTalkId(Triff);
		addTalkId(Casian);

		addKillId(Monster_Eye_Destroyer);
		addKillId(Medusa);
		addKillId(Ghoul);
		addKillId(Shackle);
		addKillId(Breka_Orc_Shaman);
		addKillId(Fettered_Soul);
		addKillId(Grandis);
		addKillId(Enchanted_Gargoyle);
		addKillId(Leto_Lizardman_Warrior);

		addQuestItem(Scripture_Chapter_3);
		addQuestItem(Brown_Scroll_Scrap);
		addQuestItem(Monster_Eye_Destroyer_Skin);
		addQuestItem(Shamans_Necklace);
		addQuestItem(Shackles_Scalp);
		addQuestItem(Ghouls_Skin);
		addQuestItem(Medusas_Blood);
		addQuestItem(Fettered_Souls_Ichor);
		addQuestItem(Enchanted_Gargoyles_Nail);

		addClassIdCheck("magister_mirien_q0214_01.htm", 11, 26, 39);
		addLevelCheck("magister_mirien_q0214_02.htm", 35);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("magister_mirien_q0214_04.htm"))
		{
			st.giveItems(Miriens_1st_Sigil, 1);
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sylvain_q0214_02.htm"))
		{
			st.giveItems(High_Priests_Sigil, 1);
			st.giveItems(Sylvains_Letter, 1);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("marya_q0214_02.htm"))
		{
			st.takeItems(Sylvains_Letter, -1);
			st.giveItems(Marias_1st_Letter, 1);
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("astrologer_creta_q0214_05.htm"))
		{
			st.takeItems(Marias_2nd_Letter, -1);
			st.giveItems(Cretas_1st_Letter, 1);
			st.setCond(6);
		}
		else if(event.equalsIgnoreCase("marya_q0214_08.htm"))
		{
			st.takeItems(Cretas_1st_Letter, -1);
			st.giveItems(Lucillas_Handbag, 1);
			st.setCond(7);
		}
		else if(event.equalsIgnoreCase("astrologer_creta_q0214_09.htm"))
		{
			st.takeItems(Lucillas_Handbag, -1);
			st.giveItems(Cretas_Painting1, 1);
			st.setCond(8);
		}
		else if(event.equalsIgnoreCase("lucas_q0214_04.htm"))
		{
			st.takeItems(Cretas_Painting2, -1);
			st.giveItems(Cretas_Painting3, 1);
			st.setCond(10);
		}
		else if(event.equalsIgnoreCase("marya_q0214_14.htm"))
		{
			st.takeItems(Cretas_Painting3, -1);
			st.takeItems(Brown_Scroll_Scrap, -1);
			st.giveItems(Crystal_of_Purity1, 1);
			st.setCond(13);
		}
		else if(event.equalsIgnoreCase("valkon_q0214_04.htm") && st.getQuestItemsCount(Valkons_Request) == 0)
		{
			st.playSound(SOUND_MIDDLE);
			st.giveItems(Valkons_Request, 1);
		}
		else if(event.equalsIgnoreCase("jurek_q0214_03.htm") && st.getQuestItemsCount(Grand_Magisters_Sigil) == 0 && st.getQuestItemsCount(Symbol_of_Jurek) == 0)
		{
			st.giveItems(Grand_Magisters_Sigil, 1);
			st.giveItems(Jureks_List, 1);
			st.setCond(16);
		}
		else if(event.equalsIgnoreCase("magister_mirien_q0214_10.htm"))
		{
			st.takeItems(Miriens_2nd_Sigil, -1);
			st.takeItems(Symbol_of_Jurek, -1);
			if(st.getPlayer().getLevel() < 36)
			{
				st.giveItems(Miriens_Instruction, 1);
				return "magister_mirien_q0214_09.htm";
			}
			st.giveItems(Miriens_3rd_Sigil, 1);
			st.setCond(19);
		}
		else if(event.equalsIgnoreCase("sage_cronos_q0214_10.htm"))
		{
			st.giveItems(Cronos_Sigil, 1);
			st.giveItems(Cronos_Letter, 1);
			st.setCond(20);
		}
		else if(event.equalsIgnoreCase("dieter_q0214_05.htm"))
		{
			st.takeItems(Cronos_Letter, -1);
			st.giveItems(Dieters_Key, 1);
			st.setCond(21);
		}
		else if(event.equalsIgnoreCase("astrologer_creta_q0214_14.htm"))
		{
			st.takeItems(Dieters_Key, -1);
			st.giveItems(Cretas_2nd_Letter, 1);
			st.setCond(22);
		}
		else if(event.equalsIgnoreCase("dieter_q0214_09.htm"))
		{
			st.takeItems(Cretas_2nd_Letter, -1);
			st.giveItems(Dieters_Letter, 1);
			st.giveItems(Dieters_Diary, 1);
			st.setCond(23);
		}
		else if(event.equalsIgnoreCase("trader_edroc_q0214_02.htm"))
		{
			st.takeItems(Dieters_Letter, -1);
			st.giveItems(Rauts_Letter_Envelope, 1);
			st.setCond(24);
		}
		else if(event.equalsIgnoreCase("warehouse_keeper_raut_q0214_02.htm"))
		{
			st.takeItems(Rauts_Letter_Envelope, -1);
			st.giveItems(Scripture_Chapter_1, 1);
			st.giveItems(Strong_Liquor, 1);
			st.setCond(25);
		}
		else if(event.equalsIgnoreCase("drunkard_treaf_q0214_04.htm"))
		{
			st.takeItems(Strong_Liquor, -1);
			st.giveItems(Triffs_Ring, 1);
			st.setCond(26);
		}
		else if(event.equalsIgnoreCase("sage_kasian_q0214_04.htm"))
		{
			if(st.getQuestItemsCount(Casians_List) == 0)
				st.giveItems(Casians_List, 1);
			st.setCond(28);
		}
		else if(event.equalsIgnoreCase("sage_kasian_q0214_07.htm"))
		{
			st.takeItems(Casians_List, -1);
			st.takeItems(Ghouls_Skin, -1);
			st.takeItems(Medusas_Blood, -1);
			st.takeItems(Fettered_Souls_Ichor, -1);
			st.takeItems(Enchanted_Gargoyles_Nail, -1);
			st.takeItems(Poitans_Notes, -1);
			st.giveItems(Scripture_Chapter_4, 1);
			st.setCond(30);
		}
		else if(event.equalsIgnoreCase("sage_cronos_q0214_14.htm"))
		{
			st.takeItems(Scripture_Chapter_1, -1);
			st.takeItems(Scripture_Chapter_2, -1);
			st.takeItems(Scripture_Chapter_3, -1);
			st.takeItems(Scripture_Chapter_4, -1);
			st.takeItems(Cronos_Sigil, -1);
			st.takeItems(Triffs_Ring, -1);
			st.takeItems(Dieters_Diary, 1);
			st.giveItems(Symbol_of_Cronos, 1);
			st.setCond(31);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(Mark_of_Scholar) > 0)
		{
			return COMPLETED_DIALOG;
		}

		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Mirien:
				if(st.getCond() == 0)
					htmltext = "magister_mirien_q0214_03.htm";
				else if(cond > 0 && cond < 14)
					htmltext = "magister_mirien_q0214_05.htm";
				else if(cond == 14)
				{
					st.takeItems(Miriens_1st_Sigil, -1);
					st.takeItems(Symbol_of_Sylvain, -1);
					st.giveItems(Miriens_2nd_Sigil, 1);
					st.setCond(15);
					htmltext = "magister_mirien_q0214_06.htm";
				}
				else if (cond >= 15 && cond < 18)
					htmltext = "magister_mirien_q0214_07.htm";
				else if (cond == 18)
					htmltext = "magister_mirien_q0214_08.htm";
				else if (cond >= 19 && cond < 31)
					htmltext = "magister_mirien_q0214_13.htm";
				else if (cond == 31)
				{
					st.takeItems(Symbol_of_Cronos, -1);
					st.takeItems(Miriens_3rd_Sigil, -1);
					if(!st.getPlayer().getVarBoolean("prof2.1"))
					{
						st.addExpAndSp(193500, 0);
						st.getPlayer().setVar("prof2.1", "1", -1);
					}
					st.giveItems(Mark_of_Scholar, 1);
					st.finishQuest();
					htmltext = "magister_mirien_q0214_14.htm";
				}
			break;

			case Sylvain:
				if(cond == 1)
					htmltext = "sylvain_q0214_01.htm";
				else if(cond == 2)
					htmltext = "sylvain_q0214_03.htm";
				else if(cond == 13)
				{
					htmltext = "sylvain_q0214_04.htm";
					st.takeItems(High_Priests_Sigil, -1);
					st.takeItems(Crystal_of_Purity1, -1);
					st.giveItems(Symbol_of_Sylvain, 1);
					st.setCond(14);
				}
				else if(cond == 14)
					htmltext = "sylvain_q0214_05.htm";
				else if(cond > 14 && cond < 31)
					htmltext = "sylvain_q0214_06.htm";
				break;

			case Lucas:
				if(cond == 3)
				{
					st.takeItems(Marias_1st_Letter, -1);
					st.giveItems(Lucass_Letter, 1);
					st.setCond(4);
					htmltext = "lucas_q0214_01.htm";
				}
				else if(cond == 3)
					htmltext = "lucas_q0214_02.htm";
				else if (cond == 9)
					htmltext = "lucas_q0214_03.htm";
				else if (cond == 10 || cond == 11)
					htmltext = "lucas_q0214_05.htm";
				else if (cond == 12)
					htmltext = "lucas_q0214_06.htm";
				else if (cond > 12 && cond < 31)
					htmltext = "lucas_q0214_06.htm";
				break;

			case Valkon:
				if (cond == 26 || cond == 27)
				{
					if(!st.haveQuestItem(Valkons_Request) && !st.haveQuestItem(Scripture_Chapter_2))
					{
						if(st.haveQuestItem(Crystal_of_Purity2))
						{
							st.takeItems(Crystal_of_Purity2, -1);
							st.playSound(SOUND_MIDDLE);
							st.giveItems(Scripture_Chapter_2, 1);
							htmltext = "valkon_q0214_06.htm";
						}
						else
							htmltext = "valkon_q0214_01.htm";
					}
					else if (st.haveQuestItem(Valkons_Request))
						htmltext = "valkon_q0214_05.htm";
					else if (st.haveQuestItem(Scripture_Chapter_2))
						htmltext = "valkon_q0214_07.htm";
				}
			break;

			case Dieter:
				if (cond == 20)
					htmltext = "dieter_q0214_01.htm";
				else if (cond == 21)
					htmltext = "dieter_q0214_06.htm";
				else if (cond == 22)
					htmltext = "dieter_q0214_07.htm";
				else if (cond == 23)
					htmltext = "dieter_q0214_10.htm";
				else if (cond == 24)
					htmltext = "dieter_q0214_11.htm";
				else if (cond == 26)
					htmltext = "dieter_q0214_12.htm";
				else if (cond > 26 && cond < 31)
					htmltext = "dieter_q0214_13.htm";
				else if (cond == 31)
					htmltext = "dieter_q0214_15.htm";
			break;

			case Jurek:
				if(cond == 15)
					htmltext = "jurek_q0214_01.htm";
				else if(cond == 16)
					htmltext = "jurek_q0214_02.htm";
				else if(cond == 17)
				{
					st.takeItems(Jureks_List, -1);
					st.takeItems(Monster_Eye_Destroyer_Skin, -1);
					st.takeItems(Shamans_Necklace, -1);
					st.takeItems(Shackles_Scalp, -1);
					st.takeItems(Grand_Magisters_Sigil, -1);
					st.giveItems(Symbol_of_Jurek, 1);
					st.setCond(18);
					htmltext = "jurek_q0214_05.htm";
				}
				else if(cond == 18)
					htmltext = "jurek_q0214_06.htm";
				else if (cond > 18)
					htmltext = "jurek_q0214_07.htm";
			break;

			case Edroc:
				if (cond == 23)
					htmltext = "trader_edroc_q0214_01.htm";
				else if (cond == 24)
					htmltext = "trader_edroc_q0214_03.htm";
				else if (cond > 24 && cond < 31)
					htmltext = "trader_edroc_q0214_04.htm";
				break;

			case Raut:
				if (cond == 24)
					htmltext = "warehouse_keeper_raut_q0214_01.htm";
				else if (cond == 25)
					htmltext = "warehouse_keeper_raut_q0214_04.htm";
				else if (cond > 25 && cond < 31)
					htmltext = "warehouse_keeper_raut_q0214_05.htm";
				break;

			case Poitan:
				if (cond == 26 || cond == 27)
				{
					if(st.haveQuestItem(Casians_List))
						htmltext = "blacksmith_poitan_q0214_03.htm";
					else if(!st.haveQuestItem(Poitans_Notes))
					{
						st.playSound(SOUND_MIDDLE);
						st.giveItems(Poitans_Notes, 1);
						htmltext = "blacksmith_poitan_q0214_01.htm";
					}
					else
						htmltext = "blacksmith_poitan_q0214_02.htm";
				}
					else if (cond > 26 && cond < 31)
						htmltext = "blacksmith_poitan_q0214_04.htm";
				break;

			case Maria:
				if (cond == 2)
					htmltext = "marya_q0214_01.htm";
				else if(cond == 3)
					htmltext = "marya_q0214_03.htm";
				else if(cond == 4)
					{
						st.takeItems(Lucass_Letter, -1);
						st.giveItems(Marias_2nd_Letter, 1);
						st.setCond(5);
						return "marya_q0214_04.htm";
					}
				else if(cond == 5)
					htmltext = "marya_q0214_05.htm";
				else if(cond == 6)
					htmltext = "marya_q0214_06.htm";
				else if(cond == 7)
					htmltext = "marya_q0214_09.htm";
				else if(cond == 8)
				{
					st.takeItems(Cretas_Painting1, 1);
					st.giveItems(Cretas_Painting2, 1);
					st.setCond(9);
					htmltext = "marya_q0214_10.htm";
				}
				else if(cond == 9)
					htmltext = "marya_q0214_11.htm";
				else if(cond == 10)
				{
					st.setCond(11);
					htmltext = "marya_q0214_12.htm";
				}
				else if(cond == 12)
					htmltext = "marya_q0214_13.htm";
				else if(cond == 13)
					htmltext = "marya_q0214_15.htm";
				else if(cond > 13 && cond < 26)
					htmltext = "marya_q0214_16.htm";
				else if(st.haveQuestItem(Valkons_Request) && (cond == 26 || cond == 27))
				{
					st.takeItems(Valkons_Request, 1);
					st.playSound(SOUND_MIDDLE);
					st.giveItems(Crystal_of_Purity2, 1);
					htmltext = "marya_q0214_18.htm";
				}
				else if(cond > 26 && cond < 31)
					htmltext = "marya_q0214_17.htm";
				break;

			case Creta:
				if(cond == 5)
					htmltext = "astrologer_creta_q0214_01.htm";
				else if(cond == 6)
					htmltext = "astrologer_creta_q0214_06.htm";
				else if(cond == 7)
					htmltext = "astrologer_creta_q0214_07.htm";
				else if(cond == 8)
					htmltext = "astrologer_creta_q0214_10.htm";
				else if(cond > 8 && cond < 21)
					htmltext = "astrologer_creta_q0214_11.htm";
				else if(cond == 21)
					htmltext = "astrologer_creta_q0214_12.htm";
				else if(cond == 22)
					htmltext = "astrologer_creta_q0214_15.htm";
				break;

			case Cronos:
				if (cond == 19)
		            htmltext = "sage_cronos_q0214_01.htm";
				else if (cond == 20)
					htmltext = "sage_cronos_q0214_11.htm";
				else if (cond == 30)
					htmltext = "sage_cronos_q0214_12.htm";
				else if (cond == 31)
					htmltext = "sage_cronos_q0214_15.htm";
				break;

			case Triff:
				if (cond == 25)
					htmltext = "drunkard_treaf_q0214_01.htm";
				else if (cond == 26 || cond == 27)
					htmltext = "drunkard_treaf_q0214_05.htm";
				break;

			case Casian:
				if (cond == 26)
				{
					if(st.haveQuestItem(Scripture_Chapter_1) && st.haveQuestItem(Scripture_Chapter_2)  && st.haveQuestItem(Scripture_Chapter_3))
						htmltext = "sage_kasian_q0214_02.htm";
					else
						htmltext = "sage_kasian_q0214_01.htm";
					if(st.getQuestItemsCount(Scripture_Chapter_1) > 0 && st.getQuestItemsCount(Scripture_Chapter_2) > 0 && st.getQuestItemsCount(Scripture_Chapter_3) > 0 && st.getQuestItemsCount(Scripture_Chapter_4) > 0)
						return "sage_kasian_q0214_08.htm";
				}
				else if (cond == 28)
					htmltext = "sage_kasian_q0214_05.htm";
				else if (cond == 29)
					htmltext = "sage_kasian_q0214_06.htm";
				else if (cond == 30)
					htmltext = "sage_kasian_q0214_08.htm";
				break;
		}



		return htmltext;
	}

	private static boolean Check_cond17_items(QuestState st)
	{
		if(st.getQuestItemsCount(Monster_Eye_Destroyer_Skin) < 5)
			return false;
		if(st.getQuestItemsCount(Shamans_Necklace) < 5)
			return false;
		if(st.getQuestItemsCount(Shackles_Scalp) < 2)
			return false;
		return true;
	}

	private static boolean Check_cond29_items(QuestState st)
	{
		if(st.getQuestItemsCount(Ghouls_Skin) < 10)
			return false;
		if(st.getQuestItemsCount(Medusas_Blood) < 12)
			return false;
		if(st.getQuestItemsCount(Fettered_Souls_Ichor) < 5)
			return false;
		if(st.getQuestItemsCount(Enchanted_Gargoyles_Nail) < 5)
			return false;
		return true;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case Leto_Lizardman_Warrior:
				if(cond == 11)
				{
					if(st.rollAndGive(Brown_Scroll_Scrap, 1, 1, 5, 50))
						st.setCond(12);
				}
				break;
			case Monster_Eye_Destroyer:
				if(cond == 16)
				{
					if(st.rollAndGive(Monster_Eye_Destroyer_Skin, 1, 1, 5, 50) && Check_cond17_items(st))
						st.setCond(17);
				}
				break;
			case Breka_Orc_Shaman:
				if(cond == 16)
				{
					if(st.rollAndGive(Shamans_Necklace, 1, 1, 5, 50) && Check_cond17_items(st))
						st.setCond(17);
				}
				break;
			case Shackle:
				if(cond == 16)
				{
					if(st.rollAndGive(Shackles_Scalp, 1, 1, 2, 50) && Check_cond17_items(st))
						st.setCond(17);
				}
				break;
			case Grandis:
				if(cond == 26)
					st.rollAndGive(Scripture_Chapter_3, 1, 1, 1, 30);
				break;
			case Ghoul:
				if(cond == 28)
				{
					if(st.rollAndGive(Ghouls_Skin, 1, 1, 10, 100) && Check_cond29_items(st))
						st.setCond(29);
				}
				break;
			case Medusa:
				if(cond == 28)
				{
					if(st.rollAndGive(Medusas_Blood, 1, 1, 12, 100) && Check_cond29_items(st))
						st.setCond(29);
				}
				break;
			case Fettered_Soul:
				if(cond == 28)
				{
					if(st.rollAndGive(Fettered_Souls_Ichor, 1, 1, 5, 100) && Check_cond29_items(st))
						st.setCond(29);
				}
				break;
			case Enchanted_Gargoyle:
				if(cond == 28)
				{
					if(st.rollAndGive(Enchanted_Gargoyles_Nail, 1, 1, 5, 100) && Check_cond29_items(st))
						st.setCond(29);
				}
				break;
		}
		return null;
	}
}