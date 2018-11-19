package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _258_BringWolfPelts extends Quest
{
	int WOLF_PELT = 702;

	int LEATHER_HELMET = 42;
	int LEATHER_SHIELD = 18;
	int COTTON_HAD = 41;
	int PANTS_LESSER = 462;

	public _258_BringWolfPelts()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30001);
		addKillId(20120);
		addKillId(20442);

		addQuestItem(WOLF_PELT);

		addLevelCheck("lector_q0258_01.htm", 3/*, 9*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.intern().equalsIgnoreCase("lector_q0258_03.htm"))
		{
			st.setCond(1);
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
			case 30001:
				if (cond == 0)
					htmltext = "lector_q0258_02.htm";
				else if (cond == 1)
					htmltext = "lector_q0258_05.htm";
				else if (cond == 2) {
					st.takeItems(WOLF_PELT, 40);
					if (Rnd.chance(10))
					{
						st.giveItems(PANTS_LESSER, 1);
						st.playSound(SOUND_JACKPOT);
					}
					else if (Rnd.chance(25))
						st.giveItems(LEATHER_HELMET, 1);
					else if (Rnd.chance(25))
						st.giveItems(COTTON_HAD, 1);
					else
						st.giveItems(LEATHER_SHIELD, 1);

					htmltext = "lector_q0258_06.htm";
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
			st.rollAndGive(WOLF_PELT, 1, 1, 40, 100);
			if (st.getQuestItemsCount(WOLF_PELT) >= 40)
				st.setCond(2);
		}
		return null;
	}
}