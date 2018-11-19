package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест Keen Claws
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _264_KeenClaws extends Quest
{
	//NPC
	private static final int Payne = 30136;
	//Quest Items
	private static final int WolfClaw = 1367;
	//Items
	private static final int POTION_ATTACK_SPEED = 735;
	private static final int POTION_HAST = 734;
	private static final int COOTON_BOOTS = 35;

	//MOB
	private static final int Goblin = 20003;
	private static final int AshenWolf = 20456;

	public _264_KeenClaws()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(Payne);

		addKillId(Goblin);
		addKillId(AshenWolf);

		addQuestItem(WolfClaw);

		addLevelCheck("paint_q0264_01.htm", 3/*, 9*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("paint_q0264_03.htm"))
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
			case Payne:
				if (cond == 0)
					htmltext = "paint_q0264_02.htm";
				else if (cond == 1)
					htmltext = "paint_q0264_04.htm";
				else if (cond == 2)
				{
					st.takeItems(WolfClaw, -1);
					if (Rnd.chance(10))
					{
						st.giveItems(COOTON_BOOTS, 1);
						st.playSound(SOUND_JACKPOT);
					}
					else if (Rnd.chance(30))
						st.giveItems(POTION_HAST, 1);
					else
						st.giveItems(POTION_ATTACK_SPEED, 1);
					htmltext = "paint_q0264_05.htm";
					st.finishQuest();
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1) {
			st.rollAndGive(WolfClaw, 1, 1, 50, 50);
			if (st.getQuestItemsCount(WolfClaw) >= 50)
				st.setCond(2);
		}
		return null;
	}
}