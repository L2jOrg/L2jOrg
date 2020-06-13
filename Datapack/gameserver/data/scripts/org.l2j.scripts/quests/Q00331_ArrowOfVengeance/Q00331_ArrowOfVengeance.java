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
package quests.Q00331_ArrowOfVengeance;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Arrow for Vengeance (331)
 * @author xban1x
 */
public class Q00331_ArrowOfVengeance extends Quest
{
	// NPCs
	private static final int BELTON = 30125;
	// Items
	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRMS_TOOTH = 1454;
	// Monster
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(20145, 59); // Harpy
		MONSTERS.put(20158, 61); // Medusa
		MONSTERS.put(20176, 60); // Wyrm
	}
	// Misc
	private static final int MIN_LVL = 32;
	private static final int HARPY_FEATHER_ADENA = 6;
	private static final int MEDUSA_VENOM_ADENA = 7;
	private static final int WYRMS_TOOTH_ADENA = 9;
	private static final int BONUS = 1000;
	private static final int BONUS_COUNT = 10;
	
	public Q00331_ArrowOfVengeance()
	{
		super(331);
		addStartNpc(BELTON);
		addTalkId(BELTON);
		addKillId(MONSTERS.keySet());
		registerQuestItems(HARPY_FEATHER, MEDUSA_VENOM, WYRMS_TOOTH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null)
		{
			switch (event)
			{
				case "30125-03.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
				case "30125-06.html":
				{
					st.exitQuest(true, true);
					htmltext = event;
					break;
				}
				case "30125-07.html":
				{
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = player.getLevel() < MIN_LVL ? "30125-01.htm" : "30125-02.htm";
				break;
			}
			case State.STARTED:
			{
				final long harpyFeathers = getQuestItemsCount(player, HARPY_FEATHER);
				final long medusaVenoms = getQuestItemsCount(player, MEDUSA_VENOM);
				final long wyrmsTeeth = getQuestItemsCount(player, WYRMS_TOOTH);
				if ((harpyFeathers + medusaVenoms + wyrmsTeeth) > 0)
				{
					giveAdena(player, ((harpyFeathers * HARPY_FEATHER_ADENA) + (medusaVenoms * MEDUSA_VENOM_ADENA) + (wyrmsTeeth * WYRMS_TOOTH_ADENA) + ((harpyFeathers + medusaVenoms + wyrmsTeeth) >= BONUS_COUNT ? BONUS : 0)), true);
					takeItems(player, -1, HARPY_FEATHER, MEDUSA_VENOM, WYRMS_TOOTH);
					htmltext = "30125-05.html";
				}
				else
				{
					htmltext = "30125-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if (st != null)
		{
			if (getRandom(100) < MONSTERS.get(npc.getId()))
			{
				switch (npc.getId())
				{
					case 20145:
					{
						giveItems(player, HARPY_FEATHER, 1);
						break;
					}
					case 20158:
					{
						giveItems(player, MEDUSA_VENOM, 1);
						break;
					}
					case 20176:
					{
						giveItems(player, WYRMS_TOOTH, 1);
						break;
					}
				}
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isPet);
	}
}
