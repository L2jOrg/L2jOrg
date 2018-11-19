package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест Scent Of Death
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _319_ScentOfDeath extends Quest
{
	//NPC
	private static final int MINALESS = 30138;
	//Quest Item
	private static final int ZombieSkin = 1045;

	public _319_ScentOfDeath()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(MINALESS);
		addTalkId(MINALESS);

		addKillId(20015);
		addKillId(20020);

		addQuestItem(ZombieSkin);

		addLevelCheck("mina_q0319_02.htm", 11/*, 18*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("mina_q0319_04.htm"))
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
			case MINALESS:
				if (cond == 0)
					htmltext = "mina_q0319_03.htm";
				else if (cond == 1)
					htmltext = "mina_q0319_05.htm";
				else if (cond == 2)
				{
					htmltext = "mina_q0319_06.htm";
					st.takeItems(ZombieSkin, -1);
					st.giveItems(ADENA_ID, 500);
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if (npcId == 20015)
				st.rollAndGive(ZombieSkin, 1, 1, 5, 20);
			else if (npcId == 20020)
				st.rollAndGive(ZombieSkin, 1, 1, 5, 25);

			if (st.getQuestItemsCount(ZombieSkin) >= 5)
				st.setCond(2);
		}
		return null;
	}
}
