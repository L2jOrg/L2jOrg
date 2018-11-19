package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _259_RanchersPlea extends Quest
{

	private static final int GIANT_SPIDER_SKIN_ID = 1495;
	private static final int HEALING_POTION_ID = 1061;
	private static final int WOODEN_ARROW_ID = 17;
	private static final int SSNG_ID = 1835;
	private static final int SPSSNG_ID = 2509;

	public _259_RanchersPlea()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30497);

		addTalkId(30405);

		addKillId(new int[]{
				20103,
				20106,
				20108
		});

		addQuestItem(GIANT_SPIDER_SKIN_ID);

		addLevelCheck("edmond_q0259_01.htm", 15/*, 21*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			st.set("id", "0");
			st.setCond(1);
			htmltext = "edmond_q0259_03.htm";
		}
		else if(event.equals("30497_1"))
		{
			htmltext = "edmond_q0259_06.htm";
			st.finishQuest();
		}
		else if(event.equals("30497_2"))
			htmltext = "edmond_q0259_07.htm";
		else if(event.equals("30405_1"))
			htmltext = "marius_q0259_03.htm";
		else if(event.equals("30405_2"))
		{
			htmltext = "marius_q0259_04.htm";
			st.giveItems(HEALING_POTION_ID, 1);
			st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
		}
		else if(event.equals("30405_3"))
		{
			htmltext = "marius_q0259_05.htm";
			st.giveItems(WOODEN_ARROW_ID, 80);
			st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
		}
		else if(event.equals("30405_8"))
		{
			htmltext = "marius_q0259_05a.htm";
			st.giveItems(SSNG_ID, 10);
			st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
		}
		else if(event.equals("30405_8a"))
			htmltext = "marius_q0259_05a.htm";
		else if(event.equals("30405_9"))
		{
			htmltext = "marius_q0259_05c.htm";
			st.giveItems(SPSSNG_ID, 5);
			st.takeItems(GIANT_SPIDER_SKIN_ID, 10);
		}
		else if(event.equals("30405_9a"))
			htmltext = "marius_q0259_05d.htm";
		else if(event.equals("30405_4"))
			if(st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) >= 10)
				htmltext = "marius_q0259_06.htm";
			else if(st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) < 10)
				htmltext = "marius_q0259_07.htm";
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
			case 30497:
				if (cond == 0)
					htmltext = "edmond_q0259_02.htm";
				else if (cond == 1 && !st.haveQuestItem(GIANT_SPIDER_SKIN_ID))
					htmltext = "edmond_q0259_04.htm";
				else if (cond == 1)
				{
					htmltext = "edmond_q0259_05.htm";

					long skinsCount = st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID);
					long reward = skinsCount * 5 + (100 * (skinsCount / 10));
					st.giveItems(ADENA_ID, reward, 1000);
					st.takeItems(GIANT_SPIDER_SKIN_ID, -1);
				}
				break;

			case 30405:
				if (cond == 1 && st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) < 10)
					htmltext = "marius_q0259_01.htm";
				else if (cond == 1 && st.getQuestItemsCount(GIANT_SPIDER_SKIN_ID) >= 10)
					htmltext = "marius_q0259_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() > 0)
			st.rollAndGive(GIANT_SPIDER_SKIN_ID, 1, 100);
		return null;
	}
}