package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _296_SilkOfTarantula extends Quest
{

	private static final int TARANTULA_SPIDER_SILK = 1493;
	private static final int TARANTULA_SPINNERETTE = 1494;
	private static final int RING_OF_RACCOON = 1508;
	private static final int RING_OF_FIREFLY = 1509;

	public _296_SilkOfTarantula()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30519);
		addTalkId(30548);

		addKillId(20403);
		addKillId(20508);

		addQuestItem(TARANTULA_SPIDER_SILK);
		addQuestItem(TARANTULA_SPINNERETTE);

		addLevelCheck("trader_mion_q0296_01.htm", 15/*, 21*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("trader_mion_q0296_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("quit"))
		{
			htmltext = "trader_mion_q0296_06.htm";
			st.takeItems(TARANTULA_SPINNERETTE, -1);
			st.finishQuest();
		}
		else if(event.equalsIgnoreCase("exchange"))
			if(st.getQuestItemsCount(TARANTULA_SPINNERETTE) >= 1)
			{
				htmltext = "defender_nathan_q0296_03.htm";
				st.giveItems(TARANTULA_SPIDER_SILK, Rnd.get(15, 20));
				st.takeItems(TARANTULA_SPINNERETTE, -1);
			}
			else
				htmltext = "defender_nathan_q0296_02.htm";
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId) {
			case 30519:
				if (cond == 0)
				{
					if (st.haveQuestItem(RING_OF_RACCOON) || st.haveQuestItem(RING_OF_FIREFLY))
						htmltext = "trader_mion_q0296_02.htm";
					else
						htmltext = "trader_mion_q0296_08.htm";
				}
				else if (cond == 1)
				{
					if (!st.haveQuestItem(TARANTULA_SPIDER_SILK))
						htmltext = "trader_mion_q0296_04.htm";
					else if (st.haveQuestItem(TARANTULA_SPIDER_SILK))
					{
						long reward = st.getQuestItemsCount(TARANTULA_SPIDER_SILK) * 5;
						htmltext = "trader_mion_q0296_05.htm";
						st.giveItems(ADENA_ID,  reward, 1000);
						st.takeItems(TARANTULA_SPIDER_SILK, -1);
					}
				}
				break;

			case 30548:
				if (cond == 1)
					htmltext = "defender_nathan_q0296_01.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			if (Rnd.chance(10))
				st.rollAndGive(TARANTULA_SPINNERETTE, 1, 45);
			else
				st.rollAndGive(TARANTULA_SPIDER_SILK, 1, 45);
		}
		return null;
	}
}