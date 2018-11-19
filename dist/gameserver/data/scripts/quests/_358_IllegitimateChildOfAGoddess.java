package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public class _358_IllegitimateChildOfAGoddess extends Quest
{
	//Variables
	private static final int CHANCE = 70; //in %

	//Quest items
	private static final int SN_SCALE = 5868;

	//Rewards
	private static final int BlackNecl70 = 4975; //16%
	private static final int BlackEarr70 = 4973; //17%
	private static final int BlackRing70 = 4974; //17%

	private static final int AdamantinNecl70 = 4939; //8%
	private static final int AdamantinEarr70 = 4937; //9%
	private static final int AdamantinRing70 = 4938; //9%

	private static final int AvadonShield = 4936; //8%
	private static final int Doomshield = 4980; //16%

	//NPCs
	private static final int RUVARD = 30942;

	//Mobs
	private static final int MOB1 = 20672;
	private static final int MOB2 = 20673;

	public _358_IllegitimateChildOfAGoddess()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(RUVARD);
		addKillId(MOB1);
		addKillId(MOB2);
        addLevelCheck("grandmaster_oltlin_q0358_01.htm", 63/*, 67*/);
		addQuestItem(SN_SCALE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("grandmaster_oltlin_q0358_05.htm"))
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
			case RUVARD:
				if(cond == 0)
					htmltext = "grandmaster_oltlin_q0358_02.htm";
				else if(cond == 1)
					htmltext = "grandmaster_oltlin_q0358_06.htm";
				else if(cond == 2)
				{
					htmltext = "grandmaster_oltlin_q0358_07.htm";
					st.takeItems(SN_SCALE, -1);
					for(int i = 0; i < (int) st.getRateQuestsReward(); i++)
					{
						int item;
						int chance = Rnd.get(100);
						if(chance <= 16)
							item = BlackNecl70;
						else if(chance <= 33)
							item = BlackEarr70;
						else if(chance <= 50)
							item = BlackRing70;
						else if(chance <= 58)
							item = AdamantinNecl70;
						else if(chance <= 67)
							item = AdamantinEarr70;
						else if(chance <= 76)
							item = AdamantinRing70;
						else if(chance <= 84)
							item = AvadonShield;
						else
							item = Doomshield;
						st.giveItems(item, 1, false);
					}
					st.finishQuest();
				}
				break;
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
		{
			st.rollAndGive(SN_SCALE, 1, 1, 520, CHANCE);
			if(st.getQuestItemsCount(SN_SCALE) >= 520)
				st.setCond(2);
		}
		return null;
	}
}