package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест проверен и работает. Рейты применены путем увеличения награды за квестовые предметы.
 */
public final class _265_ChainsOfSlavery extends Quest
{
	// NPC
	private static final int KRISTIN = 30357;

	// MOBS
	private static final int IMP = 20004;
	private static final int IMP_ELDER = 20005;

	// ITEMS
	private static final int IMP_SHACKLES = 1368;

	public _265_ChainsOfSlavery()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(KRISTIN);

		addKillId(IMP);
		addKillId(IMP_ELDER);

		addQuestItem(IMP_SHACKLES);

		addLevelCheck("sentry_krpion_q0265_01.htm", 6/*, 11*/);
		addRaceCheck("sentry_krpion_q0265_00.htm", Race.DARKELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sentry_krpion_q0265_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("sentry_krpion_q0265_06.htm"))
			st.finishQuest();
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case KRISTIN:
				if (cond == 0)
					htmltext = "sentry_krpion_q0265_02.htm";
				else if (cond == 1)
				{
					if (st.haveQuestItem(IMP_SHACKLES))
					{
						long reward = 5 * st.getQuestItemsCount(IMP_SHACKLES);
						st.giveItems(ADENA_ID, reward, 1000);
						st.takeItems(IMP_SHACKLES, -1);
						htmltext = "sentry_krpion_q0265_05.htm";
					}
					else
						htmltext = "sentry_krpion_q0265_04.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getCond() == 1 && Rnd.chance(10 + npcId - 20004))
		{
			st.giveItems(IMP_SHACKLES, 1, true);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}