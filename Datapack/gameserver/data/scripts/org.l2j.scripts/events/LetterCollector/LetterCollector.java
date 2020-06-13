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
package events.LetterCollector;

import events.ScriptEvent;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.LongTimeEvent;

/**
 * Event: Letter Collector
 * @URL https://eu.4gameforum.com/threads/648400/
 * @author Mobius, Gigi, Adapted for Classic by QuangNguyen
 */
public final class LetterCollector extends LongTimeEvent implements ScriptEvent
{
	// NPC
	private static final int ROSALIA = 9000;
	// Items
	private static final int A = 3875;
	private static final int C = 3876;
	private static final int E = 3877;
	private static final int G = 3879;
	private static final int I = 3881;
	private static final int L = 3882;
	private static final int N = 3883;
	private static final int R = 3885;
	private static final int M = 34956;
	private static final int O = 3884;
	private static final int S = 3886;
	private static final int H = 3880;
	private static final int II = 3888;
	// Exchange Letters
	private static final int[] LETTERS =
	{
		A,
		C,
		E,
		G,
		I,
		L,
		N,
		R,
		M,
		O,
		S,
		H,
		II
	};
	// Reward
	private static final int REWARD = 29581;
	private static final int MEMMORIES = 29583;
	private static final int CHRONICLE = 29582;
	
	private LetterCollector()
	{
		addStartNpc(ROSALIA);
		addFirstTalkId(ROSALIA);
		addTalkId(ROSALIA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "9000-1.htm":
			case "9000-2.htm":
			{
				htmltext = event;
				break;
			}
			case "reward":
			{
				if ((getQuestItemsCount(player, L) >= 1) && //
					(getQuestItemsCount(player, I) >= 1) && //
					(getQuestItemsCount(player, N) >= 1) && //
					(getQuestItemsCount(player, E) >= 2) && //
					(getQuestItemsCount(player, A) >= 1) && //
					(getQuestItemsCount(player, G) >= 1) && //
					(getQuestItemsCount(player, II) >= 1))
				{
					takeItems(player, L, 1);
					takeItems(player, I, 1);
					takeItems(player, N, 1);
					takeItems(player, E, 2);
					takeItems(player, A, 1);
					takeItems(player, G, 1);
					takeItems(player, II, 1);
					giveItems(player, REWARD, 1);
					htmltext = "9000-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
			case "memories":
			{
				if ((getQuestItemsCount(player, M) >= 2) && //
					(getQuestItemsCount(player, E) >= 2) && //
					(getQuestItemsCount(player, O) >= 1) && //
					(getQuestItemsCount(player, R) >= 1) && //
					(getQuestItemsCount(player, I) >= 1) && //
					(getQuestItemsCount(player, S) >= 1))
				{
					takeItems(player, M, 2);
					takeItems(player, E, 2);
					takeItems(player, O, 1);
					takeItems(player, R, 1);
					takeItems(player, I, 1);
					takeItems(player, S, 1);
					giveItems(player, MEMMORIES, 1);
					htmltext = "9000-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
			case "chronicle":
			{
				if ((getQuestItemsCount(player, C) >= 2) && //
					(getQuestItemsCount(player, H) >= 1) && //
					(getQuestItemsCount(player, R) >= 1) && //
					(getQuestItemsCount(player, O) >= 1) && //
					(getQuestItemsCount(player, N) >= 1) && //
					(getQuestItemsCount(player, I) >= 1) && //
					(getQuestItemsCount(player, L) >= 1) && //
					(getQuestItemsCount(player, E) >= 1))
				{
					takeItems(player, C, 2);
					takeItems(player, H, 1);
					takeItems(player, R, 1);
					takeItems(player, O, 1);
					takeItems(player, N, 1);
					takeItems(player, I, 1);
					takeItems(player, L, 1);
					takeItems(player, E, 1);
					giveItems(player, CHRONICLE, 1);
					htmltext = "9000-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
			case "exchangeA":
			{
				if (getQuestItemsCount(player, A) >= 2)
				{
					takeItems(player, A, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeC":
			{
				if (getQuestItemsCount(player, C) >= 2)
				{
					takeItems(player, C, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeE":
			{
				if (getQuestItemsCount(player, E) >= 2)
				{
					takeItems(player, E, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeG":
			{
				if (getQuestItemsCount(player, G) >= 2)
				{
					takeItems(player, G, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeI":
			{
				if (getQuestItemsCount(player, I) >= 2)
				{
					takeItems(player, I, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeL":
			{
				if (getQuestItemsCount(player, L) >= 2)
				{
					takeItems(player, L, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeM":
			{
				if (getQuestItemsCount(player, M) >= 2)
				{
					takeItems(player, M, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeN":
			{
				if (getQuestItemsCount(player, N) >= 2)
				{
					takeItems(player, N, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeO":
			{
				if (getQuestItemsCount(player, O) >= 2)
				{
					takeItems(player, O, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeR":
			{
				if (getQuestItemsCount(player, R) >= 2)
				{
					takeItems(player, R, 2);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeH":
			{
				if (getQuestItemsCount(player, H) >= 1)
				{
					takeItems(player, H, 1);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeS":
			{
				if (getQuestItemsCount(player, S) >= 1)
				{
					takeItems(player, S, 1);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
			case "exchangeII":
			{
				if (getQuestItemsCount(player, II) >= 1)
				{
					takeItems(player, II, 1);
					giveItems(player, getRandomEntry(LETTERS), 1);
					htmltext = "9000-2.htm";
				}
				else
				{
					htmltext = "noItemExchange.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + "-1.htm";
	}

	public static ScriptEvent provider() {
		return new LetterCollector();
	}
}