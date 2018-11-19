package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _320_BonesTellFuture extends Quest
{
	//item
	public final int BONE_FRAGMENT = 809;

	public _320_BonesTellFuture()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30359);
		addTalkId(30359);

		addKillId(20517);
		addKillId(20518);

		addQuestItem(BONE_FRAGMENT);

		addLevelCheck("tetrarch_kaitar_q0320_02.htm", 10/*, 18*/);
		addRaceCheck("tetrarch_kaitar_q0320_00.htm", Race.DARKELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("tetrarch_kaitar_q0320_04.htm"))
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
			case 30359:
				if (cond == 0)
					htmltext = "tetrarch_kaitar_q0320_03.htm";
				else if (cond == 1)
					htmltext = "tetrarch_kaitar_q0320_05.htm";
				else if (cond == 2)
				{
					htmltext = "tetrarch_kaitar_q0320_06.htm";
					st.giveItems(ADENA_ID, 500);
					st.takeItems(BONE_FRAGMENT, -1);
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		{
			st.rollAndGive(BONE_FRAGMENT, 1, 1, 10, 10);
			if (st.getQuestItemsCount(BONE_FRAGMENT) >= 10)
				st.setCond(2);
		}
		return null;
	}
}
