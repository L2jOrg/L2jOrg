package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _370_AnElderSowsSeeds extends Quest
{
	//npc
	private static final int CASIAN = 30612;
	//mobs
	private static int[] MOBS = {
			20082,
			20084,
			20086,
			20089,
			20090
	};
	//items
	private static int SPB_PAGE = 5916;
	//Collection Kranvel's Spellbooks
	private static int[] CHAPTERS = {
			5917,
			5918,
			5919,
			5920
	};

	public _370_AnElderSowsSeeds()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(CASIAN);

		for(int npcId : MOBS)
			addKillId(npcId);

		addQuestItem(SPB_PAGE);

		addLevelCheck("30612-0a.htm", 28/*, 42*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("30612-1.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30612-6.htm"))
		{
			if(st.getQuestItemsCount(CHAPTERS[0]) > 0 && st.getQuestItemsCount(CHAPTERS[1]) > 0 && st.getQuestItemsCount(CHAPTERS[2]) > 0 && st.getQuestItemsCount(CHAPTERS[3]) > 0)
			{
				long mincount = st.getQuestItemsCount(CHAPTERS[0]);

				for(int itemId : CHAPTERS)
					mincount = Math.min(mincount, st.getQuestItemsCount(itemId));

				for(int itemId : CHAPTERS)
					st.takeItems(itemId, mincount);

				long reward =  230 * mincount;
				st.giveItems(ADENA_ID, reward, 1000);
				htmltext = "30612-8.htm";
			}
			else
				htmltext = "30612-4.htm";
		}
		else if(event.equalsIgnoreCase("30612-9.htm"))
		{
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		switch (npcId)
		{
			case 30612:
				if (cond == 0)
					htmltext = "30612-0.htm";
				else if (cond == 1)
					htmltext = "30612-4.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 1)
		 	st.rollAndGive(SPB_PAGE, 1, 1, Rnd.get(15, 100));
		return null;
	}
}