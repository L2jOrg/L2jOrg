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
package quests.Q00127_FishingSpecialistsRequest;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import static java.util.Objects.isNull;

/**
 * Fishing Specialist's Request (127)
 * @author Mobius
 */
public class Q00127_FishingSpecialistsRequest extends Quest
{
	// NPCs
	private static final int PIERRE = 30013;
	private static final int FERMA = 30015;
	private static final int BAIKAL = 30016;
	// Items
	private static final int PIERRE_LETTER = 49510;
	private static final int FISH_REPORT = 49504;
	private static final int SEALED_BOTTLE = 49505;
	private static final int FISHING_ROD_CHEST = 49507;
	// Location
	private static final Location TELEPORT_LOC = new Location(105276, 162500, -3600);
	// Misc
	private static final int MIN_LEVEL = 20;
	private static final int MAX_LEVEL = 110;
	
	public Q00127_FishingSpecialistsRequest()
	{
		super(127);
		addStartNpc(PIERRE);
		addTalkId(PIERRE, FERMA, BAIKAL);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30013-00.htm");
		registerQuestItems(PIERRE_LETTER, FISH_REPORT, SEALED_BOTTLE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		final QuestState qs = getQuestState(player, false);
		if (isNull(qs)) {
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30013-02.html":
			{
				qs.startQuest();
				giveItems(player, PIERRE_LETTER, 1);
				htmltext = event;
				break;
			}
			case "teleport_to_ferma":
			{
				player.teleToLocation(TELEPORT_LOC);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if(isNull(qs)) {
			return htmltext;
		}
		
		if (qs.isCreated())
		{
			if (npc.getId() == PIERRE)
			{
				htmltext = player.getLevel() < MIN_LEVEL ? "30013-00.htm" : "30013-01.htm";
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PIERRE:
				{
					switch (qs.getCond())
					{
						case 1:
						case 2:
						{
							htmltext = "30013-03.html";
							break;
						}
						case 3:
						{
							takeItems(player, -1, SEALED_BOTTLE);
							giveItems(player, FISHING_ROD_CHEST, 1);
							qs.exitQuest(false, true);
							htmltext = "30013-04.html";
							break;
						}
					}
					break;
				}
				case FERMA:
				{
					switch (qs.getCond())
					{
						case 1:
						{
							takeItems(player, -1, PIERRE_LETTER);
							giveItems(player, FISH_REPORT, 1);
							qs.setCond(2, true);
							htmltext = "30015-01.html";
							break;
						}
						case 2:
						{
							htmltext = "30015-02.html";
							break;
						}
						case 3:
						{
							htmltext = "30015-03.html";
							break;
						}
					}
					break;
				}
				case BAIKAL:
				{
					switch (qs.getCond())
					{
						case 2:
						{
							takeItems(player, -1, FISH_REPORT);
							giveItems(player, SEALED_BOTTLE, 1);
							qs.setCond(3, true);
							htmltext = "30016-01.html";
							break;
						}
						case 3:
						{
							htmltext = "30016-02.html";
							break;
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
}
