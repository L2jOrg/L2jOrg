package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _316_DestroyPlaguebringers extends Quest
{
	//NPCs
	private static final int Ellenia = 30155;
	//Mobs
	private static int Sukar_Wererat = 20040;
	private static int Sukar_Wererat_Leader = 20047;
	private static int Varool_Foulclaw = 27020;
	//Quest Items
	private static int Wererats_Fang = 1042;
	private static int Varool_Foulclaws_Fang = 1043;
	//Chances
	private static int Wererats_Fang_Chance = 50;
	private static int Varool_Foulclaws_Fang_Chance = 10;

	public _316_DestroyPlaguebringers()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Ellenia);
		addKillId(Sukar_Wererat);
		addKillId(Sukar_Wererat_Leader);
		addKillId(Varool_Foulclaw);
		addQuestItem(Wererats_Fang);
		addQuestItem(Varool_Foulclaws_Fang);

		addLevelCheck("elliasin_q0316_02.htm", 18/*, 24*/);
		addRaceCheck("elliasin_q0316_00.htm", Race.ELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("elliasin_q0316_04.htm") && st.getPlayer().getRace() == Race.ELF && st.getPlayer().getLevel() >= 18)
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("elliasin_q0316_08.htm"))
		{
			st.finishQuest();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case Ellenia:
				if (cond == 0)
					htmltext = "elliasin_q0316_03.htm";
				else if (cond == 1)
				{
					long Reward = (st.getQuestItemsCount(Wererats_Fang) * 5) + (st.getQuestItemsCount(Varool_Foulclaws_Fang) * 1000L);
					if (Reward > 0)
					{
						htmltext = "elliasin_q0316_07.htm";
						st.takeItems(Wererats_Fang, -1);
						st.takeItems(Varool_Foulclaws_Fang, -1);
						st.giveItems(ADENA_ID, Reward);
						st.playSound(SOUND_MIDDLE);
					}
					else
						htmltext = "elliasin_q0316_05.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() == 1)
		{
			if (npc.getNpcId() == Varool_Foulclaw && qs.getQuestItemsCount(Varool_Foulclaws_Fang) == 0)
				qs.rollAndGive(Varool_Foulclaws_Fang, 1, Varool_Foulclaws_Fang_Chance);
			else
				qs.rollAndGive(Wererats_Fang, 1, Wererats_Fang_Chance);
		}
		return null;
	}
}