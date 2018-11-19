package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Рейты применены путем увеличения шанса/количества квестовго дропа
 */
public final class _331_ArrowForVengeance extends Quest
{

	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRMS_TOOTH = 1454;

	public _331_ArrowForVengeance()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(30125);

		addKillId(new int[]{
				20145,
				20158,
				20176
		});

		addQuestItem(new int[]{
				HARPY_FEATHER,
				MEDUSA_VENOM,
				WYRMS_TOOTH
		});

		addLevelCheck("beltkem_q0331_01.htm", 32/*, 39*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("beltkem_q0331_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("beltkem_q0331_06.htm"))
		{
			st.finishQuest();
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		switch(npcId)
		{
			case 30125:
				if (cond == 0)
					htmltext = "beltkem_q0331_02.htm";
				else if (cond == 1)
					if (st.getQuestItemsCount(HARPY_FEATHER) + st.getQuestItemsCount(MEDUSA_VENOM) + st.getQuestItemsCount(WYRMS_TOOTH) > 0)
					{
						long reward = 6 * st.getQuestItemsCount(HARPY_FEATHER) + 7 * st.getQuestItemsCount(MEDUSA_VENOM) + 9 * st.getQuestItemsCount(WYRMS_TOOTH);
						st.giveItems(ADENA_ID, reward, 1000);
						st.takeItems(HARPY_FEATHER, -1);
						st.takeItems(MEDUSA_VENOM, -1);
						st.takeItems(WYRMS_TOOTH, -1);
						htmltext = "beltkem_q0331_05.htm";
					}
					else
						htmltext = "beltkem_q0331_04.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() > 0)
			switch(npc.getNpcId())
			{
				case 20145:
					st.rollAndGive(HARPY_FEATHER, 1, 33);
					break;
				case 20158:
					st.rollAndGive(MEDUSA_VENOM, 1, 33);
					break;
				case 20176:
					st.rollAndGive(WYRMS_TOOTH, 1, 33);
					break;
			}
		return null;
	}
}