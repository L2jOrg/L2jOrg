package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест проверен и работает.
 * Рейты прописаны путем повышения шанса получения квестовых вещей.
 */
public final class _257_GuardIsBusy extends Quest
{
	int GLUDIO_LORDS_MARK = 1084;
	int ORC_AMULET = 752;
	int ORC_NECKLACE = 1085;
	int WEREWOLF_FANG = 1086;

	public _257_GuardIsBusy()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30039);
		addKillId(20130, 20131, 20132, 20342, 20343, 20006, 20093, 20096, 20098);
		addQuestItem(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORDS_MARK);

		addLevelCheck("gilbert_q0257_01.htm", 6/*, 16*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gilbert_q0257_03.htm"))
		{
			st.setCond(1);
			st.takeItems(GLUDIO_LORDS_MARK, -1);
			st.giveItems(GLUDIO_LORDS_MARK, 1);
		}
		else if(event.equalsIgnoreCase("257_2"))
		{
			htmltext = "gilbert_q0257_05.htm";
			st.takeItems(GLUDIO_LORDS_MARK, -1);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("257_3"))
			htmltext = "gilbert_q0257_06.htm";
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
			case 30039:
				if (cond == 0)
					htmltext = "gilbert_q0257_02.htm";
				else if (cond == 1 && st.getQuestItemsCount(ORC_AMULET) < 1 && st.getQuestItemsCount(ORC_NECKLACE) < 1 && st.getQuestItemsCount(WEREWOLF_FANG) < 1)
					htmltext = "gilbert_q0257_04.htm";
				else if (cond == 1 && (st.getQuestItemsCount(ORC_AMULET) > 0 || st.getQuestItemsCount(ORC_NECKLACE) > 0 || st.getQuestItemsCount(WEREWOLF_FANG) > 0))
				{
					long reward = 5 * st.getQuestItemsCount(ORC_AMULET) + 8 * st.getQuestItemsCount(ORC_NECKLACE) + 10 * st.getQuestItemsCount(WEREWOLF_FANG);
					st.giveItems(ADENA_ID, reward, 1000);
					st.takeItems(ORC_AMULET, -1);
					st.takeItems(ORC_NECKLACE, -1);
					st.takeItems(WEREWOLF_FANG, -1);
					htmltext = "gilbert_q0257_07.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getQuestItemsCount(GLUDIO_LORDS_MARK) > 0 && st.getCond() > 0)
			if(npcId == 20130 || npcId == 20131 || npcId == 20006)
				st.rollAndGive(ORC_AMULET, 1, 50);
			else if(npcId == 20093 || npcId == 20096 || npcId == 20098)
				st.rollAndGive(ORC_NECKLACE, 1, 50);
			else if(npcId == 20132)
				st.rollAndGive(WEREWOLF_FANG, 1, 33);
			else if(npcId == 20343)
				st.rollAndGive(WEREWOLF_FANG, 1, 50);
			else if(npcId == 20342)
				st.rollAndGive(WEREWOLF_FANG, 1, 75);
		return null;
	}
}