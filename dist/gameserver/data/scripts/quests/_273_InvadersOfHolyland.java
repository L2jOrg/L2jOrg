package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _273_InvadersOfHolyland extends Quest
{
	public final int BLACK_SOULSTONE = 1475;
	public final int RED_SOULSTONE = 1476;

	public _273_InvadersOfHolyland()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(30566);
		addKillId(new int[]{
				20311,
				20312,
				20313
		});
		addQuestItem(new int[]{
				BLACK_SOULSTONE,
				RED_SOULSTONE
		});

		addLevelCheck("atuba_chief_varkees_q0273_01.htm", 6/*, 14*/);
		addRaceCheck("atuba_chief_varkees_q0273_00.htm", Race.ORC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("atuba_chief_varkees_q0273_03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equals("atuba_chief_varkees_q0273_07.htm"))
		{
			st.finishQuest();
		}
		else if(event.equals("atuba_chief_varkees_q0273_08.htm"))
		{
			st.setCond(1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

	    switch (npcId)
		{
			case 30566:
				if (cond == 0)
					htmltext = "atuba_chief_varkees_q0273_02.htm";
				else if (cond > 0)
					if (st.getQuestItemsCount(BLACK_SOULSTONE) == 0 && st.getQuestItemsCount(RED_SOULSTONE) == 0)
						htmltext = "atuba_chief_varkees_q0273_04.htm";
					else
					{
						long adena = 0;
						if (st.getQuestItemsCount(BLACK_SOULSTONE) > 0)
						{
							htmltext = "atuba_chief_varkees_q0273_05.htm";
							adena += st.getQuestItemsCount(BLACK_SOULSTONE) * 3;
						}
						if (st.getQuestItemsCount(RED_SOULSTONE) > 0)
						{
							htmltext = "atuba_chief_varkees_q0273_06.htm";
							adena += st.getQuestItemsCount(RED_SOULSTONE) * 5;
						}
						st.takeAllItems(BLACK_SOULSTONE, RED_SOULSTONE);
						st.giveItems(ADENA_ID, adena);
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
		int cond = st.getCond();
		if(npcId == 20311)
		{
			if(cond == 1)
			{
				if(Rnd.chance(90))
					st.giveItems(BLACK_SOULSTONE, 1, true);
				else
					st.giveItems(RED_SOULSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if(npcId == 20312)
		{
			if(cond == 1)
			{
				if(Rnd.chance(87))
					st.giveItems(BLACK_SOULSTONE, 1, true);
				else
					st.giveItems(RED_SOULSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if(npcId == 20313)
			if(cond == 1)
			{
				if(Rnd.chance(77))
					st.giveItems(BLACK_SOULSTONE, 1, true);
				else
					st.giveItems(RED_SOULSTONE, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		return null;
	}

}