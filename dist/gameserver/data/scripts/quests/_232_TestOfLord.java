package quests;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Drop;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _232_TestOfLord extends Quest
{
	// NPCs
	private static int Somak = 30510;
	private static int Manakia = 30515;
	private static int Jakal = 30558;
	private static int Sumari = 30564;
	private static int Kakai = 30565;
	private static int Varkees = 30566;
	private static int Tantus = 30567;
	private static int Hatos = 30568;
	private static int Takuna = 30641;
	private static int Chianta = 30642;
	private static int First_Orc = 30643;
	private static int Ancestor_Martankus = 30649;
	// Mobs
	private static int Marsh_Spider = 20233;
	private static int Breka_Orc_Shaman = 20269;
	private static int Breka_Orc_Overlord = 20270;
	private static int Enchanted_Monstereye = 20564;
	private static int Timak_Orc = 20583;
	private static int Timak_Orc_Archer = 20584;
	private static int Timak_Orc_Soldier = 20585;
	private static int Timak_Orc_Warrior = 20586;
	private static int Timak_Orc_Shaman = 20587;
	private static int Timak_Orc_Overlord = 20588;
	private static int Ragna_Orc_Overlord = 20778;
	private static int Ragna_Orc_Seer = 20779;
	// Items
	private static int MARK_OF_LORD = 3390;
	private static int BONE_ARROW = 1341;
	// Quest Items (Drop)
	private static int TIMAK_ORC_SKULL = 3403;
	private static int BREKA_ORC_FANG = 3398;
	private static int RAGNA_ORC_HEAD = 3414;
	private static int RAGNA_CHIEF_NOTICE = 3415;
	private static int MARSH_SPIDER_FEELER = 3407;
	private static int MARSH_SPIDER_FEET = 3408;
	private static int CORNEA_OF_EN_MONSTEREYE = 3410;
	// Quest Items
	private static int ORDEAL_NECKLACE = 3391;
	private static int VARKEES_CHARM = 3392;
	private static int TANTUS_CHARM = 3393;
	private static int HATOS_CHARM = 3394;
	private static int TAKUNA_CHARM = 3395;
	private static int CHIANTA_CHARM = 3396;
	private static int MANAKIAS_ORDERS = 3397;
	private static int MANAKIAS_AMULET = 3399;
	private static int HUGE_ORC_FANG = 3400;
	private static int SUMARIS_LETTER = 3401;
	private static int URUTU_BLADE = 3402;
	private static int SWORD_INTO_SKULL = 3404;
	private static int NERUGA_AXE_BLADE = 3405;
	private static int AXE_OF_CEREMONY = 3406;
	private static int HANDIWORK_SPIDER_BROOCH = 3409;
	private static int MONSTEREYE_WOODCARVING = 3411;
	private static int BEAR_FANG_NECKLACE = 3412;
	private static int MARTANKUS_CHARM = 3413;
	private static int IMMORTAL_FLAME = 3416;

	private static Map<Integer, Drop> DROPLIST = new HashMap<Integer, Drop>();

	public _232_TestOfLord()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Kakai);

		addTalkId(Somak);
		addTalkId(Manakia);
		addTalkId(Jakal);
		addTalkId(Sumari);
		addTalkId(Varkees);
		addTalkId(Tantus);
		addTalkId(Hatos);
		addTalkId(Takuna);
		addTalkId(Chianta);
		addTalkId(First_Orc);
		addTalkId(Ancestor_Martankus);

		DROPLIST.put(Timak_Orc, new Drop(1, 10, 50).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Timak_Orc_Archer, new Drop(1, 10, 55).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Timak_Orc_Soldier, new Drop(1, 10, 60).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Timak_Orc_Warrior, new Drop(1, 10, 65).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Timak_Orc_Shaman, new Drop(1, 10, 70).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Timak_Orc_Overlord, new Drop(1, 10, 75).addItem(TIMAK_ORC_SKULL));
		DROPLIST.put(Breka_Orc_Shaman, new Drop(1, 20, 40).addItem(BREKA_ORC_FANG));
		DROPLIST.put(Breka_Orc_Overlord, new Drop(1, 20, 50).addItem(BREKA_ORC_FANG));
		DROPLIST.put(Ragna_Orc_Overlord, new Drop(4, 1, 100).addItem(RAGNA_ORC_HEAD));
		DROPLIST.put(Ragna_Orc_Seer, new Drop(4, 1, 100).addItem(RAGNA_CHIEF_NOTICE));
		DROPLIST.put(Marsh_Spider, new Drop(1, 10, 100).addItem(MARSH_SPIDER_FEELER).addItem(MARSH_SPIDER_FEET));
		DROPLIST.put(Enchanted_Monstereye, new Drop(1, 20, 90).addItem(CORNEA_OF_EN_MONSTEREYE));

		for(int kill_id : DROPLIST.keySet())
			addKillId(kill_id);

		for(Drop drop : DROPLIST.values())
			for(int item_id : drop.itemList)
				if(!isQuestItem(item_id))
					addQuestItem(item_id);

		addQuestItem(ORDEAL_NECKLACE);
		addQuestItem(VARKEES_CHARM);
		addQuestItem(TANTUS_CHARM);
		addQuestItem(HATOS_CHARM);
		addQuestItem(TAKUNA_CHARM);
		addQuestItem(CHIANTA_CHARM);
		addQuestItem(MANAKIAS_ORDERS);
		addQuestItem(MANAKIAS_AMULET);
		addQuestItem(HUGE_ORC_FANG);
		addQuestItem(SUMARIS_LETTER);
		addQuestItem(URUTU_BLADE);
		addQuestItem(SWORD_INTO_SKULL);
		addQuestItem(NERUGA_AXE_BLADE);
		addQuestItem(AXE_OF_CEREMONY);
		addQuestItem(HANDIWORK_SPIDER_BROOCH);
		addQuestItem(MONSTEREYE_WOODCARVING);
		addQuestItem(BEAR_FANG_NECKLACE);
		addQuestItem(MARTANKUS_CHARM);
		addQuestItem(IMMORTAL_FLAME);

		addRaceCheck("kakai_the_lord_of_flame_q0232_01.htm", Race.ORC);
		addClassIdCheck("kakai_the_lord_of_flame_q0232_02.htm", 50);
		addLevelCheck("kakai_the_lord_of_flame_q0232_03.htm", 39);
	}

	private static void spawn_First_Orc(QuestState st)
	{
		st.addSpawn(First_Orc, 21036, -107690, -3038);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(st.getCond() == 0)
		{
			if(event.equalsIgnoreCase("kakai_the_lord_of_flame_q0232_05.htm"))
			{
				st.giveItems(ORDEAL_NECKLACE, 1);
				st.setCond(1);
			}
		}
		else
			 if(event.equalsIgnoreCase("kakai_the_lord_of_flame_q0232_08.htm"))
			{
				st.takeItems(SWORD_INTO_SKULL, -1);
				st.takeItems(AXE_OF_CEREMONY, -1);
				st.takeItems(MONSTEREYE_WOODCARVING, -1);
				st.takeItems(HANDIWORK_SPIDER_BROOCH, -1);
				st.takeItems(ORDEAL_NECKLACE, -1);
				st.takeItems(HUGE_ORC_FANG, -1);
				st.giveItems(BEAR_FANG_NECKLACE, 1);
				st.setCond(3);
			}
			else if(event.equalsIgnoreCase("atuba_chief_varkees_q0232_02.htm"))
				st.giveItems(VARKEES_CHARM, 1);
			else if(event.equalsIgnoreCase("neruga_chief_tantus_q0232_02.htm"))
				st.giveItems(TANTUS_CHARM, 1);
			else if(event.equalsIgnoreCase("trader_jakaron_q0232_02.htm") && st.getQuestItemsCount(ADENA_ID) >= 1000)
			{
				st.takeItems(ADENA_ID, 1000);
				st.giveItems(NERUGA_AXE_BLADE, 1);
				st.playSound(SOUND_MIDDLE);
			}
			else if(event.equalsIgnoreCase("urutu_chief_hatos_q0232_02.htm"))
				st.giveItems(HATOS_CHARM, 1);
			else if(event.equalsIgnoreCase("dudamara_chief_takuna_q0232_02.htm"))
				st.giveItems(TAKUNA_CHARM, 1);
			else if(event.equalsIgnoreCase("gandi_chief_chianta_q0232_02.htm"))
				st.giveItems(CHIANTA_CHARM, 1);
			else if(event.equalsIgnoreCase("ancestor_martankus_q0232_04.htm") && st.getQuestItemsCount(BEAR_FANG_NECKLACE) > 0)
			{
				st.takeItems(BEAR_FANG_NECKLACE, -1);
				st.giveItems(MARTANKUS_CHARM, 1);
				st.setCond(4);
			}
			else if(event.equalsIgnoreCase("ancestor_martankus_q0232_07.htm"))
			{
				st.setCond(6);
				spawn_First_Orc(st);
			}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_LORD) > 0)
			return COMPLETED_DIALOG;

		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 0)
		{
			if(npcId != Kakai)
				return NO_QUEST_DIALOG;
			return "kakai_the_lord_of_flame_q0232_04.htm";
		}

		long ORDEAL_NECKLACE_COUNT = st.getQuestItemsCount(ORDEAL_NECKLACE);
		long HUGE_ORC_FANG_COUNT = st.getQuestItemsCount(HUGE_ORC_FANG);
		long SWORD_INTO_SKULL_COUNT = st.getQuestItemsCount(SWORD_INTO_SKULL);
		long AXE_OF_CEREMONY_COUNT = st.getQuestItemsCount(AXE_OF_CEREMONY);
		long MONSTEREYE_WOODCARVING_COUNT = st.getQuestItemsCount(MONSTEREYE_WOODCARVING);
		long HANDIWORK_SPIDER_BROOCH_COUNT = st.getQuestItemsCount(HANDIWORK_SPIDER_BROOCH);
		long BEAR_FANG_NECKLACE_COUNT = st.getQuestItemsCount(BEAR_FANG_NECKLACE);
		long MARTANKUS_CHARM_COUNT = st.getQuestItemsCount(MARTANKUS_CHARM);
		long IMMORTAL_FLAME_COUNT = st.getQuestItemsCount(IMMORTAL_FLAME);
		long VARKEES_CHARM_COUNT = st.getQuestItemsCount(VARKEES_CHARM);
		long MANAKIAS_AMULET_COUNT = st.getQuestItemsCount(MANAKIAS_AMULET);
		long MANAKIAS_ORDERS_COUNT = st.getQuestItemsCount(MANAKIAS_ORDERS);
		long BREKA_ORC_FANG_COUNT = st.getQuestItemsCount(BREKA_ORC_FANG);
		long TANTUS_CHARM_COUNT = st.getQuestItemsCount(TANTUS_CHARM);
		long NERUGA_AXE_BLADE_COUNT = st.getQuestItemsCount(NERUGA_AXE_BLADE);
		long HATOS_CHARM_COUNT = st.getQuestItemsCount(HATOS_CHARM);
		long URUTU_BLADE_COUNT = st.getQuestItemsCount(URUTU_BLADE);
		long TIMAK_ORC_SKULL_COUNT = st.getQuestItemsCount(TIMAK_ORC_SKULL);
		long SUMARIS_LETTER_COUNT = st.getQuestItemsCount(SUMARIS_LETTER);
		long TAKUNA_CHARM_COUNT = st.getQuestItemsCount(TAKUNA_CHARM);

		if(npcId == Kakai)
		{
			if(ORDEAL_NECKLACE_COUNT > 0)
				return cond1Complete(st) ? "kakai_the_lord_of_flame_q0232_07.htm" : "kakai_the_lord_of_flame_q0232_06.htm";
			if(BEAR_FANG_NECKLACE_COUNT > 0)
				return "kakai_the_lord_of_flame_q0232_09.htm";
			if(MARTANKUS_CHARM_COUNT > 0)
				return "kakai_the_lord_of_flame_q0232_10.htm";
			if(IMMORTAL_FLAME_COUNT > 0)
			{
				st.takeItems(IMMORTAL_FLAME, -1);
				st.giveItems(MARK_OF_LORD, 1);
				if(!st.getPlayer().getVarBoolean("prof2.3"))
				{
					st.addExpAndSp(135000, 0);
					st.getPlayer().setVar("prof2.3", "1", -1);
				}
				st.finishQuest();
				return "kakai_the_lord_of_flame_q0232_11.htm";
			}
		}

		if(npcId == Varkees && ORDEAL_NECKLACE_COUNT > 0)
		{
			if(HUGE_ORC_FANG_COUNT > 0)
				return "atuba_chief_varkees_q0232_05.htm";
			if(VARKEES_CHARM_COUNT == 0)
				return "atuba_chief_varkees_q0232_01.htm";
			if(MANAKIAS_AMULET_COUNT == 0)
				return "atuba_chief_varkees_q0232_03.htm";
			st.takeItems(VARKEES_CHARM, -1);
			st.takeItems(MANAKIAS_AMULET, -1);
			st.giveItems(HUGE_ORC_FANG, 1);
			if(cond1Complete(st))
				st.setCond(2, SOUND_JACKPOT);
			else
				st.playSound(SOUND_MIDDLE);
			return "atuba_chief_varkees_q0232_04.htm";
		}

		if(npcId == Manakia && ORDEAL_NECKLACE_COUNT > 0)
			if(VARKEES_CHARM_COUNT > 0 && HUGE_ORC_FANG_COUNT == 0)
			{
				if(MANAKIAS_AMULET_COUNT == 0)
				{
					if(MANAKIAS_ORDERS_COUNT == 0)
					{
						st.giveItems(MANAKIAS_ORDERS, 1);
						return "seer_manakia_q0232_01.htm";
					}
					if(BREKA_ORC_FANG_COUNT < 20)
						return "seer_manakia_q0232_02.htm";
					st.takeItems(MANAKIAS_ORDERS, -1);
					st.takeItems(BREKA_ORC_FANG, -1);
					st.giveItems(MANAKIAS_AMULET, 1);
					st.playSound(SOUND_MIDDLE);
					return "seer_manakia_q0232_03.htm";
				}
				else if(MANAKIAS_ORDERS_COUNT == 0)
					return "seer_manakia_q0232_04.htm";
			}
			else if(VARKEES_CHARM_COUNT == 0 && HUGE_ORC_FANG_COUNT > 0 && MANAKIAS_AMULET_COUNT == 0 && MANAKIAS_ORDERS_COUNT == 0)
				return "seer_manakia_q0232_05.htm";

		if(npcId == Tantus)
		{
			if(AXE_OF_CEREMONY_COUNT == 0)
			{
				if(TANTUS_CHARM_COUNT == 0)
					return "neruga_chief_tantus_q0232_01.htm";
				if(NERUGA_AXE_BLADE_COUNT == 0 || st.getQuestItemsCount(BONE_ARROW) < 1000)
					return "neruga_chief_tantus_q0232_03.htm";
				st.takeItems(TANTUS_CHARM, -1);
				st.takeItems(NERUGA_AXE_BLADE, -1);
				st.takeItems(BONE_ARROW, 1000);
				st.giveItems(AXE_OF_CEREMONY, 1);
				if(cond1Complete(st))
					st.setCond(2, SOUND_JACKPOT);
				else
					st.playSound(SOUND_MIDDLE);
				return "neruga_chief_tantus_q0232_04.htm";
			}
			if(TANTUS_CHARM_COUNT == 0)
				return "neruga_chief_tantus_q0232_05.htm";
		}

		if(npcId == Jakal)
		{
			if(TANTUS_CHARM_COUNT > 0 && AXE_OF_CEREMONY_COUNT == 0)
			{
				if(NERUGA_AXE_BLADE_COUNT > 0)
					return "trader_jakaron_q0232_04.htm";
				return st.getQuestItemsCount(ADENA_ID) < 1000 ? "trader_jakaron_q0232_03.htm" : "trader_jakaron_q0232_01.htm";
			}
			if(TANTUS_CHARM_COUNT == 0 && AXE_OF_CEREMONY_COUNT > 0)
				return "trader_jakaron_q0232_05.htm";
		}

		if(npcId == Hatos)
		{
			if(SWORD_INTO_SKULL_COUNT == 0)
			{
				if(HATOS_CHARM_COUNT == 0)
					return "urutu_chief_hatos_q0232_01.htm";
				if(URUTU_BLADE_COUNT == 0 || TIMAK_ORC_SKULL_COUNT < 10)
					return "urutu_chief_hatos_q0232_03.htm";
				st.takeItems(HATOS_CHARM, -1);
				st.takeItems(URUTU_BLADE, -1);
				st.takeItems(TIMAK_ORC_SKULL, -1);
				st.giveItems(SWORD_INTO_SKULL, 1);
				if(cond1Complete(st))
					st.setCond(2, SOUND_JACKPOT);
				else
					st.playSound(SOUND_MIDDLE);
				return "urutu_chief_hatos_q0232_04.htm";
			}
			if(HATOS_CHARM_COUNT == 0)
				return "urutu_chief_hatos_q0232_05.htm";
		}

		if(npcId == Sumari)
			if(HATOS_CHARM_COUNT > 0 && SWORD_INTO_SKULL_COUNT == 0)
			{
				if(URUTU_BLADE_COUNT == 0)
				{
					if(SUMARIS_LETTER_COUNT > 0)
						return "blacksmith_sumari_q0232_02.htm";
					st.giveItems(SUMARIS_LETTER, 1);
					st.playSound(SOUND_MIDDLE);
					return "blacksmith_sumari_q0232_01.htm";
				}
				else if(SUMARIS_LETTER_COUNT == 0)
					return "blacksmith_sumari_q0232_03.htm";
			}
			else if(HATOS_CHARM_COUNT == 0 && SWORD_INTO_SKULL_COUNT > 0 && URUTU_BLADE_COUNT == 0 && SUMARIS_LETTER_COUNT == 0)
				return "blacksmith_sumari_q0232_04.htm";

		if(npcId == Somak)
			if(SWORD_INTO_SKULL_COUNT == 0)
			{
				if(URUTU_BLADE_COUNT == 0 && HATOS_CHARM_COUNT > 0 && SUMARIS_LETTER_COUNT > 0)
				{
					st.takeItems(SUMARIS_LETTER, -1);
					st.giveItems(URUTU_BLADE, 1);
					st.playSound(SOUND_MIDDLE);
					return "seer_somak_q0232_01.htm";
				}
				if(URUTU_BLADE_COUNT > 0 && HATOS_CHARM_COUNT > 0 && SUMARIS_LETTER_COUNT == 0)
					return "seer_somak_q0232_02.htm";
			}
			else if(URUTU_BLADE_COUNT == 0 && HATOS_CHARM_COUNT == 0 && SUMARIS_LETTER_COUNT == 0)
				return "seer_somak_q0232_03.htm";

		if(npcId == Takuna)
		{
			if(HANDIWORK_SPIDER_BROOCH_COUNT == 0)
			{
				if(TAKUNA_CHARM_COUNT == 0)
					return "dudamara_chief_takuna_q0232_01.htm";
				if(st.getQuestItemsCount(MARSH_SPIDER_FEELER) < 10 || st.getQuestItemsCount(MARSH_SPIDER_FEET) < 10)
					return "dudamara_chief_takuna_q0232_03.htm";
				st.takeItems(MARSH_SPIDER_FEELER, -1);
				st.takeItems(MARSH_SPIDER_FEET, -1);
				st.takeItems(TAKUNA_CHARM, -1);
				st.giveItems(HANDIWORK_SPIDER_BROOCH, 1);
				if(cond1Complete(st))
					st.setCond(2, SOUND_JACKPOT);
				else
					st.playSound(SOUND_MIDDLE);
				return "dudamara_chief_takuna_q0232_04.htm";
			}
			if(TAKUNA_CHARM_COUNT == 0)
				return "dudamara_chief_takuna_q0232_05.htm";
		}

		if(npcId == Chianta)
		{
			long CHIANTA_CHARM_COUNT = st.getQuestItemsCount(CHIANTA_CHARM);
			if(MONSTEREYE_WOODCARVING_COUNT == 0)
			{
				if(CHIANTA_CHARM_COUNT == 0)
					return "gandi_chief_chianta_q0232_01.htm";
				if(st.getQuestItemsCount(CORNEA_OF_EN_MONSTEREYE) < 20)
					return "gandi_chief_chianta_q0232_03.htm";
				st.takeItems(CORNEA_OF_EN_MONSTEREYE, -1);
				st.takeItems(CHIANTA_CHARM, -1);
				st.giveItems(MONSTEREYE_WOODCARVING, 1);
				if(cond1Complete(st))
					st.setCond(2, SOUND_JACKPOT);
				else
					st.playSound(SOUND_MIDDLE);
				return "gandi_chief_chianta_q0232_04.htm";
			}
			if(CHIANTA_CHARM_COUNT == 0)
				return "gandi_chief_chianta_q0232_05.htm";
		}

		if(npcId == Ancestor_Martankus)
		{
			if(BEAR_FANG_NECKLACE_COUNT > 0)
				return "ancestor_martankus_q0232_01.htm";
			if(MARTANKUS_CHARM_COUNT > 0)
			{
				if(cond == 5 || st.getQuestItemsCount(RAGNA_CHIEF_NOTICE) > 0 && st.getQuestItemsCount(RAGNA_ORC_HEAD) > 0)
				{
					st.takeItems(MARTANKUS_CHARM, -1);
					st.takeItems(RAGNA_ORC_HEAD, -1);
					st.takeItems(RAGNA_CHIEF_NOTICE, -1);
					st.giveItems(IMMORTAL_FLAME, 1);
					st.playSound(SOUND_MIDDLE);
					return "ancestor_martankus_q0232_06.htm";
				}
				return "ancestor_martankus_q0232_05.htm";
			}
			if(cond == 6 || cond == 7)
				return "ancestor_martankus_q0232_08.htm";
		}

		if(npcId == First_Orc && st.getQuestItemsCount(IMMORTAL_FLAME) > 0)
		{
			st.setCond(7);
			return "first_orc_q0232_01.htm";
		}

		return NO_QUEST_DIALOG;
	}

	private boolean cond1Complete(QuestState st)
	{
		long HUGE_ORC_FANG_COUNT = st.getQuestItemsCount(HUGE_ORC_FANG);
		long SWORD_INTO_SKULL_COUNT = st.getQuestItemsCount(SWORD_INTO_SKULL);
		long AXE_OF_CEREMONY_COUNT = st.getQuestItemsCount(AXE_OF_CEREMONY);
		long MONSTEREYE_WOODCARVING_COUNT = st.getQuestItemsCount(MONSTEREYE_WOODCARVING);
		long HANDIWORK_SPIDER_BROOCH_COUNT = st.getQuestItemsCount(HANDIWORK_SPIDER_BROOCH);
		return HUGE_ORC_FANG_COUNT > 0 && SWORD_INTO_SKULL_COUNT > 0 && AXE_OF_CEREMONY_COUNT > 0 && MONSTEREYE_WOODCARVING_COUNT > 0 && HANDIWORK_SPIDER_BROOCH_COUNT > 0;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();

		Drop _drop = DROPLIST.get(npcId);
		if(_drop == null)
			return null;
		int cond = qs.getCond();

		for(int item_id : _drop.itemList)
		{
			long ORDEAL_NECKLACE_COUNT = qs.getQuestItemsCount(ORDEAL_NECKLACE);
			if(item_id == TIMAK_ORC_SKULL && !(ORDEAL_NECKLACE_COUNT > 0 && qs.getQuestItemsCount(HATOS_CHARM) > 0 && qs.getQuestItemsCount(SWORD_INTO_SKULL) == 0))
				continue;

			if(item_id == BREKA_ORC_FANG && !(ORDEAL_NECKLACE_COUNT > 0 && qs.getQuestItemsCount(VARKEES_CHARM) > 0 && qs.getQuestItemsCount(MANAKIAS_ORDERS) > 0))
				continue;

			if(npcId == Marsh_Spider && !(ORDEAL_NECKLACE_COUNT > 0 && qs.getQuestItemsCount(TAKUNA_CHARM) > 0))
				continue;

			if(npcId == Enchanted_Monstereye && !(ORDEAL_NECKLACE_COUNT > 0 && qs.getQuestItemsCount(CHIANTA_CHARM) > 0))
				continue;

			long count = qs.getQuestItemsCount(item_id);
			if(cond == _drop.condition && count < _drop.maxcount && Rnd.chance(_drop.chance))
			{
				qs.giveItems(item_id, 1, true);
				if(count + 1 >= _drop.maxcount)
				{
					if(cond == 4 && qs.getQuestItemsCount(RAGNA_ORC_HEAD) > 0 && qs.getQuestItemsCount(RAGNA_CHIEF_NOTICE) > 0)
						qs.setCond(5);
				}
				else
					qs.playSound(SOUND_ITEMGET);
			}
		}

		return null;
	}
}