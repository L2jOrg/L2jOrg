package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _303_CollectArrowheads extends Quest
{
	int ORCISH_ARROWHEAD = 963;

	public _303_CollectArrowheads()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30029);

		addTalkId(30029);

		addKillId(20361);

		addQuestItem(ORCISH_ARROWHEAD);

		addLevelCheck("minx_q0303_02.htm", 10/*, 14*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("minx_q0303_04.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case 30029:
				if (cond == 0)
					htmltext = "minx_q0303_03.htm";
				else if (cond == 1)
					htmltext = "minx_q0303_05.htm";
				else if (cond == 2)
				{
					st.takeItems(ORCISH_ARROWHEAD, -1);
					st.giveItems(ADENA_ID, 500);
					htmltext = "minx_q0303_06.htm";
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.giveItems(ORCISH_ARROWHEAD, 1, true);
			if(st.getQuestItemsCount(ORCISH_ARROWHEAD) >= 10)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}