package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _355_FamilyHonor extends Quest
{
	//NPC
	private static final int GALIBREDO = 30181;
	private static final int PATRIN = 30929;

	//CHANCES
	private static final int CHANCE_FOR_GALFREDOS_BUST = 80;
	private static final int CHANCE_FOR_GODDESS_BUST = 30;

	//ITEMS
	private static final int GALFREDOS_BUST = 4252;
	private static final int BUST_OF_ANCIENT_GODDESS = 4349;
	private static final int WORK_OF_BERONA = 4350;
	private static final int STATUE_PROTOTYPE = 4351;
	private static final int STATUE_ORIGINAL = 4352;
	private static final int STATUE_REPLICA = 4353;
	private static final int STATUE_FORGERY = 4354;

	public _355_FamilyHonor()
	{
		super(PARTY_ONE, REPEATABLE);

		addStartNpc(GALIBREDO);
		addTalkId(PATRIN);

		for(int mob = 20767; mob <= 20770; mob++)
			addKillId(mob);

		addLevelCheck("galicbredo_q0355_01.htm", 36/*, 49*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("galicbredo_q0355_04.htm"))
			st.setCond(1);
		else if(event.equals("galicbredo_q0355_07.htm"))
		{
			long count = st.getQuestItemsCount(BUST_OF_ANCIENT_GODDESS);
			st.takeItems(BUST_OF_ANCIENT_GODDESS, count);
			st.giveItems(WORK_OF_BERONA, count);
		}
		else if(event.equals("appraise"))
		{
			int appraising = Rnd.get(100);
			if(appraising < 20)
			{
				htmltext = "patrin_q0355_07.htm";
				st.takeItems(WORK_OF_BERONA, 1);
			}
			else if(appraising < 40)
			{
				htmltext = "patrin_q0355_05.htm";
				st.takeItems(WORK_OF_BERONA, 1);
				st.giveItems(STATUE_REPLICA, 1);
			}
			else if(appraising < 60)
			{
				htmltext = "patrin_q0355_04.htm";
				st.takeItems(WORK_OF_BERONA, 1);
				st.giveItems(STATUE_ORIGINAL, 1);
			}
			else if(appraising < 80)
			{
				htmltext = "galicbredo_q0355_10.htm";
				st.takeItems(WORK_OF_BERONA, 1);
				st.giveItems(STATUE_FORGERY, 1);
			}
			else if(appraising < 100)
			{
				htmltext = "galicbredo_q0355_11.htm";
				st.takeItems(WORK_OF_BERONA, 1);
				st.giveItems(STATUE_PROTOTYPE, 1);
			}
		}
		else if(event.equals("galicbredo_q0355_09.htm"))
		{
			st.playSound(SOUND_FINISH);
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
			case GALIBREDO:
				if(cond == 0)
					htmltext = "galicbredo_q0355_02.htm";
				else if(cond == 1)
				{
					long count = st.getQuestItemsCount(GALFREDOS_BUST);
					if(count > 0)
					{
						st.takeItems(GALFREDOS_BUST, -1);
						st.giveItems(ADENA_ID, count * 20);
						htmltext = "galicbredo_q0355_07a.htm";
					}
					else
						htmltext = "galicbredo_q0355_08.htm";
				}
				break;
			case PATRIN:
				if(st.getQuestItemsCount(WORK_OF_BERONA) > 0)
					htmltext = "patrin_q0355_01.htm";
				else
				{
					// TODO
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
		if(npcId >= 20767 && npcId <= 20770)
		{
			if(cond == 1)
			{
				st.rollAndGive(GALFREDOS_BUST, 1, 1, CHANCE_FOR_GALFREDOS_BUST);
				st.rollAndGive(BUST_OF_ANCIENT_GODDESS, 1, 1, CHANCE_FOR_GODDESS_BUST);
			}
		}
		return null;
	}
}