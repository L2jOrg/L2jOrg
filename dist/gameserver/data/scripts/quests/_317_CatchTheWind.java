package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест Catch The Wind
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _317_CatchTheWind extends Quest
{
	//NPCs
	private static final int Rizraell = 30361;
	//Quest Items
	private static int WindShard = 1078;
	//Mobs
	private static int Lirein = 20036;
	private static int LireinElder = 20044;

	public _317_CatchTheWind()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Rizraell);
		//Mob Drop
		addKillId(Lirein);
		addKillId(LireinElder);
		addQuestItem(WindShard);

		addLevelCheck("rizraell_q0317_02.htm", 18/*, 23*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("rizraell_q0317_04.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("rizraell_q0317_08.htm"))
		{
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Rizraell:
				if (cond == 0)
					htmltext = "rizraell_q0317_03.htm";
				else if (cond == 1)
				{
					long count = st.getQuestItemsCount(WindShard) * 10;
					if (count > 0)
					{
						st.takeItems(WindShard, -1);
						st.giveItems(ADENA_ID, count, 1000);
						htmltext = "rizraell_q0317_07.htm";
					}
					else
						htmltext = "rizraell_q0317_05.htm";
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
			st.rollAndGive(WindShard, 1, 60);
		}
		return null;
	}
}