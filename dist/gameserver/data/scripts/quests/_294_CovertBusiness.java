package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _294_CovertBusiness extends Quest
{
	public static int BatFang = 1491;
	public static int RingOfRaccoon = 1508;

	public static int BarbedBat = 20370;
	public static int BladeBat = 20480;

	public static final int Keef = 30534;

	public _294_CovertBusiness()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(Keef);
		addTalkId(Keef);

		addKillId(BarbedBat);
		addKillId(BladeBat);

		addQuestItem(BatFang);

		addLevelCheck("elder_keef_q0294_01.htm", 10/*, 16*/);
		addRaceCheck("elder_keef_q0294_00.htm", Race.DWARF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("elder_keef_q0294_03.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		switch (npcId)
		{
			case Keef:
				if (cond == 0)
					htmltext = "elder_keef_q0294_02.htm";
				else if (cond == 1)
					htmltext = "elder_keef_q0294_04.htm";
				else if (cond == 2)
				{
						if (st.getQuestItemsCount(RingOfRaccoon) < 1)
						{
							st.giveItems(RingOfRaccoon, 1);
							htmltext = "elder_keef_q0294_05.htm";
						}
						else
						{
							st.giveItems(ADENA_ID, 180);
							htmltext = "elder_keef_q0294_06.htm";
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
		if(st.getCond() == 1) {
			st.rollAndGive(BatFang, 1, 1, 100, 100);
			if (st.getQuestItemsCount(BatFang) >= 100)
				st.setCond(2);
		}
		return null;
	}
}