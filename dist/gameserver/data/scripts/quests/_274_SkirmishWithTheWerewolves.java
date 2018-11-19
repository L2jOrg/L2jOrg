package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _274_SkirmishWithTheWerewolves extends Quest
{

	private static final int MARAKU_WEREWOLF_HEAD = 1477;

	public _274_SkirmishWithTheWerewolves()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30569);

		addKillId(20363);
		addKillId(20364);

		addQuestItem(MARAKU_WEREWOLF_HEAD);

		addLevelCheck("prefect_brukurse_q0274_01.htm", 9/*, 18*/);
		addRaceCheck("prefect_brukurse_q0274_00.htm", Race.ORC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("prefect_brukurse_q0274_03.htm"))
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
			case 30569:
				if (cond == 0)
					htmltext = "prefect_brukurse_q0274_02.htm";
				else if (cond == 1)
					htmltext = "prefect_brukurse_q0274_04.htm";
				else if (cond == 2)
				{
					st.takeItems(MARAKU_WEREWOLF_HEAD, -1);
					st.giveItems(ADENA_ID, 200);
					htmltext = "prefect_brukurse_q0274_05.htm";
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
			st.rollAndGive(MARAKU_WEREWOLF_HEAD, 1, 1, 40, 100);
			if (st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) >= 40)
				st.setCond(2);
		}
		return null;
	}
}