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
package events.ChefMonkeyEvent;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.LongTimeEvent;

/**
 * Chef Monkey Event
 * @URL https://eu.4gameforum.com/threads/603119/
 * @author Mobius
 */
public final class ChefMonkeyEvent extends LongTimeEvent
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
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "34292-01.htm";
	}
	
	public static void main(String[] args)
	{
		new ChefMonkeyEvent();
	}
}
