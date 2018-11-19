package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _329_CuriosityOfDwarf extends Quest
{
	private int GOLEM_HEARTSTONE = 1346;
	private int BROKEN_HEARTSTONE = 1365;

	public _329_CuriosityOfDwarf()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30437);
		addKillId(20083);
		addKillId(20085);

		addQuestItem(BROKEN_HEARTSTONE);
		addQuestItem(GOLEM_HEARTSTONE);

		addLevelCheck("trader_rolento_q0329_01.htm", 33/*, 38*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("trader_rolento_q0329_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("trader_rolento_q0329_06.htm"))
		{
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		switch (npcId) {
			case 30437:
				if (cond == 0)
					htmltext = "trader_rolento_q0329_02.htm";
				else if (cond > 0)
				{
					if (st.haveQuestItem(GOLEM_HEARTSTONE) || st.haveQuestItem(BROKEN_HEARTSTONE))
					{
						long golemHeartstones = st.getQuestItemsCount(GOLEM_HEARTSTONE);
						long brokenHeartstones = st.getQuestItemsCount(BROKEN_HEARTSTONE);
						if((golemHeartstones + brokenHeartstones) >= 700)
							st.giveItems(ADENA_ID, 700, 700);
						else
						{
							long reward = (40 * golemHeartstones) + (5 * brokenHeartstones);
							st.giveItems(ADENA_ID, reward, 1000);
						}
						st.takeItems(BROKEN_HEARTSTONE, -1);
						st.takeItems(GOLEM_HEARTSTONE, -1);
						htmltext = "trader_rolento_q0329_05.htm";
					}
					else
						htmltext = "trader_rolento_q0329_04.htm";
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int n = Rnd.get(1, 100);

		if(npcId == 20085)
		{
			if(n < 5)
			{
				st.giveItems(GOLEM_HEARTSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
			else if(n < 58)
			{
				st.giveItems(BROKEN_HEARTSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if(npcId == 20083)
			if(n < 6)
			{
				st.giveItems(GOLEM_HEARTSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
			else if(n < 56)
			{
				st.giveItems(BROKEN_HEARTSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}