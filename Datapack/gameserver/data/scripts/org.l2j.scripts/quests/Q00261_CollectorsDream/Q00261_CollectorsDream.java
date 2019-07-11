package quests.Q00261_CollectorsDream;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;

/**
 * Collector's Dream (261)
 * @author xban1x
 */
public final class Q00261_CollectorsDream extends Quest
{
	// Npc
	private static final int ALSHUPES = 30222;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20308, // Hook Spider
		20460, // Crimson Spider
		20466, // Pincer Spider
	};
	// Item
	private static final int SPIDER_LEG = 1087;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LEG_COUNT = 8;
	// Message
	private static final ExShowScreenMessage MESSAGE = new ExShowScreenMessage(NpcStringId.LAST_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_HELPER, 2, 5000);
	
	public Q00261_CollectorsDream()
	{
		super(261);
		addStartNpc(ALSHUPES);
		addTalkId(ALSHUPES);
		addKillId(MONSTERS);
		registerQuestItems(SPIDER_LEG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30222-03.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			if (giveItemRandomly(killer, SPIDER_LEG, 1, MAX_LEG_COUNT, 1, true))
			{
				st.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LVL) ? "30222-02.htm" : "30222-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30222-04.html";
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, SPIDER_LEG) >= MAX_LEG_COUNT)
						{
							giveNewbieReward(player);
							giveAdena(player, 700, true);
							st.exitQuest(true, true);
							htmltext = "30222-05.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void giveNewbieReward(Player player)
	{
		final PlayerVariables vars = player.getVariables();
		if (vars.getString("GUIDE_MISSION", null) == null)
		{
			vars.set("GUIDE_MISSION", 100000);
			player.sendPacket(MESSAGE);
		}
		else if (((vars.getInt("GUIDE_MISSION") % 100000000) / 10000000) != 1)
		{
			vars.set("GUIDE_MISSION", vars.getInt("GUIDE_MISSION") + 10000000);
			player.sendPacket(MESSAGE);
		}
	}
}
