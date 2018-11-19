package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _262_TradewiththeIvoryTower extends Quest
{
	//NPC
	public final int VOLODOS = 30137;

	//MOB
	public final int GREEN_FUNGUS = 20007;
	public final int BLOOD_FUNGUS = 20400;

	public final int FUNGUS_SAC = 707;

	public _262_TradewiththeIvoryTower()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(VOLODOS);
		addKillId(new int[]{
				BLOOD_FUNGUS,
				GREEN_FUNGUS
		});
		addQuestItem(new int[]{FUNGUS_SAC});

		addLevelCheck("vollodos_q0262_01.htm", 8/*, 16*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("vollodos_q0262_03.htm"))
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
			case VOLODOS:
				if (cond == 0)
					htmltext = "vollodos_q0262_02.htm";
				else if (cond == 1)
					htmltext = "vollodos_q0262_04.htm";
				else if (cond == 2)
				{
					st.giveItems(ADENA_ID, 300);
					st.takeItems(FUNGUS_SAC, -1);
					htmltext = "vollodos_q0262_05.htm";
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int random = Rnd.get(10);
		if(st.getCond() == 1)
		{
			if (npcId == GREEN_FUNGUS && random < 3 || npcId == BLOOD_FUNGUS && random < 4)
			{
				st.giveItems(FUNGUS_SAC, 1, true);
				if (st.getQuestItemsCount(FUNGUS_SAC) >= 10)
					st.setCond(2);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}