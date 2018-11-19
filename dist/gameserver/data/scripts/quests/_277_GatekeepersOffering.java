package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _277_GatekeepersOffering extends Quest
{

	private static final int STARSTONE1_ID = 1572;
	private static final int SCROLL_OF_ESCAPE_ID = 736;

	public _277_GatekeepersOffering()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30576);
		addKillId(20333);
		addQuestItem(STARSTONE1_ID);

		addLevelCheck("gatekeeper_tamil_q0277_01.htm", 15/*, 21*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
				htmltext = "gatekeeper_tamil_q0277_03.htm";
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
			case 30576:
				if (cond == 0)
					htmltext = "gatekeeper_tamil_q0277_02.htm";
				else if (cond == 1)
					htmltext = "gatekeeper_tamil_q0277_04.htm";
				else if (cond == 2)
				{
					htmltext = "gatekeeper_tamil_q0277_05.htm";
					st.takeItems(STARSTONE1_ID, -1);
					st.giveItems(SCROLL_OF_ESCAPE_ID, 2);
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
			st.rollAndGive(STARSTONE1_ID, 1, 1, 20, 33);
			if (st.getQuestItemsCount(STARSTONE1_ID) >= 20)
				st.setCond(2);
		}
		return null;
	}
}