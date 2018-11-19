package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _300_HuntingLetoLizardman extends Quest
{
	//NPC's
	private static final int RATH = 30126;

	//Item's
	private static final int BRACELET_OF_LIZARDMAN = 7139;
	private static final int ANIMAL_BONE = 1872;
	private static final int ANIMAL_SKIN = 1867;

	//Chance
	private static final int BRACELET_OF_LIZARDMAN_CHANCE = 70;

	public _300_HuntingLetoLizardman()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(RATH);

		for(int lizardman_id = 20577; lizardman_id <= 20582; lizardman_id++)
			addKillId(lizardman_id);

		addQuestItem(BRACELET_OF_LIZARDMAN);
		addLevelCheck("rarshints_q0300_0103.htm", 34/*, 39*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("rarshints_q0300_0104.htm"))
			st.setCond(1);
		else if(event.equalsIgnoreCase("rarshints_q0300_0201.htm"))
		{
			if(st.getQuestItemsCount(BRACELET_OF_LIZARDMAN) < 500)
			{
				htmltext = "rarshints_q0300_0202.htm";
				st.setCond(1);
			}
			else
			{
				st.takeItems(BRACELET_OF_LIZARDMAN, -1);
				switch(Rnd.get(3))
				{
					case 0:
						st.giveItems(ADENA_ID, 5000);
						break;
					case 1:
						st.giveItems(ANIMAL_BONE, 50);
						break;
					case 2:
						st.giveItems(ANIMAL_SKIN, 50);
						break;
				}
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
			case RATH:
				if(cond == 0)
					htmltext = "rarshints_q0300_0101.htm";
				else if(cond == 1)
					htmltext = "rarshints_q0300_0106.htm";
				else if(cond == 2)
					htmltext = "rarshints_q0300_0105.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId >= 20577 && npcId <= 20582)
		{
			if(cond == 1)
			{
				if(st.rollAndGive(BRACELET_OF_LIZARDMAN, 1, 1, 500, BRACELET_OF_LIZARDMAN_CHANCE))
					st.setCond(2);
			}
		}
		return null;
	}
}