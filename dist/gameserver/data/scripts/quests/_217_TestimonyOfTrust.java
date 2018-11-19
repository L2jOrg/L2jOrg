package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _217_TestimonyOfTrust extends Quest
{
	private static final int MARK_OF_TRUST_ID = 2734;
	private static final int LETTER_TO_ELF_ID = 1558;
	private static final int LETTER_TO_DARKELF_ID = 1556;
	private static final int LETTER_TO_DWARF_ID = 2737;
	private static final int LETTER_TO_ORC_ID = 2738;
	private static final int LETTER_TO_SERESIN_ID = 2739;
	private static final int SCROLL_OF_DARKELF_TRUST_ID = 2740;
	private static final int SCROLL_OF_ELF_TRUST_ID = 2741;
	private static final int SCROLL_OF_DWARF_TRUST_ID = 2742;
	private static final int SCROLL_OF_ORC_TRUST_ID = 2743;
	private static final int RECOMMENDATION_OF_HOLLIN_ID = 2744;
	private static final int ORDER_OF_OZZY_ID = 2745;
	private static final int BREATH_OF_WINDS_ID = 2746;
	private static final int SEED_OF_VERDURE_ID = 2747;
	private static final int LETTER_OF_THIFIELL_ID = 2748;
	private static final int BLOOD_OF_GUARDIAN_BASILISK_ID = 2749;
	private static final int GIANT_APHID_ID = 2750;
	private static final int STAKATOS_FLUIDS_ID = 2751;
	private static final int BASILISK_PLASMA_ID = 2752;
	private static final int HONEY_DEW_ID = 2753;
	private static final int STAKATO_ICHOR_ID = 2754;
	private static final int ORDER_OF_CLAYTON_ID = 2755;
	private static final int PARASITE_OF_LOTA_ID = 2756;
	private static final int LETTER_TO_MANAKIA_ID = 2757;
	private static final int LETTER_OF_MANAKIA_ID = 2758;
	private static final int LETTER_TO_NICHOLA_ID = 2759;
	private static final int ORDER_OF_NICHOLA_ID = 2760;
	private static final int HEART_OF_PORTA_ID = 2761;
	private static final int RewardExp = 156480;
	private static final int RewardSP = 0;

	public _217_TestimonyOfTrust()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(30191);

		addTalkId(30031);
		addTalkId(30154);
		addTalkId(30358);
		addTalkId(30464);
		addTalkId(30515);
		addTalkId(30531);
		addTalkId(30565);
		addTalkId(30621);
		addTalkId(30657);

		addKillId(20013);
		addKillId(20157);
		addKillId(20019);
		addKillId(20213);
		addKillId(20230);
		addKillId(20232);
		addKillId(20234);
		addKillId(20036);
		addKillId(20044);
		addKillId(27120);
		addKillId(27121);
		addKillId(20550);
		addKillId(20553);
		addKillId(20082);
		addKillId(20084);
		addKillId(20086);
		addKillId(20087);
		addKillId(20088);

		addQuestItem(new int[]{
				SCROLL_OF_DARKELF_TRUST_ID,
				SCROLL_OF_ELF_TRUST_ID,
				SCROLL_OF_DWARF_TRUST_ID,
				SCROLL_OF_ORC_TRUST_ID,
				BREATH_OF_WINDS_ID,
				SEED_OF_VERDURE_ID,
				ORDER_OF_OZZY_ID,
				LETTER_TO_ELF_ID,
				ORDER_OF_CLAYTON_ID,
				BASILISK_PLASMA_ID,
				STAKATO_ICHOR_ID,
				HONEY_DEW_ID,
				LETTER_TO_DARKELF_ID,
				LETTER_OF_THIFIELL_ID,
				LETTER_TO_SERESIN_ID,
				LETTER_TO_ORC_ID,
				LETTER_OF_MANAKIA_ID,
				LETTER_TO_MANAKIA_ID,
				PARASITE_OF_LOTA_ID,
				LETTER_TO_DWARF_ID,
				LETTER_TO_NICHOLA_ID,
				HEART_OF_PORTA_ID,
				ORDER_OF_NICHOLA_ID,
				RECOMMENDATION_OF_HOLLIN_ID,
				BLOOD_OF_GUARDIAN_BASILISK_ID,
				STAKATOS_FLUIDS_ID,
				GIANT_APHID_ID
		});

		addClassIdCheck("hollin_q0217_02.htm", 1, 4, 7, 11, 15);
		addLevelCheck("hollin_q0217_01.htm", 37);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			htmltext = "hollin_q0217_04.htm";
			st.setCond(1);
			st.set("id", "0");
			st.giveItems(LETTER_TO_ELF_ID, 1);
			st.giveItems(LETTER_TO_DARKELF_ID, 1);
		}
		else if(event.equalsIgnoreCase("30154_1"))
			htmltext = "ozzy_q0217_02.htm";
		else if(event.equalsIgnoreCase("30154_2"))
		{
			htmltext = "ozzy_q0217_03.htm";
			st.takeItems(LETTER_TO_ELF_ID, 1);
			st.giveItems(ORDER_OF_OZZY_ID, 1);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30358_1"))
		{
			htmltext = "tetrarch_thifiell_q0217_02.htm";
			st.takeItems(LETTER_TO_DARKELF_ID, 1);
			st.giveItems(LETTER_OF_THIFIELL_ID, 1);
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("30657_1"))
			if(st.getPlayer().getLevel() >= 38)
			{
				htmltext = "cardinal_seresin_q0217_03.htm";
				st.takeItems(LETTER_TO_SERESIN_ID, 1);
				st.giveItems(LETTER_TO_ORC_ID, 1);
				st.giveItems(LETTER_TO_DWARF_ID, 1);
				st.setCond(12);
			}
			else
				htmltext = "cardinal_seresin_q0217_02.htm";
		else if(event.equalsIgnoreCase("30565_1"))
		{
			htmltext = "kakai_the_lord_of_flame_q0217_02.htm";
			st.takeItems(LETTER_TO_ORC_ID, 1);
			st.giveItems(LETTER_TO_MANAKIA_ID, 1);
			st.setCond(13);
		}
		else if(event.equalsIgnoreCase("30515_1"))
		{
			htmltext = "seer_manakia_q0217_02.htm";
			st.takeItems(LETTER_TO_MANAKIA_ID, 1);
			st.setCond(14);
		}
		else if(event.equalsIgnoreCase("30531_1"))
		{
			htmltext = "first_elder_lockirin_q0217_02.htm";
			st.takeItems(LETTER_TO_DWARF_ID, 1);
			st.giveItems(LETTER_TO_NICHOLA_ID, 1);
			st.setCond(18);
		}
		else if(event.equalsIgnoreCase("30621_1"))
		{
			htmltext = "maestro_nikola_q0217_02.htm";
			st.takeItems(LETTER_TO_NICHOLA_ID, 1);
			st.giveItems(ORDER_OF_NICHOLA_ID, 1);
			st.setCond(19);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_TRUST_ID) > 0)
		{
			return COMPLETED_DIALOG;
		}
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case 30191:
				if(cond == 0)
					htmltext = "hollin_q0217_03.htm";
				else if(cond >= 1 && cond <= 7)
					htmltext = "hollin_q0217_08.htm";
				else if(cond == 9)
				{
					htmltext = "hollin_q0217_05.htm";
					st.takeItems(SCROLL_OF_DARKELF_TRUST_ID, 1);
					st.takeItems(SCROLL_OF_ELF_TRUST_ID, 1);
					st.giveItems(LETTER_TO_SERESIN_ID, 1);
					st.setCond(10);
				}
				else if(cond >= 10 && cond <= 21)
					htmltext = "hollin_q0217_09.htm";
				else if(cond == 22)
				{
					htmltext = "hollin_q0217_06.htm";
					st.takeItems(SCROLL_OF_DWARF_TRUST_ID, 1);
					st.takeItems(SCROLL_OF_ORC_TRUST_ID, 1);
					st.giveItems(RECOMMENDATION_OF_HOLLIN_ID, 1);
					st.setCond(23);
				}
				else if(cond == 23)
					htmltext = "hollin_q0217_07.htm";
			break;

			case 30154:
				if(cond == 1)
					htmltext = "ozzy_q0217_01.htm";
				else if(cond == 2)
					htmltext = "ozzy_q0217_04.htm";
				else if(cond == 3)
				{
					htmltext = "ozzy_q0217_05.htm";
					st.takeItems(BREATH_OF_WINDS_ID, 1);
					st.takeItems(SEED_OF_VERDURE_ID, 1);
					st.takeItems(ORDER_OF_OZZY_ID, 1);
					st.giveItems(SCROLL_OF_ELF_TRUST_ID, 1);
					st.setCond(4);
				}
				else if(cond == 4)
					htmltext = "ozzy_q0217_06.htm";
			break;

			case 30358:
				if(cond == 4)
					htmltext = "tetrarch_thifiell_q0217_01.htm";
				else if(cond == 8)
				{
					st.takeItems(BASILISK_PLASMA_ID, 1);
					st.takeItems(STAKATO_ICHOR_ID, 1);
					st.takeItems(HONEY_DEW_ID, 1);
					st.giveItems(SCROLL_OF_DARKELF_TRUST_ID, 1);
					st.setCond(9);
					htmltext = "tetrarch_thifiell_q0217_03.htm";
				}
				else if(cond == 7)
					htmltext = "tetrarch_thifiell_q0217_04.htm";
				else if(cond == 5)
					htmltext = "tetrarch_thifiell_q0217_05.htm";
			break;

			case 30464:
				if(cond == 5)
				{
					htmltext = "magister_clayton_q0217_01.htm";
					st.takeItems(LETTER_OF_THIFIELL_ID, 1);
					st.giveItems(ORDER_OF_CLAYTON_ID, 1);
					st.setCond(6);
				}
				else if(cond == 6)
					htmltext = "magister_clayton_q0217_02.htm";
				else if(cond == 7)
				{
					st.takeItems(ORDER_OF_CLAYTON_ID, 1);
					st.setCond(8);
					htmltext = "magister_clayton_q0217_03.htm";
				}
			break;

			case 30657:
				if(cond == 10 || cond == 11)
				{
					if(st.getQuestItemsCount(LETTER_TO_SERESIN_ID) > 0 && st.getPlayer().getLevel() >= 38)
						htmltext = "cardinal_seresin_q0217_01.htm";
					else
					{
						htmltext = "cardinal_seresin_q0217_02.htm";
						if(cond == 10)
							st.setCond(11);
					}
				}
				else if(cond == 18)
					htmltext = "cardinal_seresin_q0217_05.htm";
			break;

			case 30565:
				if(cond == 12)
					htmltext = "kakai_the_lord_of_flame_q0217_01.htm";
				else if(cond == 13)
					htmltext = "kakai_the_lord_of_flame_q0217_03.htm";
				else if(cond == 16)
				{
					htmltext = "kakai_the_lord_of_flame_q0217_04.htm";
					st.takeItems(LETTER_OF_MANAKIA_ID, 1);
					st.giveItems(SCROLL_OF_ORC_TRUST_ID, 1);
					st.setCond(17);
				}
				else if(cond >= 17)
					htmltext = "kakai_the_lord_of_flame_q0217_05.htm";
			break;

			case 30515:
				if(cond == 13)
					htmltext = "seer_manakia_q0217_01.htm";
				else if(cond == 14)
					htmltext = "seer_manakia_q0217_03.htm";
				else if(cond == 15)
				{
					htmltext = "seer_manakia_q0217_04.htm";
					st.takeItems(PARASITE_OF_LOTA_ID, -1);
					st.giveItems(LETTER_OF_MANAKIA_ID, 1);
					st.setCond(16);
				}
				else if(cond == 16)
					htmltext = "seer_manakia_q0217_05.htm";
			break;

			case 30531:
				if(cond == 17)
					htmltext = "first_elder_lockirin_q0217_01.htm";
				else if(cond == 18)
					htmltext = "first_elder_lockirin_q0217_03.htm";
				else if(cond == 21)
				{
					htmltext = "first_elder_lockirin_q0217_04.htm";
					st.giveItems(SCROLL_OF_DWARF_TRUST_ID, 1);
					st.setCond(22);
				}
				else if(cond == 22)
					htmltext = "first_elder_lockirin_q0217_05.htm";
			break;

			case 30621:
				if(cond == 18)
					htmltext = "maestro_nikola_q0217_01.htm";
				else if(cond == 19)
					htmltext = "maestro_nikola_q0217_03.htm";
				else if(cond == 20)
				{
					htmltext = "maestro_nikola_q0217_04.htm";
					st.takeItems(HEART_OF_PORTA_ID, -1);
					st.takeItems(ORDER_OF_NICHOLA_ID, 1);
					st.setCond(21);
				}
				else if(cond == 21)
					htmltext = "maestro_nikola_q0217_05.htm";
			break;

			case 30031:
				if(cond == 23)
				{
					htmltext = "quilt_q0217_01.htm";
					st.takeItems(RECOMMENDATION_OF_HOLLIN_ID, -1);
					st.giveItems(MARK_OF_TRUST_ID, 1);
					if(!st.getPlayer().getVarBoolean("prof2.2"))
					{
						st.addExpAndSp(RewardExp, RewardSP);
						st.getPlayer().setVar("prof2.2", "1", -1);
					}
					st.finishQuest();
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 20036 || npcId == 20044)
		{
			if(cond == 2 && st.getQuestItemsCount(BREATH_OF_WINDS_ID) == 0)
			{
				st.set("id", String.valueOf(st.getInt("id") + 1));
				if(Rnd.chance(st.getInt("id") * 33))
				{
					st.addSpawn(27120);
					st.playSound(SOUND_BEFORE_BATTLE);
				}
			}
		}
		else if(npcId == 20013 || npcId == 20019)
		{
			if(cond == 2 && st.getQuestItemsCount(SEED_OF_VERDURE_ID) == 0)
			{
				st.set("id", String.valueOf(st.getInt("id") + 1));
				if(Rnd.chance(st.getInt("id") * 33))
				{
					st.addSpawn(27121);
					st.playSound(SOUND_BEFORE_BATTLE);
				}
			}
		}
		else if(npcId == 27120)
		{
			if(cond == 2 && st.getQuestItemsCount(BREATH_OF_WINDS_ID) == 0)
				if(st.getQuestItemsCount(SEED_OF_VERDURE_ID) > 0)
				{
					st.giveItems(BREATH_OF_WINDS_ID, 1);
					st.setCond(3);
				}
				else
				{
					st.giveItems(BREATH_OF_WINDS_ID, 1);
					st.playSound(SOUND_ITEMGET);
				}
		}
		else if(npcId == 27121)
		{
			if(cond == 2 && st.getQuestItemsCount(SEED_OF_VERDURE_ID) == 0)
				if(st.getQuestItemsCount(BREATH_OF_WINDS_ID) > 0)
				{
					st.giveItems(SEED_OF_VERDURE_ID, 1);
					st.setCond(3);
				}
				else
				{
					st.giveItems(SEED_OF_VERDURE_ID, 1);
					st.playSound(SOUND_ITEMGET);
				}
		}
		else if(npcId == 20550)
		{
			if(cond == 6 && st.getQuestItemsCount(BLOOD_OF_GUARDIAN_BASILISK_ID) < 5 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(BASILISK_PLASMA_ID) == 0)
			{
				st.giveItems(BLOOD_OF_GUARDIAN_BASILISK_ID, 1);
				if(st.getQuestItemsCount(BLOOD_OF_GUARDIAN_BASILISK_ID) >= 5)
				{
					st.takeItems(BLOOD_OF_GUARDIAN_BASILISK_ID, -1);
					st.giveItems(BASILISK_PLASMA_ID, 1);
					if(st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) >= 3)
						st.setCond(7);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}	
		}
		else if(npcId == 20157 || npcId == 20230 || npcId == 20232 || npcId == 20234)
		{
			if(cond == 6 && st.getQuestItemsCount(STAKATOS_FLUIDS_ID) < 5 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(STAKATO_ICHOR_ID) == 0)
			{
				st.giveItems(STAKATOS_FLUIDS_ID, 1);
				if(st.getQuestItemsCount(STAKATOS_FLUIDS_ID) >= 5)
				{
					st.takeItems(STAKATOS_FLUIDS_ID, -1);
					st.giveItems(STAKATO_ICHOR_ID, 1);
					if(st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) >= 3)
						st.setCond(7);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}	
		}
		else if(npcId == 20082 || npcId == 20086 || npcId == 20087 || npcId == 20084 || npcId == 20088)
		{
			if(cond == 6 && st.getQuestItemsCount(GIANT_APHID_ID) < 5 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(HONEY_DEW_ID) == 0)
			{
				st.giveItems(GIANT_APHID_ID, 1);
				if(st.getQuestItemsCount(GIANT_APHID_ID) >= 5)
				{
					st.takeItems(GIANT_APHID_ID, -1);
					st.giveItems(HONEY_DEW_ID, 1);
					if(st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) >= 3)
						st.setCond(7);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}	
		}
		else if(npcId == 20553)
		{
			if(cond == 14 && st.getQuestItemsCount(PARASITE_OF_LOTA_ID) < 10)
			{
				st.rollAndGive(PARASITE_OF_LOTA_ID, 1, 1, 10, 50);
				if(st.getQuestItemsCount(PARASITE_OF_LOTA_ID) > 9)
					st.setCond(15);
			}
		}
		else if(npcId == 20213 && cond == 19 && st.getQuestItemsCount(HEART_OF_PORTA_ID) < 1)
		{
			st.giveItems(HEART_OF_PORTA_ID, 1);
			st.setCond(20);
		}
		return null;
	}
}