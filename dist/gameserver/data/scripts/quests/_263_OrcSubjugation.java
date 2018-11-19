package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _263_OrcSubjugation extends Quest
{
	// NPC
	public final int KAYLEEN = 30346;

	// MOBS
	public final int BALOR_ORC_ARCHER = 20385;
	public final int BALOR_ORC_FIGHTER = 20386;
	public final int BALOR_ORC_FIGHTER_LEADER = 20387;
	public final int BALOR_ORC_LIEUTENANT = 20388;

	public final int ORC_AMULET = 1116;
	public final int ORC_NECKLACE = 1117;

	public _263_OrcSubjugation()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(KAYLEEN);
		addKillId(new int[]{
				BALOR_ORC_ARCHER,
				BALOR_ORC_FIGHTER,
				BALOR_ORC_FIGHTER_LEADER,
				BALOR_ORC_LIEUTENANT
		});
		addQuestItem(new int[]{
				ORC_AMULET,
				ORC_NECKLACE
		});

		addLevelCheck("sentry_kayleen_q0263_01.htm", 8/*, 16*/);
		addRaceCheck("sentry_kayleen_q0263_00.htm", Race.DARKELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("sentry_kayleen_q0263_03.htm"))
			st.setCond(1);
		else if(event.equals("sentry_kayleen_q0263_06.htm"))
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
			case KAYLEEN:
				if (cond == 0)
					htmltext = "sentry_kayleen_q0263_02.htm";
				else if (cond == 1)
				{
					if (st.getQuestItemsCount(ORC_AMULET) == 0 && st.getQuestItemsCount(ORC_NECKLACE) == 0)
						htmltext = "sentry_kayleen_q0263_04.htm";
					else
					{
						long reward = st.getQuestItemsCount(ORC_AMULET) * 8 + st.getQuestItemsCount(ORC_NECKLACE) * 10;
						htmltext = "sentry_kayleen_q0263_05.htm";
						st.giveItems(ADENA_ID, reward, 1000);
						st.takeItems(ORC_AMULET, -1);
						st.takeItems(ORC_NECKLACE, -1);
					}
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
			if (Rnd.chance(70))
				st.rollAndGive(ORC_AMULET, 1, 14);
			else
				st.rollAndGive(ORC_NECKLACE, 1, 14);
		}
		return null;
	}
}