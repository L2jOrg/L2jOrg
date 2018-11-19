package quests;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


public final class _233_TestOfWarspirit extends Quest
{
	// NPCs
	private static int Somak = 30510;
	private static int Vivyan = 30030;
	private static int Sarien = 30436;
	private static int Racoy = 30507;
	private static int Manakia = 30515;
	private static int Orim = 30630;
	private static int Ancestor_Martankus = 30649;
	private static int Pekiron = 30682;
	// Mobs
	private static int Porta = 20213;
	private static int Excuro = 20214;
	private static int Mordeo = 20215;
	private static int Noble_Ant = 20089;
	private static int Noble_Ant_Leader = 20090;
	private static int Leto_Lizardman_Shaman = 20581;
	private static int Leto_Lizardman_Overlord = 20582;
	private static int Medusa = 20158;
	private static int Stenoa_Gorgon_Queen = 27108;
	private static int Tamlin_Orc = 20601;
	private static int Tamlin_Orc_Archer = 20602;
	// Items
	private static int MARK_OF_WARSPIRIT = 2879;
	// Quest Items
	private static int VENDETTA_TOTEM = 2880;
	private static int TAMLIN_ORC_HEAD = 2881;
	private static int WARSPIRIT_TOTEM = 2882;
	private static int ORIMS_CONTRACT = 2883;
	private static int PORTAS_EYE = 2884;
	private static int EXCUROS_SCALE = 2885;
	private static int MORDEOS_TALON = 2886;
	private static int BRAKIS_REMAINS1 = 2887;
	private static int PEKIRONS_TOTEM = 2888;
	private static int TONARS_SKULL = 2889;
	private static int TONARS_RIB_BONE = 2890;
	private static int TONARS_SPINE = 2891;
	private static int TONARS_ARM_BONE = 2892;
	private static int TONARS_THIGH_BONE = 2893;
	private static int TONARS_REMAINS1 = 2894;
	private static int MANAKIAS_TOTEM = 2895;
	private static int HERMODTS_SKULL = 2896;
	private static int HERMODTS_RIB_BONE = 2897;
	private static int HERMODTS_SPINE = 2898;
	private static int HERMODTS_ARM_BONE = 2899;
	private static int HERMODTS_THIGH_BONE = 2900;
	private static int HERMODTS_REMAINS1 = 2901;
	private static int RACOYS_TOTEM = 2902;
	private static int VIVIANTES_LETTER = 2903;
	private static int INSECT_DIAGRAM_BOOK = 2904;
	private static int KIRUNAS_SKULL = 2905;
	private static int KIRUNAS_RIB_BONE = 2906;
	private static int KIRUNAS_SPINE = 2907;
	private static int KIRUNAS_ARM_BONE = 2908;
	private static int KIRUNAS_THIGH_BONE = 2909;
	private static int KIRUNAS_REMAINS1 = 2910;
	private static int BRAKIS_REMAINS2 = 2911;
	private static int TONARS_REMAINS2 = 2912;
	private static int HERMODTS_REMAINS2 = 2913;
	private static int KIRUNAS_REMAINS2 = 2914;

	private static int[] Noble_Ant_Drops = {
			KIRUNAS_THIGH_BONE,
			KIRUNAS_ARM_BONE,
			KIRUNAS_SPINE,
			KIRUNAS_RIB_BONE,
			KIRUNAS_SKULL
	};
	private static int[] Leto_Lizardman_Drops = {
			TONARS_SKULL,
			TONARS_RIB_BONE,
			TONARS_SPINE,
			TONARS_ARM_BONE,
			TONARS_THIGH_BONE
	};
	private static int[] Medusa_Drops = {
			HERMODTS_RIB_BONE,
			HERMODTS_SPINE,
			HERMODTS_THIGH_BONE,
			HERMODTS_ARM_BONE
	};

	public _233_TestOfWarspirit()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Somak);

		addTalkId(Vivyan);
		addTalkId(Sarien);
		addTalkId(Racoy);
		addTalkId(Manakia);
		addTalkId(Orim);
		addTalkId(Ancestor_Martankus);
		addTalkId(Pekiron);

		addKillId(Porta);
		addKillId(Excuro);
		addKillId(Mordeo);
		addKillId(Noble_Ant);
		addKillId(Noble_Ant_Leader);
		addKillId(Leto_Lizardman_Shaman);
		addKillId(Leto_Lizardman_Overlord);
		addKillId(Medusa);
		addKillId(Stenoa_Gorgon_Queen);
		addKillId(Tamlin_Orc);
		addKillId(Tamlin_Orc_Archer);

		addQuestItem(VENDETTA_TOTEM);
		addQuestItem(TAMLIN_ORC_HEAD);
		addQuestItem(WARSPIRIT_TOTEM);
		addQuestItem(ORIMS_CONTRACT);
		addQuestItem(PORTAS_EYE);
		addQuestItem(EXCUROS_SCALE);
		addQuestItem(MORDEOS_TALON);
		addQuestItem(BRAKIS_REMAINS1);
		addQuestItem(PEKIRONS_TOTEM);
		addQuestItem(TONARS_SKULL);
		addQuestItem(TONARS_RIB_BONE);
		addQuestItem(TONARS_SPINE);
		addQuestItem(TONARS_ARM_BONE);
		addQuestItem(TONARS_THIGH_BONE);
		addQuestItem(TONARS_REMAINS1);
		addQuestItem(MANAKIAS_TOTEM);
		addQuestItem(HERMODTS_SKULL);
		addQuestItem(HERMODTS_RIB_BONE);
		addQuestItem(HERMODTS_SPINE);
		addQuestItem(HERMODTS_ARM_BONE);
		addQuestItem(HERMODTS_THIGH_BONE);
		addQuestItem(HERMODTS_REMAINS1);
		addQuestItem(RACOYS_TOTEM);
		addQuestItem(VIVIANTES_LETTER);
		addQuestItem(INSECT_DIAGRAM_BOOK);
		addQuestItem(KIRUNAS_SKULL);
		addQuestItem(KIRUNAS_RIB_BONE);
		addQuestItem(KIRUNAS_SPINE);
		addQuestItem(KIRUNAS_ARM_BONE);
		addQuestItem(KIRUNAS_THIGH_BONE);
		addQuestItem(KIRUNAS_REMAINS1);
		addQuestItem(BRAKIS_REMAINS2);
		addQuestItem(TONARS_REMAINS2);
		addQuestItem(HERMODTS_REMAINS2);
		addQuestItem(KIRUNAS_REMAINS2);

		addRaceCheck("seer_somak_q0233_01.htm", Race.ORC);
		addClassIdCheck("seer_somak_q0233_02.htm", 50);
		addLevelCheck("seer_somak_q0233_03.htm", 39);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("seer_somak_q0233_05.htm"))
			st.setCond(1);
		else if(event.equalsIgnoreCase("orim_the_shadow_q0233_04.htm"))
			st.giveItems(ORIMS_CONTRACT, 1);
		else if(event.equalsIgnoreCase("seer_pekiron_q0233_02.htm"))
			st.giveItems(PEKIRONS_TOTEM, 1);
		else if(event.equalsIgnoreCase("seer_manakia_q0233_02.htm"))
			st.giveItems(MANAKIAS_TOTEM, 1);
		else if(event.equalsIgnoreCase("seer_racoy_q0233_02.htm"))
			st.giveItems(RACOYS_TOTEM, 1);
		else if(event.equalsIgnoreCase("vivi_q0233_04.htm"))
			st.giveItems(VIVIANTES_LETTER, 1);
		else if(event.equalsIgnoreCase("ancestor_martankus_q0233_03.htm") && st.getQuestItemsCount(WARSPIRIT_TOTEM) > 0)
		{
			st.takeItems(WARSPIRIT_TOTEM, -1);
			st.takeItems(BRAKIS_REMAINS2, -1);
			st.takeItems(HERMODTS_REMAINS2, -1);
			st.takeItems(KIRUNAS_REMAINS2, -1);
			st.takeItems(TAMLIN_ORC_HEAD, -1);
			st.takeItems(TONARS_REMAINS2, -1);
			st.giveItems(MARK_OF_WARSPIRIT, 1);
			if(!st.getPlayer().getVarBoolean("prof2.3"))
			{
				st.addExpAndSp(124200, 0);
				st.getPlayer().setVar("prof2.3", "1", -1);
			}
			st.finishQuest();
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_WARSPIRIT) > 0)
			return COMPLETED_DIALOG;

		int npcId = npc.getNpcId();
		if(st.getCond() == 0)
		{
			if(npcId != Somak)
				return NO_QUEST_DIALOG;
			return "seer_somak_q0233_04.htm";
		}

		if(npcId == Somak)
		{
			if(st.getQuestItemsCount(VENDETTA_TOTEM) > 0)
			{
				if(st.getQuestItemsCount(TAMLIN_ORC_HEAD) < 13)
					return "seer_somak_q0233_08.htm";
				st.takeItems(VENDETTA_TOTEM, -1);
				st.giveItems(WARSPIRIT_TOTEM, 1);
				st.giveItems(BRAKIS_REMAINS2, 1);
				st.giveItems(HERMODTS_REMAINS2, 1);
				st.giveItems(KIRUNAS_REMAINS2, 1);
				st.giveItems(TONARS_REMAINS2, 1);
				st.playSound(SOUND_MIDDLE);
				return "seer_somak_q0233_09.htm";
			}
			if(st.getQuestItemsCount(WARSPIRIT_TOTEM) > 0)
				return "seer_somak_q0233_10.htm";
			if(st.getQuestItemsCount(BRAKIS_REMAINS1) == 0 || st.getQuestItemsCount(HERMODTS_REMAINS1) == 0 || st.getQuestItemsCount(KIRUNAS_REMAINS1) == 0 || st.getQuestItemsCount(TONARS_REMAINS1) == 0)
				return "seer_somak_q0233_06.htm";
			st.takeItems(BRAKIS_REMAINS1, -1);
			st.takeItems(HERMODTS_REMAINS1, -1);
			st.takeItems(KIRUNAS_REMAINS1, -1);
			st.takeItems(TONARS_REMAINS1, -1);
			st.giveItems(VENDETTA_TOTEM, 1);
			st.playSound(SOUND_MIDDLE);
			return "seer_somak_q0233_07.htm";
		}

		if(npcId == Orim)
		{
			if(st.getQuestItemsCount(ORIMS_CONTRACT) > 0)
			{
				if(st.getQuestItemsCount(PORTAS_EYE) < 10 || st.getQuestItemsCount(EXCUROS_SCALE) < 10 || st.getQuestItemsCount(MORDEOS_TALON) < 10)
					return "orim_the_shadow_q0233_05.htm";
				st.takeItems(ORIMS_CONTRACT, -1);
				st.takeItems(PORTAS_EYE, -1);
				st.takeItems(EXCUROS_SCALE, -1);
				st.takeItems(MORDEOS_TALON, -1);
				st.giveItems(BRAKIS_REMAINS1, 1);
				st.playSound(SOUND_MIDDLE);
				return "orim_the_shadow_q0233_06.htm";
			}
			if(st.getQuestItemsCount(BRAKIS_REMAINS1) == 0 && st.getQuestItemsCount(BRAKIS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
				return "orim_the_shadow_q0233_01.htm";
			return "orim_the_shadow_q0233_07.htm";
		}

		if(npcId == Pekiron)
		{
			if(st.getQuestItemsCount(PEKIRONS_TOTEM) > 0)
			{
				for(int drop_id : Leto_Lizardman_Drops)
					if(st.getQuestItemsCount(drop_id) == 0)
						return "seer_pekiron_q0233_03.htm";
				st.takeItems(PEKIRONS_TOTEM, -1);
				for(int drop_id : Leto_Lizardman_Drops)
					if(st.getQuestItemsCount(drop_id) == 0)
						st.takeItems(drop_id, -1);
				st.giveItems(TONARS_REMAINS1, 1);
				st.playSound(SOUND_MIDDLE);
				return "seer_pekiron_q0233_04.htm";
			}
			if(st.getQuestItemsCount(TONARS_REMAINS1) == 0 && st.getQuestItemsCount(TONARS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
				return "seer_pekiron_q0233_01.htm";
			return "seer_pekiron_q0233_05.htm";
		}

		if(npcId == Manakia)
		{
			if(st.getQuestItemsCount(MANAKIAS_TOTEM) > 0)
			{
				if(st.getQuestItemsCount(HERMODTS_SKULL) == 0)
					return "seer_manakia_q0233_03.htm";
				for(int drop_id : Medusa_Drops)
					if(st.getQuestItemsCount(drop_id) == 0)
						return "seer_manakia_q0233_03.htm";
				st.takeItems(MANAKIAS_TOTEM, -1);
				st.takeItems(HERMODTS_SKULL, -1);
				for(int drop_id : Medusa_Drops)
					if(st.getQuestItemsCount(drop_id) == 0)
						st.takeItems(drop_id, -1);
				st.giveItems(HERMODTS_REMAINS1, 1);
				st.playSound(SOUND_MIDDLE);
				return "seer_manakia_q0233_04.htm";
			}
			if(st.getQuestItemsCount(HERMODTS_REMAINS1) == 0 && st.getQuestItemsCount(HERMODTS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
				return "seer_manakia_q0233_01.htm";
			if(st.getQuestItemsCount(RACOYS_TOTEM) == 0 && (st.getQuestItemsCount(KIRUNAS_REMAINS2) > 0 || st.getQuestItemsCount(WARSPIRIT_TOTEM) > 0 || st.getQuestItemsCount(BRAKIS_REMAINS2) > 0 || st.getQuestItemsCount(HERMODTS_REMAINS2) > 0 || st.getQuestItemsCount(TAMLIN_ORC_HEAD) > 0 || st.getQuestItemsCount(TONARS_REMAINS2) > 0))
				return "seer_manakia_q0233_05.htm";
		}

		if(npcId == Racoy)
			if(st.getQuestItemsCount(RACOYS_TOTEM) > 0)
			{
				if(st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0)
					return st.getQuestItemsCount(VIVIANTES_LETTER) == 0 ? "seer_racoy_q0233_03.htm" : "seer_racoy_q0233_04.htm";
				if(st.getQuestItemsCount(VIVIANTES_LETTER) == 0)
				{
					for(int drop_id : Noble_Ant_Drops)
						if(st.getQuestItemsCount(drop_id) == 0)
							return "seer_racoy_q0233_05.htm";
					st.takeItems(RACOYS_TOTEM, -1);
					st.takeItems(INSECT_DIAGRAM_BOOK, -1);
					for(int drop_id : Noble_Ant_Drops)
						if(st.getQuestItemsCount(drop_id) == 0)
							st.takeItems(drop_id, -1);
					st.giveItems(KIRUNAS_REMAINS1, 1);
					st.playSound(SOUND_MIDDLE);
					return "seer_racoy_q0233_06.htm";
				}
			}
			else
			{
				if(st.getQuestItemsCount(KIRUNAS_REMAINS1) == 0 && st.getQuestItemsCount(KIRUNAS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
					return "seer_racoy_q0233_01.htm";
				return "seer_racoy_q0233_07.htm";
			}

		if(npcId == Vivyan)
			if(st.getQuestItemsCount(RACOYS_TOTEM) > 0)
			{
				if(st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0)
					return st.getQuestItemsCount(VIVIANTES_LETTER) == 0 ? "vivi_q0233_01.htm" : "vivi_q0233_05.htm";
				if(st.getQuestItemsCount(VIVIANTES_LETTER) == 0)
					return "vivi_q0233_06.htm";
			}
			else if(st.getQuestItemsCount(KIRUNAS_REMAINS1) == 0 && st.getQuestItemsCount(KIRUNAS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
				return "vivi_q0233_07.htm";

		if(npcId == Sarien)
			if(st.getQuestItemsCount(RACOYS_TOTEM) > 0)
			{
				if(st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) == 0 && st.getQuestItemsCount(VIVIANTES_LETTER) > 0)
				{
					st.takeItems(VIVIANTES_LETTER, -1);
					st.giveItems(INSECT_DIAGRAM_BOOK, 1);
					st.playSound(SOUND_MIDDLE);
					return "trader_salient_q0233_01.htm";
				}
				if(st.getQuestItemsCount(VIVIANTES_LETTER) == 0 && st.getQuestItemsCount(INSECT_DIAGRAM_BOOK) > 0)
					return "trader_salient_q0233_02.htm";
			}
			else if(st.getQuestItemsCount(KIRUNAS_REMAINS1) == 0 && st.getQuestItemsCount(KIRUNAS_REMAINS2) == 0 && st.getQuestItemsCount(VENDETTA_TOTEM) == 0)
				return "trader_salient_q0233_03.htm";

		if(npcId == Ancestor_Martankus && st.getQuestItemsCount(WARSPIRIT_TOTEM) > 0)
			return "ancestor_martankus_q0233_01.htm";

		return NO_QUEST_DIALOG;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();

		if(npcId == Porta && qs.getQuestItemsCount(ORIMS_CONTRACT) > 0 && qs.getQuestItemsCount(PORTAS_EYE) < 10)
		{
			qs.giveItems(PORTAS_EYE, 1, true);
			qs.playSound(qs.getQuestItemsCount(PORTAS_EYE) >= 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
		}
		else if(npcId == Excuro && qs.getQuestItemsCount(ORIMS_CONTRACT) > 0 && qs.getQuestItemsCount(EXCUROS_SCALE) < 10)
		{
			qs.giveItems(EXCUROS_SCALE, 1, true);
			qs.playSound(qs.getQuestItemsCount(EXCUROS_SCALE) >= 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
		}
		else if(npcId == Mordeo && qs.getQuestItemsCount(ORIMS_CONTRACT) > 0 && qs.getQuestItemsCount(MORDEOS_TALON) < 10)
		{
			qs.giveItems(MORDEOS_TALON, 1, true);
			qs.playSound(qs.getQuestItemsCount(MORDEOS_TALON) >= 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
		}
		else if((npcId == Noble_Ant || npcId == Noble_Ant_Leader) && qs.getQuestItemsCount(RACOYS_TOTEM) > 0)
		{
			List<Integer> drops = new ArrayList<Integer>();
			for(int drop_id : Noble_Ant_Drops)
				if(qs.getQuestItemsCount(drop_id) == 0)
					drops.add(drop_id);
			if(drops.size() > 0 && Rnd.chance(30))
			{
				int drop_id = drops.get(Rnd.get(drops.size()));
				qs.giveItems(drop_id, 1);
				qs.playSound(drops.size() >= 1 ? SOUND_MIDDLE : SOUND_ITEMGET);
			}
			drops.clear();
			drops = null;
		}
		else if((npcId == Leto_Lizardman_Shaman || npcId == Leto_Lizardman_Overlord) && qs.getQuestItemsCount(PEKIRONS_TOTEM) > 0)
		{
			List<Integer> drops = new ArrayList<Integer>();
			for(int drop_id : Leto_Lizardman_Drops)
				if(qs.getQuestItemsCount(drop_id) == 0)
					drops.add(drop_id);
			if(drops.size() > 0 && Rnd.chance(25))
			{
				int drop_id = drops.get(Rnd.get(drops.size()));
				qs.giveItems(drop_id, 1);
				qs.playSound(drops.size() >= 1 ? SOUND_MIDDLE : SOUND_ITEMGET);
			}
			drops.clear();
			drops = null;
		}
		else if(npcId == Medusa && qs.getQuestItemsCount(MANAKIAS_TOTEM) > 0)
		{
			List<Integer> drops = new ArrayList<Integer>();
			for(int drop_id : Medusa_Drops)
				if(qs.getQuestItemsCount(drop_id) == 0)
					drops.add(drop_id);
			if(drops.size() > 0 && Rnd.chance(30))
			{
				int drop_id = drops.get(Rnd.get(drops.size()));
				qs.giveItems(drop_id, 1);
				qs.playSound(drops.size() >= 1 && qs.getQuestItemsCount(HERMODTS_SKULL) > 0 ? SOUND_MIDDLE : SOUND_ITEMGET);
			}
			drops.clear();
			drops = null;
		}
		else if(npcId == Stenoa_Gorgon_Queen && qs.getQuestItemsCount(MANAKIAS_TOTEM) > 0 && qs.getQuestItemsCount(HERMODTS_SKULL) == 0 && Rnd.chance(30))
		{
			qs.giveItems(HERMODTS_SKULL, 1);
			boolean _allset = true;
			for(int drop_id : Medusa_Drops)
				if(qs.getQuestItemsCount(drop_id) == 0)
				{
					_allset = false;
					break;
				}
			qs.playSound(_allset ? SOUND_MIDDLE : SOUND_ITEMGET);
		}
		else if((npcId == Tamlin_Orc || npcId == Tamlin_Orc_Archer) && qs.getQuestItemsCount(VENDETTA_TOTEM) > 0 && qs.getQuestItemsCount(TAMLIN_ORC_HEAD) < 13)
			if(Rnd.chance(npcId == Tamlin_Orc ? 30 : 50))
			{
				qs.giveItems(TAMLIN_ORC_HEAD, 1, true);
				qs.playSound(qs.getQuestItemsCount(TAMLIN_ORC_HEAD) >= 13 ? SOUND_MIDDLE : SOUND_ITEMGET);
			}

		return null;
	}
}