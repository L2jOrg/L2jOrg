package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _295_DreamsOfTheSkies extends Quest
{
	public static int FLOATING_STONE = 1492;
	public static int RING_OF_FIREFLY = 1509;

	public static final int Arin = 30536;
	public static int MagicalWeaver = 20153;

	public _295_DreamsOfTheSkies()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(Arin);
		addTalkId(Arin);
		addKillId(MagicalWeaver);

		addQuestItem(FLOATING_STONE);

		addLevelCheck("elder_arin_q0295_01.htm", 11/*, 15*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("elder_arin_q0295_03.htm"))
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
			case Arin:
				if (cond == 0)
					htmltext = "elder_arin_q0295_02.htm";
				else if (cond == 1)
					htmltext = "elder_arin_q0295_04.htm";
				else if (cond == 2)
				{

					if (st.getQuestItemsCount(RING_OF_FIREFLY) < 1)
					{
						htmltext = "elder_arin_q0295_05.htm";
						st.giveItems(RING_OF_FIREFLY, 1);
					}
					else
					{
						htmltext = "elder_arin_q0295_06.htm";
						st.giveItems(ADENA_ID, 180);
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
		if(st.getCond() == 1)
		{
			st.rollAndGive(FLOATING_STONE, 1, 1, 50, 100);
			if (st.getQuestItemsCount(FLOATING_STONE) >= 50)
				st.setCond(2);
		}
		return null;
	}
}