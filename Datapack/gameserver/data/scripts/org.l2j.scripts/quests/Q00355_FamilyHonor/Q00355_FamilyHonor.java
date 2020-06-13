/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00355_FamilyHonor;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Family Honor (355)
 * @author Adry_85
 */
public final class Q00355_FamilyHonor extends Quest
{
	private static final class DropInfo
	{
		public final int _firstChance;
		public final int _secondChance;
		
		public DropInfo(int firstChance, int secondChance)
		{
			_firstChance = firstChance;
			_secondChance = secondChance;
		}
		
		public int getFirstChance()
		{
			return _firstChance;
		}
		
		public int getSecondChance()
		{
			return _secondChance;
		}
	}
	
	// NPCs
	private static final int GALIBREDO = 30181;
	private static final int PATRIN = 30929;
	// Items
	private static final int GALFREDO_ROMERS_BUST = 4252;
	private static final int SCULPTOR_BERONA = 4350;
	private static final int ANCIENT_STATUE_PROTOTYPE = 4351;
	private static final int ANCIENT_STATUE_ORIGINAL = 4352;
	private static final int ANCIENT_STATUE_REPLICA = 4353;
	private static final int ANCIENT_STATUE_FORGERY = 4354;
	// Misc
	private static final int MIN_LEVEL = 36;
	
	private static final Map<Integer, DropInfo> MOBS = new HashMap<>();
	static
	{
		MOBS.put(20767, new DropInfo(560, 684)); // timak_orc_troop_leader
		MOBS.put(20768, new DropInfo(530, 650)); // timak_orc_troop_shaman
		MOBS.put(20769, new DropInfo(420, 516)); // timak_orc_troop_warrior
		MOBS.put(20770, new DropInfo(440, 560)); // timak_orc_troop_archer
	}
	
	public Q00355_FamilyHonor()
	{
		super(355);
		addStartNpc(GALIBREDO);
		addTalkId(GALIBREDO, PATRIN);
		addKillId(MOBS.keySet());
		registerQuestItems(GALFREDO_ROMERS_BUST);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30181-02.htm":
			case "30181-09.html":
			case "30929-01.html":
			case "30929-02.html":
			{
				htmltext = event;
				break;
			}
			case "30181-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30181-06.html":
			{
				final long galfredoRomersBustCount = getQuestItemsCount(player, GALFREDO_ROMERS_BUST);
				
				if (galfredoRomersBustCount < 1)
				{
					htmltext = event;
				}
				else if (galfredoRomersBustCount >= 100)
				{
					giveAdena(player, (galfredoRomersBustCount * 20), true);
					takeItems(player, GALFREDO_ROMERS_BUST, -1);
					htmltext = "30181-07.html";
				}
				else
				{
					giveAdena(player, (galfredoRomersBustCount * 20), true);
					takeItems(player, GALFREDO_ROMERS_BUST, -1);
					htmltext = "30181-08.html";
				}
				break;
			}
			case "30181-10.html":
			{
				final long galfredoRomersBustCount = getQuestItemsCount(player, GALFREDO_ROMERS_BUST);
				
				if (galfredoRomersBustCount > 0)
				{
					giveAdena(player, galfredoRomersBustCount * 120, true);
				}
				
				takeItems(player, GALFREDO_ROMERS_BUST, -1);
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30929-03.html":
			{
				final int random = getRandom(100);
				
				if (hasQuestItems(player, SCULPTOR_BERONA))
				{
					if (random < 2)
					{
						giveItems(player, ANCIENT_STATUE_PROTOTYPE, 1);
						htmltext = event;
					}
					else if (random < 32)
					{
						giveItems(player, ANCIENT_STATUE_ORIGINAL, 1);
						htmltext = "30929-04.html";
					}
					else if (random < 62)
					{
						giveItems(player, ANCIENT_STATUE_REPLICA, 1);
						htmltext = "30929-05.html";
					}
					else if (random < 77)
					{
						giveItems(player, ANCIENT_STATUE_FORGERY, 1);
						htmltext = "30929-06.html";
					}
					else
					{
						htmltext = "30929-07.html";
					}
					
					takeItems(player, SCULPTOR_BERONA, 1);
				}
				else
				{
					htmltext = "30929-08.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs == null) || !GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return null;
		}
		
		final DropInfo info = MOBS.get(npc.getId());
		final int random = getRandom(1000);
		
		if (random < info.getFirstChance())
		{
			giveItemRandomly(killer, npc, GALFREDO_ROMERS_BUST, 1, 0, 1.0, true);
		}
		else if (random < info.getSecondChance())
		{
			giveItemRandomly(killer, npc, SCULPTOR_BERONA, 1, 0, 1.0, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30181-01.htm" : "30181-04.html";
		}
		else if (qs.isStarted())
		{
			if (npc.getId() == GALIBREDO)
			{
				if (hasQuestItems(player, SCULPTOR_BERONA))
				{
					htmltext = "30181-11.html";
				}
				else
				{
					htmltext = "30181-05.html";
				}
			}
			else
			{
				htmltext = "30929-01.html";
			}
		}
		return htmltext;
	}
}
