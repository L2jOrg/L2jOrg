package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _276_HestuiTotem extends Quest
{
	//NPCs
	private static final int Tanapi = 30571;
	//Mobs
	private static int Kasha_Bear = 20479;
	private static int Kasha_Bear_Totem_Spirit = 27044;
	//Items
	private static int Leather_Pants = 29;
	private static int Totem_of_Hestui = 1500;
	//Quest Items
	private static int Kasha_Parasite = 1480;
	private static int Kasha_Crystal = 1481;

	public _276_HestuiTotem()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(Tanapi);
		addKillId(Kasha_Bear);
		addKillId(Kasha_Bear_Totem_Spirit);
		addQuestItem(Kasha_Parasite);
		addQuestItem(Kasha_Crystal);

		addLevelCheck("seer_tanapi_q0276_01.htm", 12/*, 18*/);
		addRaceCheck("seer_tanapi_q0276_00.htm", Race.ORC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("seer_tanapi_q0276_03.htm") && st.getPlayer().getRace() == Race.ORC && st.getPlayer().getLevel() >= 15)
		{
			st.setCond(1);
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
			case Tanapi:
				if (cond == 0)
					htmltext = "seer_tanapi_q0276_02.htm";
				if (cond == 1 || cond == 2)
				{
					if (st.getQuestItemsCount(Kasha_Crystal) > 0)
					{
						htmltext = "seer_tanapi_q0276_05.htm";
						st.takeItems(Kasha_Parasite, -1);
						st.takeItems(Kasha_Crystal, -1);
						if (Rnd.chance(5))
							st.giveItems(Totem_of_Hestui, 1);
						else
							st.giveItems(Leather_Pants, 1);
						st.finishQuest();
					}
					else
						htmltext = "seer_tanapi_q0276_04.htm";
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if (qs.getCond() == 1)
		   {
			   if (npcId == Kasha_Bear)
			   {
				   if (qs.getQuestItemsCount(Kasha_Parasite) < 60)
				   {
					   qs.giveItems(Kasha_Parasite, 1, true);
					   qs.playSound(SOUND_ITEMGET);
				   }
				   if (qs.getQuestItemsCount(Kasha_Parasite) >= 60)
				   {
					   qs.takeItems(Kasha_Parasite, -1);
					   qs.addSpawn(Kasha_Bear_Totem_Spirit);
				   }
			   }
			   else if (npcId == Kasha_Bear_Totem_Spirit)
			   {
				   qs.giveItems(Kasha_Crystal, 1);
				   qs.setCond(2);
			   }
		   }
		return null;
	}
}