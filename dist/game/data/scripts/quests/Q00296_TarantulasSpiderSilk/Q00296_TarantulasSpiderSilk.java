/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00296_TarantulasSpiderSilk;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.util.Util;

/**
 * Tarantula's Spider Silk (296)
 * @author xban1x
 */
public final class Q00296_TarantulasSpiderSilk extends Quest
{
	// NPCs
	private static final int TRADER_MION = 30519;
	private static final int DEFENDER_NATHAN = 30548;
	// Items
	private static final int TARANTULA_SPIDER_SILK = 1493;
	private static final int TARANTULA_SPINNERETTE = 1494;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20394,
		20403,
		20508,
	};
	// Misc
	private static final int MIN_LVL = 15;
	
	public Q00296_TarantulasSpiderSilk()
	{
		super(296);
		addStartNpc(TRADER_MION);
		addTalkId(TRADER_MION, DEFENDER_NATHAN);
		addKillId(MONSTERS);
		registerQuestItems(TARANTULA_SPIDER_SILK, TARANTULA_SPINNERETTE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String html = null;
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "30519-03.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					html = event;
				}
				break;
			}
			case "30519-06.html":
			{
				if (qs.isStarted())
				{
					qs.exitQuest(true, true);
					html = event;
				}
				break;
			}
			case "30519-07.html":
			{
				if (qs.isStarted())
				{
					html = event;
				}
				break;
			}
			case "30548-03.html":
			{
				if (qs.isStarted())
				{
					if (hasQuestItems(player, TARANTULA_SPINNERETTE))
					{
						giveItems(player, TARANTULA_SPIDER_SILK, (15 + getRandom(9)) * getQuestItemsCount(player, TARANTULA_SPINNERETTE));
						takeItems(player, TARANTULA_SPINNERETTE, -1);
						html = event;
					}
					else
					{
						html = "30548-02.html";
					}
				}
				break;
			}
		}
		return html;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && Util.checkIfInRange(1500, npc, killer, true))
		{
			final int chance = getRandom(100);
			if (chance > 95)
			{
				giveItemRandomly(killer, npc, TARANTULA_SPINNERETTE, 1, 0, 1, true);
			}
			else if (chance > 45)
			{
				giveItemRandomly(killer, npc, TARANTULA_SPIDER_SILK, 1, 0, 1, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		if (qs.isCreated() && (npc.getId() == TRADER_MION))
		{
			html = (talker.getLevel() >= MIN_LVL ? "30519-02.htm" : "30519-01.htm");
		}
		else if (qs.isStarted())
		{
			if (npc.getId() == TRADER_MION)
			{
				final long silk = getQuestItemsCount(talker, TARANTULA_SPIDER_SILK);
				if (silk >= 1)
				{
					giveAdena(talker, (silk * 5) + (silk >= 10 ? 1000 : 0), true);
					takeItems(talker, TARANTULA_SPIDER_SILK, -1);
					// Q00281_HeadForTheHills.giveNewbieReward(talker);// TODO: It's using wrong bitmask, need to create a general bitmask for this using EnumIntBitmask class inside Quest class for handling Quest rewards.
					html = "30519-05.html";
				}
				else
				{
					html = "30519-04.html";
				}
			}
			else if (npc.getId() == DEFENDER_NATHAN)
			{
				html = "30548-01.html";
			}
		}
		return html;
	}
}
