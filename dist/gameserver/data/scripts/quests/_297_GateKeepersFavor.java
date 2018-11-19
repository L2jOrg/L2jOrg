package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _297_GateKeepersFavor extends Quest
{

	private static final int STARSTONE = 1573;
	private static final int SCROLL_OF_ESCAPE_ID = 736;

	public _297_GateKeepersFavor()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30540);

		addTalkId(30540);

		addKillId(20521);

		addQuestItem(STARSTONE);

		addLevelCheck("gatekeeper_wirphy_q0297_01.htm", 15/*, 21*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gatekeeper_wirphy_q0297_03.htm"))
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
			case 30540:
				if (cond == 0)
					htmltext = "gatekeeper_wirphy_q0297_02.htm";
				else if (cond == 1)
					htmltext = "gatekeeper_wirphy_q0297_04.htm";
				else if (cond == 2)
				{
					htmltext = "gatekeeper_wirphy_q0297_05.htm";
					st.takeItems(STARSTONE, -1);
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
			st.rollAndGive(STARSTONE, 1, 1, 20, 33);
			if (st.getQuestItemsCount(STARSTONE) >= 20)
				st.setCond(2);
		}
		return null;
	}
}