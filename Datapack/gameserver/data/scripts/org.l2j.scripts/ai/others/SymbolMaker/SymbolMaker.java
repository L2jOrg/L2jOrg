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
package ai.others.SymbolMaker;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;
import org.l2j.gameserver.network.serverpackets.HennaRemoveList;

/**
 * Symbol Maker AI.
 * @author Adry_85
 */
public final class SymbolMaker extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		31046, // Marsden
		31047, // Kell
		31048, // McDermott
		31049, // Pepper
		31050, // Thora
		31051, // Keach
		31052, // Heid
		31053, // Kidder
		31264, // Olsun
	};
	
	private SymbolMaker()
	{
		addFirstTalkId(NPCS);
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "symbol_maker.htm":
			case "symbol_maker-1.htm":
			case "symbol_maker-2.htm":
			case "symbol_maker-3.htm":
			{
				htmltext = event;
				break;
			}
			case "Draw":
			{
				player.sendPacket(new HennaEquipList(player));
				break;
			}
			case "Remove":
			{
				player.sendPacket(new HennaRemoveList(player));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "symbol_maker.htm";
	}
	
	public static AbstractNpcAI provider()
	{
		return new SymbolMaker();
	}
}