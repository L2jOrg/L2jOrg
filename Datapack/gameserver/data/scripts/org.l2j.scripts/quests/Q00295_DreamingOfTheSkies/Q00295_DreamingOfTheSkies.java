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
package quests.Q00295_DreamingOfTheSkies;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.GameUtils;

/**
 * Dreaming of the Skies (295)
 * @author xban1x
 */
public final class Q00295_DreamingOfTheSkies extends Quest
{
	// NPC
	private static final int ARIN = 30536;
	// Monster
	private static final int MAGICAL_WEAVER = 20153;
	// Item
	private static final int FLOATING_STONE = 1492;
	// Reward
	private static final int RING_OF_FIREFLY = 1509;
	// Misc
	private static final int MIN_LVL = 11;
	
	public Q00295_DreamingOfTheSkies()
	{
		super(295);
		addStartNpc(ARIN);
		addTalkId(ARIN);
		addKillId(MAGICAL_WEAVER);
		registerQuestItems(FLOATING_STONE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCreated() && event.equals("30536-03.htm"))
		{
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			if (giveItemRandomly(killer, npc, FLOATING_STONE, (getRandom(100) > 25) ? 1 : 2, 50, 1.0, true))
			{
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		if (qs.isCreated())
		{
			html = (talker.getLevel() >= MIN_LVL) ? "30536-02.htm" : "30536-01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(2))
			{
				if (hasQuestItems(talker, RING_OF_FIREFLY))
				{
					giveAdena(talker, 200, true);
					html = "30536-06.html";
				}
				else
				{
					giveItems(talker, RING_OF_FIREFLY, 1);
					html = "30536-05.html";
				}
				takeItems(talker, FLOATING_STONE, -1);
				qs.exitQuest(true, true);
			}
			else
			{
				html = "30536-04.html";
			}
		}
		return html;
	}
}
