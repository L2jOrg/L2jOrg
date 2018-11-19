package quests;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Drop;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _327_ReclaimTheLand extends Quest
{
	// NPCs
	private static final int Piotur = 30597;
	private static final int Asha = 30313;
	// Quest Items
	private static int TUREK_DOGTAG = 1846;
	private static int TUREK_MEDALLION = 1847;
	private static int CLAY_URN_FRAGMENT = 1848;
	private static int BRASS_TRINKET_PIECE = 1849;
	private static int BRONZE_MIRROR_PIECE = 1850;
	private static int JADE_NECKLACE_BEAD = 1851;
	private static int ANCIENT_CLAY_URN = 1852;
	private static int ANCIENT_BRASS_TIARA = 1853;
	private static int ANCIENT_BRONZE_MIRROR = 1854;
	private static int ANCIENT_JADE_NECKLACE = 1855;
	// Chances
	private static int Exchange_Chance = 80;

	private static Map<Integer, Drop> DROPLIST = new HashMap<Integer, Drop>();
	private static Map<Integer, Integer> EXP = new HashMap<Integer, Integer>();

	public _327_ReclaimTheLand()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Piotur);
		addTalkId(Asha);

		DROPLIST.put(20495, new Drop(1, 0xFFFF, 13).addItem(TUREK_MEDALLION));
		DROPLIST.put(20496, new Drop(1, 0xFFFF, 9).addItem(TUREK_DOGTAG));
		DROPLIST.put(20497, new Drop(1, 0xFFFF, 11).addItem(TUREK_MEDALLION));
		DROPLIST.put(20498, new Drop(1, 0xFFFF, 10).addItem(TUREK_DOGTAG));
		DROPLIST.put(20499, new Drop(1, 0xFFFF, 8).addItem(TUREK_DOGTAG));
		DROPLIST.put(20500, new Drop(1, 0xFFFF, 7).addItem(TUREK_DOGTAG));
		DROPLIST.put(20501, new Drop(1, 0xFFFF, 12).addItem(TUREK_MEDALLION));
		EXP.put(ANCIENT_CLAY_URN, 913);
		EXP.put(ANCIENT_BRASS_TIARA, 1065);
		EXP.put(ANCIENT_BRONZE_MIRROR, 1065);
		EXP.put(ANCIENT_JADE_NECKLACE, 1294);

		for(int kill_id : DROPLIST.keySet())
			addKillId(kill_id);

		addQuestItem(TUREK_MEDALLION);
		addQuestItem(TUREK_DOGTAG);

		addLevelCheck("piotur_q0327_01.htm", 25/*, 34*/);
	}


	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("piotur_q0327_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("piotur_q0327_06.htm"))
		{
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("trader_acellopy_q0327_02.htm") && st.getQuestItemsCount(CLAY_URN_FRAGMENT) >= 5)
		{
			st.takeItems(CLAY_URN_FRAGMENT, 5);
			if(!Rnd.chance(Exchange_Chance))
				return "trader_acellopy_q0327_10.htm";
			st.giveItems(ANCIENT_CLAY_URN, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_03.htm";
		}
		else if(event.equalsIgnoreCase("trader_acellopy_q0327_04.htm") && st.getQuestItemsCount(BRASS_TRINKET_PIECE) >= 5)
		{
			st.takeItems(BRASS_TRINKET_PIECE, 5);
			if(!Rnd.chance(Exchange_Chance))
				return "trader_acellopy_q0327_10.htm";
			st.giveItems(ANCIENT_BRASS_TIARA, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_05.htm";
		}
		else if(event.equalsIgnoreCase("trader_acellopy_q0327_06.htm") && st.getQuestItemsCount(BRONZE_MIRROR_PIECE) >= 5)
		{
			st.takeItems(BRONZE_MIRROR_PIECE, 5);
			if(!Rnd.chance(Exchange_Chance))
				return "trader_acellopy_q0327_10.htm";
			st.giveItems(ANCIENT_BRONZE_MIRROR, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_07.htm";
		}
		else if(event.equalsIgnoreCase("trader_acellopy_q0327_08.htm") && st.getQuestItemsCount(JADE_NECKLACE_BEAD) >= 5)
		{
			st.takeItems(JADE_NECKLACE_BEAD, 5);
			if(!Rnd.chance(Exchange_Chance))
				return "trader_acellopy_q0327_09.htm";
			st.giveItems(ANCIENT_JADE_NECKLACE, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_07.htm";
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Piotur:
				if (cond == 0)
					htmltext = "piotur_q0327_02.htm";
				if (cond == 1)
				{
					long reward = st.getQuestItemsCount(TUREK_DOGTAG) * 8 + st.getQuestItemsCount(TUREK_MEDALLION) * 8;
					if (reward == 0)
						htmltext = "piotur_q0327_04.htm";
					else
					{
						st.takeItems(TUREK_DOGTAG, -1);
						st.takeItems(TUREK_MEDALLION, -1);
						st.giveItems(ADENA_ID, reward, 1000);
						st.playSound(SOUND_MIDDLE);
						htmltext = "piotur_q0327_05.htm";
					}
				}
				break;
			case Asha:
					return "trader_acellopy_q0327_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();

		Drop _drop = DROPLIST.get(npcId);
		if(_drop == null)
			return null;

		if (qs.getCond() == 1)
		{
			if (Rnd.chance(_drop.chance))
			{
				int n = Rnd.get(100);
				if (n < 25)
					qs.giveItems(CLAY_URN_FRAGMENT, 1, true);
				else if (n < 50)
					qs.giveItems(BRASS_TRINKET_PIECE, 1, true);
				else if (n < 75)
					qs.giveItems(BRONZE_MIRROR_PIECE, 1, true);
				else
					qs.giveItems(JADE_NECKLACE_BEAD, 1, true);
			}
			qs.giveItems(_drop.itemList[0], 1, true);
			qs.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}