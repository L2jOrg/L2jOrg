package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _360_PlunderTheirSupplies extends Quest
{
	//NPC
	private static final int RUVARD = 30942;
	private static final int CHANCE = 50;

	//MOBS
	private static final int TAIK_SEEKER = 20666;
	private static final int TAIK_LEADER = 20669;

	//QUEST ITEMS
	private static final int SUPPLY_ITEM = 5872;

	public _360_PlunderTheirSupplies()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(RUVARD);
		addKillId(TAIK_SEEKER);
		addKillId(TAIK_LEADER);
        addLevelCheck("guard_coleman_q0360_01.htm", 52/*, 59*/);
		addQuestItem(SUPPLY_ITEM);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("guard_coleman_q0360_04.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = NO_QUEST_DIALOG;
		switch (npcId)
		{
			case RUVARD:
				if (cond == 0)
					htmltext = "guard_coleman_q0360_02.htm";
				else if (cond == 1)
					htmltext = "guard_coleman_q0360_07.htm";
				else if (cond == 2)
				{
					st.takeItems(SUPPLY_ITEM, -1);
					st.giveItems(57, 14000);
					htmltext = "guard_coleman_q0360_09.htm";
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
			st.rollAndGive(SUPPLY_ITEM, 1, 1, 500, CHANCE);
			if(st.getQuestItemsCount(SUPPLY_ITEM) >= 500)
				st.setCond(2);
		}
		return null;
	}
}