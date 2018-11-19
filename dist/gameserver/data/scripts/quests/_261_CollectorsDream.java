package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _261_CollectorsDream extends Quest
{
	int GIANT_SPIDER_LEG = 1087;

	public _261_CollectorsDream()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30222);

		addTalkId(30222);

		addKillId(20308);
		addKillId(20460);
		addKillId(20466);

		addQuestItem(GIANT_SPIDER_LEG);

		addLevelCheck("moneylender_alshupes_q0261_01.htm", 15/*, 21*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.intern().equalsIgnoreCase("moneylender_alshupes_q0261_03.htm"))
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
			case 30222:
				if (cond == 0)
					htmltext = "moneylender_alshupes_q0261_02.htm";
				else if (cond == 1)
					htmltext = "moneylender_alshupes_q0261_04.htm";
				else if (cond == 2)
				{
					st.takeItems(GIANT_SPIDER_LEG, -1);
					st.giveItems(ADENA_ID, 700);
					htmltext = "moneylender_alshupes_q0261_05.htm";
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
			st.giveItems(GIANT_SPIDER_LEG, 1, true);
			if(st.getQuestItemsCount(GIANT_SPIDER_LEG) >= 8)
				st.setCond(2);
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}