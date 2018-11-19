package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @reworked by Bonux
**/
public final class _215_TrialOfPilgrim extends Quest
{
	// NPC's
	private static final int HERMIT_SANTIAGO = 30648;	// Отшельник Сантьяго
	private static final int PRIEST_PETRON = 30036;	// Петрон Жрец
	private static final int PRIEST_PRIMOS = 30117;	// Примос Жрец
	private static final int ANDELLIA = 30362;	// Анделия
	private static final int TWINKLEROCK_GAURI = 30550;	// Мерцающая Скала Гаури
	private static final int PRIEST_TANAPI = 30571;	// Провидец Танапи
	private static final int ELDER_CASIAN = 30612;	// Старейшина Казиан
	private static final int ANCESTOR_MARTANKUS = 30649;	// Прародитель Мартанкус
	private static final int PRIEST_OF_THE_EARTH_GERALD = 30650;	// Жрец Земли Геральд
	private static final int WANDERER_DORF = 30651;	// Скиталец Дорф
	private static final int URUHA = 30652;	// Уруха

	// Monster's
	private static final int QUEST_MONSTER_LAVA_SALAMANDER = 27116;	// Квестовый Монстр Лавовая Саламандра
	private static final int QUEST_MONSTER_NAHIR = 27117;	// Квестовый Монстр Нахир
	private static final int QUEST_MONSTER_BLACK_WILLOW = 27118;	// Квестовый Монстр Черная Ива

	// Item's
	private static final int MARK_OF_PILGRIM_ID = 2721;	// Знак Упорства
	private static final int BOOK_OF_SAGE_ID = 2722;	// Книга Мудреца
	private static final int VOUCHER_OF_TRIAL_ID = 2723;	// Знак Испытаний
	private static final int SPIRIT_OF_FLAME_ID = 2724;	// Дух Пламени
	private static final int ESSENSE_OF_FLAME_ID = 2725;	// Эссенция Огня
	private static final int BOOK_OF_GERALD_ID = 2726;	// Книга Геральда
	private static final int GREY_BADGE_ID = 2727;	// Серая Эмблема
	private static final int PICTURE_OF_NAHIR_ID = 2728;	// Рисунок Нахира
	private static final int HAIR_OF_NAHIR_ID = 2729;	// Волосы Нахира
	private static final int STATUE_OF_EINHASAD_ID = 2730;	// Статуэтка Эйнхасад
	private static final int BOOK_OF_DARKNESS_ID = 2731;	// Книга Тьмы
	private static final int DEBRIS_OF_WILLOW_ID = 2732;	// Обломки Ивы
	private static final int TAG_OF_RUMOR_ID = 2733;	// Записи Слухов

	// Reward's
	private static final int EXP_REWARD = 133300; // EXP reward count
	private static final int SP_REWARD = 0; // SP reward count

	// Condition's
	private static final int MIN_LEVEL = 35;

	public _215_TrialOfPilgrim()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(HERMIT_SANTIAGO);

		addTalkId(PRIEST_PETRON);
		addTalkId(PRIEST_PRIMOS);
		addTalkId(ANDELLIA);
		addTalkId(TWINKLEROCK_GAURI);
		addTalkId(PRIEST_TANAPI);
		addTalkId(ELDER_CASIAN);
		addTalkId(ANCESTOR_MARTANKUS);
		addTalkId(PRIEST_OF_THE_EARTH_GERALD);
		addTalkId(WANDERER_DORF);
		addTalkId(URUHA);

		addKillId(QUEST_MONSTER_LAVA_SALAMANDER);
		addKillId(QUEST_MONSTER_NAHIR);
		addKillId(QUEST_MONSTER_BLACK_WILLOW);

		addQuestItem(BOOK_OF_SAGE_ID);
		addQuestItem(VOUCHER_OF_TRIAL_ID);
		addQuestItem(ESSENSE_OF_FLAME_ID);
		addQuestItem(BOOK_OF_GERALD_ID);
		addQuestItem(TAG_OF_RUMOR_ID);
		addQuestItem(PICTURE_OF_NAHIR_ID);
		addQuestItem(HAIR_OF_NAHIR_ID);
		addQuestItem(BOOK_OF_DARKNESS_ID);
		addQuestItem(DEBRIS_OF_WILLOW_ID);
		addQuestItem(GREY_BADGE_ID);
		addQuestItem(SPIRIT_OF_FLAME_ID);
		addQuestItem(STATUE_OF_EINHASAD_ID);

		addClassIdCheck("hermit_santiago_q0215_02.htm", ClassId.CLERIC, ClassId.ORACLE, ClassId.SHILLEN_ORACLE, ClassId.ORC_SHAMAN);
		addLevelCheck("hermit_santiago_q0215_01.htm", MIN_LEVEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("hermit_santiago_q0215_04.htm"))
		{
			st.setCond(1);
			st.giveItems(VOUCHER_OF_TRIAL_ID, 1);
		}
		else if(event.equals("ancestor_martankus_q0215_04.htm"))
		{
			st.setCond(5);
			st.giveItems(SPIRIT_OF_FLAME_ID, 1);
			st.takeItems(ESSENSE_OF_FLAME_ID, 1);
		}
		else if(event.equals("gerald_priest_of_earth_q0215_02.htm"))
		{
			if(st.getQuestItemsCount(ADENA_ID) >= 5000)
			{
				st.setCond(8);
				st.giveItems(BOOK_OF_GERALD_ID, 1);
				st.takeItems(ADENA_ID, 5000);
			}
			else
				htmltext = "gerald_priest_of_earth_q0215_03.htm";
		}
		else if(event.equals("andellria_q0215_05.htm"))
		{
			st.setCond(16);
			st.takeItems(BOOK_OF_DARKNESS_ID, 1);
		}
		else if(event.equals("andellria_q0215_04.htm"))
			st.setCond(16);
		else if(event.equals("uruha_q0215_02.htm"))
		{
			st.setCond(15);
			st.giveItems(BOOK_OF_DARKNESS_ID, 1);
			st.takeItems(DEBRIS_OF_WILLOW_ID, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_PILGRIM_ID) > 0)
			return COMPLETED_DIALOG;

		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;
		switch(npcId)
		{
			case HERMIT_SANTIAGO:
			{
				if(cond == 0)
					htmltext = "hermit_santiago_q0215_03.htm";
				else if(cond == 1)
					htmltext = "hermit_santiago_q0215_09.htm";
				else if(cond == 17)
				{
					htmltext = "hermit_santiago_q0215_10.htm";
					st.takeItems(BOOK_OF_SAGE_ID, -1);
					st.giveItems(MARK_OF_PILGRIM_ID, 1);
					if(!st.getPlayer().getVarBoolean("prof2.1"))
					{
						st.addExpAndSp(EXP_REWARD, SP_REWARD);
						st.getPlayer().setVar("prof2.1", "1", -1);
					}
					st.finishQuest();
				}
				break;
			}
			case PRIEST_TANAPI:
			{
				if(cond == 1)
				{
					htmltext = "seer_tanapi_q0215_01.htm";
					st.takeItems(VOUCHER_OF_TRIAL_ID, 1);
					st.setCond(2);
				}
				else if(cond == 2)
					htmltext = "seer_tanapi_q0215_02.htm";
				else if(cond == 5)
				{
					htmltext = "seer_tanapi_q0215_03.htm";
					st.setCond(6);
				}
				break;
			}
			case ANCESTOR_MARTANKUS:
			{
				if(cond == 2)
				{
					htmltext = "ancestor_martankus_q0215_01.htm";
					st.setCond(3);
				}
				else if(cond == 3)
					htmltext = "ancestor_martankus_q0215_02.htm";
				else if(cond == 4)
					htmltext = "ancestor_martankus_q0215_03.htm";
				break;
			}
			case TWINKLEROCK_GAURI:
			{
				if(cond == 6)
				{
					htmltext = "gauri_twinklerock_q0215_01.htm";
					st.giveItems(TAG_OF_RUMOR_ID, 1);
					st.setCond(7);
				}
				else if(cond == 7)
					htmltext = "gauri_twinklerock_q0215_02.htm";
				break;
			}
			case PRIEST_OF_THE_EARTH_GERALD:
			{
				if(cond == 7)
					htmltext = "gerald_priest_of_earth_q0215_01.htm";
				else if(cond >= 9 && st.haveQuestItem(BOOK_OF_GERALD_ID))
				{
					htmltext = "gerald_priest_of_earth_q0215_04.htm";
					st.giveItems(ADENA_ID, 5000, true);
					st.takeItems(BOOK_OF_GERALD_ID, 1);
				}
				break;
			}
			case WANDERER_DORF:
			{
				if(cond == 7)
				{
					htmltext = "wanderer_dorf_q0215_01.htm";
					st.setCond(8);
					st.giveItems(GREY_BADGE_ID, 1);
					st.takeItems(TAG_OF_RUMOR_ID, 1);
				}
				else if(cond == 8)
					htmltext = "wanderer_dorf_q0215_03.htm";
				break;
			}
			case PRIEST_PRIMOS:
			{
				if(cond == 8)
				{
					htmltext = "primoz_q0215_01.htm";
					st.setCond(9);
				}
				else if(cond == 9)
					htmltext = "primoz_q0215_02.htm";
				break;
			}
			case PRIEST_PETRON:
			{
				if(cond == 9)
				{
					htmltext = "potter_q0215_01.htm";
					st.giveItems(PICTURE_OF_NAHIR_ID, 1);
					st.setCond(10);
				}
				else if(cond == 10)
					htmltext = "potter_q0215_02.htm";
				else if(cond == 11)
				{
					htmltext = "potter_q0215_03.htm";
					st.setCond(12);
					st.giveItems(STATUE_OF_EINHASAD_ID, 1);
					st.takeItems(PICTURE_OF_NAHIR_ID, 1);
					st.takeItems(HAIR_OF_NAHIR_ID, 1);
				}
				else if(cond == 12)
					htmltext = "potter_q0215_04.htm";
				break;
			}
			case ANDELLIA:
			{
				if(cond == 12)
				{
					htmltext = "andellria_q0215_01.htm";
					st.setCond(13);
				}
				else if(cond == 13)
					htmltext = "andellria_q0215_02.htm";
				else if(cond == 15)
				{
					if(st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) > 0)
						htmltext = "andellria_q0215_03.htm";
					else
						htmltext = "andellria_q0215_07.htm";
				}
				else if(cond == 16)
					htmltext = "andellria_q0215_06.htm";
				break;
			}
			case URUHA:
			{
				if(cond == 14)
					htmltext = "uruha_q0215_01.htm";
				else if(cond == 15)
					htmltext = "uruha_q0215_03.htm";
				break;
			}
			case ELDER_CASIAN:
			{
				if(cond == 16)
				{
					htmltext = "sage_kasian_q0215_01.htm";
					st.giveItems(BOOK_OF_SAGE_ID, 1);

					if(st.getQuestItemsCount(BOOK_OF_DARKNESS_ID) > 0)
						st.takeItems(BOOK_OF_DARKNESS_ID, 1);
					if(st.getQuestItemsCount(BOOK_OF_GERALD_ID) > 0)
						st.takeItems(BOOK_OF_GERALD_ID, 1);

					st.setCond(17);
					st.takeItems(GREY_BADGE_ID, 1);
					st.takeItems(SPIRIT_OF_FLAME_ID, 1);
					st.takeItems(STATUE_OF_EINHASAD_ID, 1);
				}
				else if(cond == 17)
					htmltext = "sage_kasian_q0215_02.htm";
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		switch(npcId)
		{
			case QUEST_MONSTER_LAVA_SALAMANDER:
			{
				if(cond == 3 && st.getQuestItemsCount(ESSENSE_OF_FLAME_ID) == 0)
				{
					if(Rnd.chance(30))
					{
						st.setCond(4);
						st.giveItems(ESSENSE_OF_FLAME_ID, 1);
					}
				}
				break;
			}
			case QUEST_MONSTER_NAHIR:
			{
				if(cond == 10 && st.getQuestItemsCount(HAIR_OF_NAHIR_ID) == 0)
				{
					st.setCond(11);
					st.giveItems(HAIR_OF_NAHIR_ID, 1);
				}
				break;
			}
			case QUEST_MONSTER_BLACK_WILLOW:
			{
				if(cond == 13 && st.getQuestItemsCount(DEBRIS_OF_WILLOW_ID) == 0)
				{
					if(Rnd.chance(20))
					{
						st.setCond(14);
						st.giveItems(DEBRIS_OF_WILLOW_ID, 1);
					}
				}
				break;
			}
		}
		return null;
	}
}