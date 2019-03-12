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
package quests.Q00293_TheHiddenVeins;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

/**
 * The Hidden Veins (293)
 * @author xban1x
 */
public final class Q00293_TheHiddenVeins extends Quest
{
	// NPCs
	private static final int FILAUR = 30535;
	private static final int CHICHIRIN = 30539;
	// Items
	private static final int CHRYSOLITE_ORE = 1488;
	private static final int TORN_MAP_FRAGMENT = 1489;
	private static final int HIDDEN_ORE_MAP = 1490;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20446,
		20447,
		20448,
	};
	// Misc
	private static final int MIN_LVL = 6;
	private static final int REQUIRED_TORN_MAP_FRAGMENT = 4;
	
	public Q00293_TheHiddenVeins()
	{
		super(293);
		addStartNpc(FILAUR);
		addTalkId(FILAUR, CHICHIRIN);
		addKillId(MONSTERS);
		registerQuestItems(CHRYSOLITE_ORE, TORN_MAP_FRAGMENT, HIDDEN_ORE_MAP);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30535-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30535-07.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30535-08.html":
			{
				htmltext = event;
				break;
			}
			case "30539-03.html":
			{
				if (getQuestItemsCount(player, TORN_MAP_FRAGMENT) >= REQUIRED_TORN_MAP_FRAGMENT)
				{
					giveItems(player, HIDDEN_ORE_MAP, 1);
					takeItems(player, TORN_MAP_FRAGMENT, REQUIRED_TORN_MAP_FRAGMENT);
					htmltext = event;
				}
				else
				{
					htmltext = "30539-02.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if (st != null)
		{
			final int chance = getRandom(100);
			if (chance > 50)
			{
				giveItems(killer, CHRYSOLITE_ORE, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else if (chance < 5)
			{
				giveItems(killer, TORN_MAP_FRAGMENT, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case FILAUR:
			{
				switch (st.getState())
				{
					case State.CREATED:
					{
						htmltext = (player.getRace() == Race.DWARF) ? (player.getLevel() >= MIN_LVL) ? "30535-03.htm" : "30535-02.htm" : "30535-01.htm";
						break;
					}
					case State.STARTED:
					{
						if (hasAtLeastOneQuestItem(player, CHRYSOLITE_ORE, HIDDEN_ORE_MAP))
						{
							final long ores = getQuestItemsCount(player, CHRYSOLITE_ORE);
							final long maps = getQuestItemsCount(player, HIDDEN_ORE_MAP);
							giveAdena(player, (ores * 5) + (maps * 150) + (((ores + maps) >= 10) ? 1000 : 0), true);
							takeItems(player, -1, CHRYSOLITE_ORE, HIDDEN_ORE_MAP);
							// Q00281_HeadForTheHills.giveNewbieReward(player);
							htmltext = (ores > 0) ? (maps > 0) ? "30535-10.html" : "30535-06.html" : "30535-09.html";
						}
						else
						{
							htmltext = "30535-05.html";
						}
						break;
					}
				}
				break;
			}
			case CHICHIRIN:
			{
				htmltext = "30539-01.html";
				break;
			}
		}
		return htmltext;
	}
}
