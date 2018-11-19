package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _354_ConquestofAlligatorIsland extends Quest
{
	//npc
	public final int MORITA = 30937;
	//mobs
	public final int[] MONSTERS = { 20804, 20805, 20806,20807, 20808, 20793 };
	//items
	public final int ALLIGATOR_TOOTH = 5863;
	public final int CHANCE = 35;

	public _354_ConquestofAlligatorIsland()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(MORITA);

		addKillId(MONSTERS);
		addQuestItem(ALLIGATOR_TOOTH);
		addLevelCheck("30895-00.htm", 38/*, 49*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30895-02.htm"))
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
		switch(npcId)
		{
			case MORITA:
				if(cond == 0)
					htmltext = "30895-01.htm";
				else if(cond == 1)
					htmltext = "30895-03.htm";
				else if(cond == 2)
				{
					st.giveItems(57, 2000);
					st.takeAllItems(ALLIGATOR_TOOTH, -1);
					htmltext = "30895-04.htm";
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
			if(st.rollAndGive(ALLIGATOR_TOOTH, 1, 1, 400, CHANCE))
				st.setCond(2);
		}
		return null;
	}
}