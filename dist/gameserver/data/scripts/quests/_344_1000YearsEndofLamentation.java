package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _344_1000YearsEndofLamentation extends Quest
{
	// Quest Item's
	private static final int ARTICLES_DEAD_HEROES = 4269;

	// NPC's
	private static final int GILMORE = 30754;

	// Chance's
	private static final int CHANCE = 100; // TODO: Check.

	private static final int ADENA_REWARD_COUNT = 14053;

	public _344_1000YearsEndofLamentation()
	{
		super(PARTY_ONE, REPEATABLE);

		addStartNpc(GILMORE);

		for(int mob = 20236; mob <= 20240; mob++)
			addKillId(mob);

		addQuestItem(ARTICLES_DEAD_HEROES);
		addLevelCheck("30754-01.htm", 45/*, 52*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30754-04.htm"))
			st.setCond(1);
		else if(event.equalsIgnoreCase("30754-07.htm"))
		{
			st.giveItems(ADENA_ID, ADENA_REWARD_COUNT);
			st.finishQuest();
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
			case GILMORE:
				if(cond == 0)
					htmltext = "30754-02.htm";
				else if(cond == 1)
					htmltext = "30754-05.htm";
				else if(cond == 2)
					htmltext = "30754-06.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId >= 20236 && npcId <= 20240)
		{
			if(cond == 1)
			{
				if(st.rollAndGive(ARTICLES_DEAD_HEROES, 1, 1, 1000, CHANCE))
					st.setCond(2);
			}
		}
		return null;
	}
}