package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _266_PleaOfPixies extends Quest
{

	private static final int PREDATORS_FANG = 1334;
	private static final int EMERALD = 1337;
	private static final int BLUE_ONYX = 1338;
	private static final int ONYX = 1339;
	private static final int GLASS_SHARD = 1336;

	public _266_PleaOfPixies()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(31852);
		addKillId(new int[]{
				20525,
				20530,
				20534,
				20537
		});
		addQuestItem(PREDATORS_FANG);

		addLevelCheck("pixy_murika_q0266_01.htm", 3/*, 8*/);
		addRaceCheck("pixy_murika_q0266_00.htm", Race.ELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("pixy_murika_q0266_03.htm"))
		{
			st.setCond(1);
		}
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
			case 31852:
				if (cond == 0)
					htmltext = "pixy_murika_q0266_02.htm";
				if (cond == 1)
					htmltext = "pixy_murika_q0266_04.htm";
				if (cond == 2)
				{
					st.takeItems(PREDATORS_FANG, -1);
					if (Rnd.chance(1))
					{
						st.giveItems(EMERALD, 1);
						st.playSound(SOUND_JACKPOT);
					}
					else if (Rnd.chance(10))
						st.giveItems(BLUE_ONYX, 1);
					else if (Rnd.chance(30))
						st.giveItems(ONYX, 1);
					else
						st.giveItems(GLASS_SHARD, 1);
					htmltext = "pixy_murika_q0266_05.htm";
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
			st.rollAndGive(PREDATORS_FANG, 1, 1, 100, 90);
			if (st.getQuestItemsCount(PREDATORS_FANG) >= 100)
				st.setCond(2);
		}
		return null;
	}
}