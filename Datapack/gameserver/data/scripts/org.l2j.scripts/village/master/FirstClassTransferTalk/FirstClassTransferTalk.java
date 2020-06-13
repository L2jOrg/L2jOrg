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
package village.master.FirstClassTransferTalk;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.VillageMasterFighter;
import org.l2j.gameserver.model.actor.instance.VillageMasterPriest;

import java.util.HashMap;
import java.util.Map;

/**
 * This script manages the dialogs of the headmasters of all newbie villages.<br>
 * None of them provide actual class transfers, they only talk about it.
 * @author jurchiks, xban1x
 */
public final class FirstClassTransferTalk extends AbstractNpcAI
{
	private static final Map<Integer, Race> MASTERS = new HashMap<>();
	static
	{
		MASTERS.put(30026, Race.HUMAN); // Blitz, TI Fighter Guild Head Master
		MASTERS.put(30031, Race.HUMAN); // Biotin, TI Einhasad Temple High Priest
		MASTERS.put(30154, Race.ELF); // Asterios, Elven Village Tetrarch
		MASTERS.put(30358, Race.DARK_ELF); // Thifiell, Dark Elf Village Tetrarch
		MASTERS.put(30565, Race.ORC); // Kakai, Orc Village Flame Lord
		MASTERS.put(30520, Race.DWARF); // Reed, Dwarven Village Warehouse Chief
		MASTERS.put(30525, Race.DWARF); // Bronk, Dwarven Village Head Blacksmith
	}
	
	private FirstClassTransferTalk()
	{
		addStartNpc(MASTERS.keySet());
		addTalkId(MASTERS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = npc.getId() + "_";
		
		if (MASTERS.get(npc.getId()) != player.getRace())
		{
			return htmltext += "no.html";
		}
		
		switch (MASTERS.get(npc.getId()))
		{
			case HUMAN:
			{
				if (player.getClassId().level() == 0)
				{
					if (player.isMageClass())
					{
						if (npc instanceof VillageMasterPriest)
						{
							htmltext += "mystic.html";
						}
						else
						{
							htmltext += "no.html";
						}
					}
					else if (npc instanceof VillageMasterFighter)
					{
						htmltext += "fighter.html";
					}
					else
					{
						htmltext += "no.html";
					}
				}
				else if (player.getClassId().level() == 1)
				{
					htmltext += "transfer_1.html";
				}
				else
				{
					htmltext += "transfer_2.html";
				}
				break;
			}
			case ELF:
			case DARK_ELF:
			case ORC:
			{
				if (player.getClassId().level() == 0)
				{
					if (player.isMageClass())
					{
						htmltext += "mystic.html";
					}
					else
					{
						htmltext += "fighter.html";
					}
				}
				else if (player.getClassId().level() == 1)
				{
					htmltext += "transfer_1.html";
				}
				else
				{
					htmltext += "transfer_2.html";
				}
				break;
			}
			case DWARF:
			{
				if (player.getClassId().level() == 0)
				{
					htmltext += "fighter.html";
				}
				else if (player.getClassId().level() == 1)
				{
					htmltext += "transfer_1.html";
				}
				else
				{
					htmltext += "transfer_2.html";
				}
				break;
			}
			default:
			{
				htmltext += "no.html";
				break;
			}
		}
		return htmltext;
	}
	
	public static FirstClassTransferTalk provider()
	{
		return new FirstClassTransferTalk();
	}
}
