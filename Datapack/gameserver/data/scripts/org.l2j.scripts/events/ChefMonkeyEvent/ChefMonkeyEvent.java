/*
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
package events.ChefMonkeyEvent;

import events.ScriptEvent;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.LongTimeEvent;

/**
 * Chef Monkey Event
 * URL https://eu.4gameforum.com/threads/603119/
 * @author Mobius
 */
public final class ChefMonkeyEvent extends LongTimeEvent implements ScriptEvent
{
	// NPC
	private static final int CHEF_MONKEY = 34292;
	
	private ChefMonkeyEvent()
	{
		addStartNpc(CHEF_MONKEY);
		addFirstTalkId(CHEF_MONKEY);
		addTalkId(CHEF_MONKEY);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "34292-01.htm";
	}

	public static ScriptEvent provider() {
		return new ChefMonkeyEvent();
	}
}
