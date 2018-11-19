package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _324_SweetestVenom extends Quest
{
	//NPCs
	private static final int ASTARON = 30351;
	//Mobs
	private static int Prowler = 20034;
	private static int Venomous_Spider = 20038;
	private static int Arachnid_Tracker = 20043;
	//Items
	private static int VENOM_SAC = 1077;
	//Chances
	private static int VENOM_SAC_BASECHANCE = 60;

	public _324_SweetestVenom()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(ASTARON);
		addKillId(Prowler);
		addKillId(Venomous_Spider);
		addKillId(Arachnid_Tracker);
		addQuestItem(VENOM_SAC);

		addLevelCheck("astaron_q0324_02.htm", 18, 23);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case ASTARON:
				if (cond == 0)
					htmltext = "astaron_q0324_03.htm";
				else if (cond == 1)
					htmltext = "astaron_q0324_05.htm";
				else if (cond == 2)
				{
					htmltext = "astaron_q0324_06.htm";
					st.takeItems(VENOM_SAC, -1);
					st.giveItems(ADENA_ID, 3200);
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("astaron_q0324_04.htm"))
		{
			st.setCond(1);
		}
		return event;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			int _chance = VENOM_SAC_BASECHANCE + (npc.getNpcId() - Prowler) / 4 * 12;
			qs.rollAndGive(VENOM_SAC, 1, 1, 10, _chance);
			if(qs.getQuestItemsCount(VENOM_SAC) >= 10)
				qs.setCond(2);
		}
		return null;
	}
}