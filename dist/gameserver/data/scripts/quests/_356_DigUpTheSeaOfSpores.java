package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _356_DigUpTheSeaOfSpores extends Quest
{
	//NPC
	private static final int GAUEN = 30717;

	//MOBS
	private static final int SPORE_ZOMBIE = 20562;
	private static final int ROTTING_TREE = 20558;

	//QUEST ITEMS
	private static final int CARNIVORE_SPORE = 5865;
	private static final int HERBIBOROUS_SPORE = 5866;

	public _356_DigUpTheSeaOfSpores()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(GAUEN);
		addKillId(SPORE_ZOMBIE, ROTTING_TREE);
		addQuestItem(CARNIVORE_SPORE, HERBIBOROUS_SPORE);
		addLevelCheck(NO_QUEST_DIALOG, 43, 51);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("magister_gauen_q0356_06.htm"))
			st.setCond(1);
		else if(event.equalsIgnoreCase("magister_gauen_q0356_11.htm") || event.equalsIgnoreCase("magister_gauen_q0356_13.htm"))
		{
			if(st.haveQuestItem(CARNIVORE_SPORE, 100) && st.haveQuestItem(HERBIBOROUS_SPORE, 100))
			{
				st.takeItems(CARNIVORE_SPORE, -1);
				st.takeItems(HERBIBOROUS_SPORE, -1);
				if(event.equalsIgnoreCase("magister_gauen_q0356_11.htm"))
					st.giveItems(ADENA_ID, 1300);
				else
					st.giveItems(ADENA_ID, 3000);
				st.finishQuest();
			}
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
			case GAUEN:
				if(cond == 0)
					htmltext = "magister_gauen_q0356_02.htm";
				else if(cond == 1)
					htmltext = "magister_gauen_q0356_07.htm";
				else if(cond == 2)
					htmltext = "magister_gauen_q0356_10.htm";
				else if(cond == 3)
					htmltext = "magister_gauen_q0356_12.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case SPORE_ZOMBIE:
				if(st.rollAndGive(CARNIVORE_SPORE, 1, 1, 100, 100.))
				{
					if(st.getQuestItemsCount(HERBIBOROUS_SPORE) >= 100)
						st.setCond(3);
					else if(cond == 1)
						st.setCond(2);
				}
				break;
			case ROTTING_TREE:
				if(st.rollAndGive(HERBIBOROUS_SPORE, 1, 1, 100, 100.))
				{
					if(st.getQuestItemsCount(CARNIVORE_SPORE) >= 100)
						st.setCond(3);
					else if(cond == 1)
						st.setCond(2);
				}
				break;
		}
		return null;
	}
}