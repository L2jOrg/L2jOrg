package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест Wrath Of Ancestors
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _272_WrathOfAncestors extends Quest
{
	//NPC
	private static final int Livina = 30572;
	//Quest Item
	private static final int GraveRobbersHead = 1474;
	//MOB
	private static final int GoblinGraveRobber = 20319;
	private static final int GoblinTombRaiderLeader = 20320;

	public _272_WrathOfAncestors()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Livina);

		addKillId(GoblinGraveRobber);
		addKillId(GoblinTombRaiderLeader);

		addQuestItem(GraveRobbersHead);

		addLevelCheck("seer_livina_q0272_01.htm", 5/*, 16*/);
		addRaceCheck("seer_livina_q0272_00.htm", Race.ORC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.setCond(1);
			htmltext = "seer_livina_q0272_03.htm";
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
			case Livina:
				if (cond == 0)
					htmltext = "seer_livina_q0272_02.htm";
				else if (cond == 1)
					htmltext = "seer_livina_q0272_04.htm";
				else if (cond == 2)
				{
					st.takeItems(GraveRobbersHead, -1);
					st.giveItems(ADENA_ID, 100);
					htmltext = "seer_livina_q0272_05.htm";
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
			st.rollAndGive(GraveRobbersHead, 1, 1, 50, 100);
			if (st.getQuestItemsCount(GraveRobbersHead) >= 50)
				st.setCond(2);
		}
		return null;
	}
}