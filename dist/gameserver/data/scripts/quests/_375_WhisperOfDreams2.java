package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _375_WhisperOfDreams2 extends Quest
{
	//NPCs
	private static final int VANUTA = 30938;
	private static final int CHANCE = 50;

	//Quest items
	private int K_HORN = 5888;
	private int CH_SKULL = 5889;

	//Mobs & Drop
	private int[] MONSTERS = { 20628, 20629 };

	public _375_WhisperOfDreams2()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(VANUTA);
		addKillId(MONSTERS);
		addLevelCheck("30938-2.htm", 60, 74); // Квест имеет четкую границу уровня (оффлайк).
		addItemHaveCheck("30938-2.htm", 5887, 1);
		addQuestItem(CH_SKULL);
		addQuestItem(K_HORN);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30938-6.htm"))
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
			case VANUTA:
				if (cond == 0)
					htmltext = "30938-1.htm";
				else if (cond == 1)
					htmltext = "30938-8.htm";
				else if (cond == 2)
				{
					st.takeItems(CH_SKULL, -1);
					st.takeItems(K_HORN, -1);
					if(Rnd.chance(1))
						st.giveItems(947, 1);
					else if(Rnd.get(1, 1000) == 1)
						st.giveItems(33808, 1);
					else if(Rnd.chance(21))
						st.giveItems(49476, 1);
					else
						st.giveItems(49474, 1);
					st.giveItems(57, 9000);
					htmltext = "30938-4.htm";
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
			if(npc.getNpcId() == 20628)
				st.rollAndGive(CH_SKULL, 1, 1, 325, CHANCE);
			if(npc.getNpcId() == 20629)
				st.rollAndGive(K_HORN, 1, 1, 325, CHANCE);

			if (st.getQuestItemsCount(CH_SKULL) >= 325 && st.getQuestItemsCount(K_HORN) >= 325)
				st.setCond(2);
		}
		return null;
	}
}
