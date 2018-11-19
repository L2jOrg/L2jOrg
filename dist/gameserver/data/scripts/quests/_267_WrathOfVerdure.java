package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _267_WrathOfVerdure extends Quest
{
	//NPCs
	private final static int Treant_Bremec = 31853;
	//Mobs
	private static int Goblin_Raider = 20325;
	//Quest Items
	private static int Goblin_Club = 1335;
	//Chances
	private static int Goblin_Club_Chance = 50;

	public _267_WrathOfVerdure()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Treant_Bremec);
		addKillId(Goblin_Raider);
		addQuestItem(Goblin_Club);

		addLevelCheck("bri_mec_tran_q0267_01.htm", 4/*, 9*/);
		addRaceCheck("bri_mec_tran_q0267_00.htm", Race.ELF);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("bri_mec_tran_q0267_03.htm") && st.getPlayer().getRace() == Race.ELF && st.getPlayer().getLevel() >= 4)
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("bri_mec_tran_q0267_06.htm"))
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
			case Treant_Bremec:
				if (cond == 0)
					htmltext = "bri_mec_tran_q0267_02.htm";
				else if (cond == 1)
				{
					if (st.haveQuestItem(Goblin_Club))
					{
						long reward = st.getQuestItemsCount(Goblin_Club) * 2;
						htmltext = "bri_mec_tran_q0267_05.htm";
						st.takeItems(Goblin_Club, -1);
						st.giveItems(ADENA_ID, reward, 1000);
						st.playSound(SOUND_MIDDLE);
					}
					else
						htmltext = "bri_mec_tran_q0267_04.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getCond() > 0)
		{
			if (Rnd.chance(Goblin_Club_Chance))
			{
				qs.giveItems(Goblin_Club, 1, true);
				qs.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}
