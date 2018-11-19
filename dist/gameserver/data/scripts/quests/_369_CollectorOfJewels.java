package quests;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

public final class _369_CollectorOfJewels extends Quest
{
	// NPCs
	private static final int NELL = 30376;
	// Mobs
	private static int Roxide = 20747;
	private static int Rowin_Undine = 20619;
	private static int Lakin_Undine = 20616;
	private static int Salamander_Rowin = 20612;
	private static int Lakin_Salamander = 20609;
	private static int Death_Fire = 20749;
	// Quest Items
	private static int SOUL_SHARD = 49054;

	private final Map<Integer, int[]> DROPLIST = new HashMap<Integer, int[]>();

	public _369_CollectorOfJewels()
	{
		super(PARTY_NONE, REPEATABLE);
		addStartNpc(NELL);
		addKillId(Roxide);
		addKillId(Rowin_Undine);
		addKillId(Lakin_Undine);
		addKillId(Salamander_Rowin);
		addKillId(Lakin_Salamander);
		addKillId(Death_Fire);
		addQuestItem(SOUL_SHARD);

		DROPLIST.put(Roxide, new int[]{
				SOUL_SHARD,
				85
		});
		DROPLIST.put(Rowin_Undine, new int[]{
				SOUL_SHARD,
				73
		});
		DROPLIST.put(Lakin_Undine, new int[]{
				SOUL_SHARD,
				60
		});
		DROPLIST.put(Salamander_Rowin, new int[]{
				SOUL_SHARD,
				77
		});
		DROPLIST.put(Lakin_Salamander, new int[]{
				SOUL_SHARD,
				77
		});
		DROPLIST.put(Death_Fire, new int[]{
				SOUL_SHARD,
				85
		});

		addLevelCheck("30376-01.htm", 25/*, 37*/);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("30376-03.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30376-08.htm"))
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
		switch (npcId)
		{
			case NELL:
				if (cond == 0)
					htmltext = "30376-02.htm";
				else if (cond == 1)
					htmltext = "30376-04.htm";
				else if (cond == 2)
				{
					htmltext = "30376-05.htm";
					if(st.takeItems(SOUL_SHARD, -1) > 0)
						st.giveItems(ADENA_ID, 3000);
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int cond = qs.getCond();
		if (cond == 1)
		{
			int[] drop = DROPLIST.get(npc.getNpcId());
			if (drop == null)
				return null;

			qs.rollAndGive(SOUL_SHARD, 1, 1, 200, drop[1]);
			if (qs.getQuestItemsCount(SOUL_SHARD) >= 200)
				qs.setCond(2);
		}
		return null;
	}
}