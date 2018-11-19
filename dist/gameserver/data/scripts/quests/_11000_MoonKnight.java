package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By Evil_dnk

public class _11000_MoonKnight extends Quest
{
	// NPC's
	private static final int JONSON = 30939;
	private static final int DAMION = 30208;
	private static final int NETI = 30425;
	private static final int ROLENTO = 30437;
	private static final int AMORA = 30940;
	private static final int GUDZ = 30941;

	// Monster's
	private static final int MONSTERSCOND1 = 27201;
	private static final int MONSTERSCOND2 = 27202;
	private static final int MONSTERSCOND3 = 27203;

	// Item's
	private static final int DROPITEMC2 = 49555;
	private static final int DROPITEMC5_1 = 49557;
	private static final int DROPITEMC5_2 = 49558;
	private static final int DROPITEMC9 = 49561;

	private static final int AMORACERT = 49556;

	private static final double CHANCE1 = 50;
	private static final double CHANCE2 = 30;

	public _11000_MoonKnight()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(JONSON);
		addTalkId(DAMION, NETI, ROLENTO, AMORA, GUDZ);
		addKillId(MONSTERSCOND1, MONSTERSCOND2, MONSTERSCOND3);
		addQuestItem(DROPITEMC2, DROPITEMC5_1, DROPITEMC5_2, DROPITEMC9, AMORACERT);
		addLevelCheck("30939-02.htm", 25/*, 40*/);

	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30939-03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30939-05.htm"))
		{
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("30939-07.htm"))
		{
			st.setCond(6);
		}
		else if(event.equalsIgnoreCase("30437-03.htm"))
		{
			st.setCond(8);
		}
		else if(event.equalsIgnoreCase("30941-04.htm"))
		{
			st.setCond(9);
		}
		else if(event.equalsIgnoreCase("light"))
		{
			st.giveItems(7850, 1, false);
			st.giveItems(7854, 1, false);
			st.giveItems(7855, 1, false);
			st.giveItems(7856, 1, false);
			st.finishQuest();
			return null;
		}
		else if(event.equalsIgnoreCase("heavy"))
		{
			st.giveItems(7850, 1, false);
			st.giveItems(7851, 1, false);
			st.giveItems(7852, 1, false);
			st.giveItems(7853, 1, false);
			st.finishQuest();
			return null;
		}
		else if(event.equalsIgnoreCase("magic"))
		{
			st.giveItems(7850, 1, false);
			st.giveItems(7857, 1, false);
			st.giveItems(7858, 1, false);
			st.giveItems(7859, 1, false);
			st.finishQuest();
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		String htmltext = NO_QUEST_DIALOG;
		switch(npcId)
		{
			case JONSON:
				if(cond == 0)
					htmltext = "30939-01.htm";
				else if(cond == 1)
					htmltext = "30939-03.htm";
				else if(cond == 4)
					htmltext = "30939-04.htm";
				else if(cond == 5)
				{
					if(st.haveQuestItem(DROPITEMC5_1) && st.haveQuestItem(DROPITEMC5_2))
						htmltext = "30939-06.htm";
					else
						htmltext = "30939-05.htm";
				}
				else if(cond == 6)
					htmltext = "30939-07.htm";
				else if(cond == 10)
					htmltext = "30939-08.htm";
				break;
			case DAMION:
				if(cond == 1)
				{
					st.setCond(2);
					htmltext = "30208-01.htm";
				}
				else if(cond == 2)
					htmltext = "30208-01.htm";
				else if(cond == 3)
				{
					st.takeItems(AMORACERT, -1);
					st.setCond(4);
					htmltext = "30208-03.htm";
				}
				else if(cond == 4)
					htmltext = "30208-03.htm";
				break;
			case AMORA:
				if(cond == 2)
				{
					if(st.haveQuestItem(DROPITEMC2, 10))
					{
						st.takeItems(DROPITEMC2, -1);
						st.giveItems(AMORACERT, 1, false);
						st.setCond(3);
						htmltext = "30940-01.htm";
					}
					else
					{
						//?
					}
				}
				else if(cond == 3)
					htmltext = "30940-01.htm";
				break;
			case NETI:
				if(cond == 6)
				{
					st.setCond(7);
					htmltext = "30425-01.htm";
				}
				else if(cond == 7)
					htmltext = "30425-01.htm";
				break;
			case ROLENTO:
				if(cond == 7)
					htmltext = "30437-01.htm";
				else if(cond == 8)
					htmltext = "30437-03.htm";
				break;
			case GUDZ:
				if(cond == 8)
					htmltext = "30941-01.htm";
				else if(cond == 9)
				{
					if(st.haveQuestItem(DROPITEMC9, 10))
					{
						st.setCond(10);
						htmltext = "30941-05.htm";
					}
					else
						htmltext = "30941-04.htm";
				}
				else if(cond == 10)
					htmltext = "30941-05.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 2)
		{
			if(npc.getNpcId() == MONSTERSCOND1)
			{
				if(st.rollAndGive(DROPITEMC2, 1, 1, 10, CHANCE1))
					st.playSound(Quest.SOUND_MIDDLE);
			}
		}
		else if(cond == 5)
		{
			if(npc.getNpcId() == MONSTERSCOND2)
			{
				if(st.rollAndGive(DROPITEMC5_1, 1, 1, 1, CHANCE2))
				{
					if(st.haveQuestItem(DROPITEMC5_2))
					{
						st.playSound(Quest.SOUND_MIDDLE);
						return null;
					}
				}
				if(st.rollAndGive(DROPITEMC5_2, 1, 1, 1, CHANCE2))
				{
					if(st.haveQuestItem(DROPITEMC5_1))
					{
						st.playSound(Quest.SOUND_MIDDLE);
						return null;
					}
				}
			}
		}
		else if(cond == 9)
		{
			if(npc.getNpcId() == MONSTERSCOND3)
			{
				if(st.rollAndGive(DROPITEMC9, 1, 1, 10, 100))
					st.playSound(Quest.SOUND_MIDDLE);
			}
		}
		return null;
	}
}