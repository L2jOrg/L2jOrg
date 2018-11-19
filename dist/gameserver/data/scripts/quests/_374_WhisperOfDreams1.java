package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _374_WhisperOfDreams1 extends Quest
{

	//Quest items
	private static final int CB_TOOTH = 5884; //Cave Beast Tooth
	private static final int DW_LIGHT = 5885; //Death Wave Light
	private static final int M_STONE_S = 5886; //Mysterius stone
	private static final int M_STONE = 5887; //Mysterius stone

	//NPCs
	private static final int VANUTA = 30938;
	private static final int GALMAN = 31044;

	//Mobs & Drop
	private static final int[] MONSTERS = {20620, 20621};

	private static final int CHANCE = 50;

	public _374_WhisperOfDreams1()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(VANUTA);
		addTalkId(VANUTA);
		addTalkId(GALMAN);
		addKillId(MONSTERS);
		addLevelCheck("30938-2.htm", 56, 66); // Квест имеет четкую границу уровня (оффлайк).
		addQuestItem(CB_TOOTH);
		addQuestItem(DW_LIGHT);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30938-4.htm"))
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
					htmltext = "30938-6.htm";
				else if (cond == 2)
				{
					if(st.haveQuestItem(M_STONE_S))
					{
						st.setCond(3);
						return "30938-6.htm";
					}
					st.takeItems(CB_TOOTH, -1);
					st.takeItems(DW_LIGHT, -1);
					if(Rnd.chance(4))
						st.giveItems(948, 1);
					else if(Rnd.get(1, 1000) <= 5)
						st.giveItems(33814, 1);
					else if(Rnd.chance(26))
						st.giveItems(49478, 1);
					else
						st.giveItems(49475, 1);
					st.giveItems(57, 9000);
					htmltext = "30938-11.htm";
					st.finishQuest();
				}
				else if (cond == 3)
				{
					htmltext = "30938-10.htm";
				}
				else if (cond == 4)
				{
					st.takeItems(CB_TOOTH, -1);
					st.takeItems(DW_LIGHT, -1);
					if(Rnd.chance(4))
						st.giveItems(948, 1);
					else if(Rnd.get(1, 1000) <= 5)
						st.giveItems(33814, 1);
					else if(Rnd.chance(26))
						st.giveItems(49478, 1);
					else
						st.giveItems(49475, 1);
					st.giveItems(57, 9000);
					htmltext = "30938-11.htm";
					st.finishQuest();
				}
				break;

			case GALMAN:
				if(cond == 3 && st.getQuestItemsCount(M_STONE_S) > 0)
				{
					htmltext = "31044-1.htm";
					st.takeItems(M_STONE_S, -1);
					st.giveItems(M_STONE, 1, false);
					st.setCond(4);
				}

				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 || st.getCond() == 2)
		{
			if(Rnd.chance(1) && !st.haveQuestItem(M_STONE_S) && !st.haveQuestItem(M_STONE))
				st.giveItems(M_STONE_S, 1, false);
		}

		if (st.getCond() == 1)
		{
			if(npc.getNpcId() == 20620)
				st.rollAndGive(CB_TOOTH, 1, 1, 360, CHANCE);
			if(npc.getNpcId() == 20621)
				st.rollAndGive(DW_LIGHT, 1, 1, 360, CHANCE);

			if (st.getQuestItemsCount(DW_LIGHT) >= 360 && st.getQuestItemsCount(CB_TOOTH) >= 360)
				st.setCond(2);
		}
		return null;
	}
}