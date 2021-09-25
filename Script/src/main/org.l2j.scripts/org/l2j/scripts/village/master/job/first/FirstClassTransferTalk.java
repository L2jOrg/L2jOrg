/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.village.master.job.first;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * This script manages the dialogs of the headmasters of all newbie villages.<br>
 * None of them provide actual class transfers, they only talk about it.
 * @author jurchiks, xban1x
 */
public final class FirstClassTransferTalk extends AbstractNpcAI
{
	private static final IntMap<Race> MASTERS = new HashIntMap<>();
	public static final String NO_HTML = "no.html";
	public static final String FIGHTER_HTML = "fighter.html";
	public static final String TRANSFER_1_HTML = "transfer_1.html";
	public static final String TRANSFER_2_HTML = "transfer_2.html";

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
	
	private FirstClassTransferTalk() {
		addStartNpc(MASTERS.keySet());
		addTalkId(MASTERS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player) {
		String htmlText = npc.getId() + "_";
		
		if (MASTERS.get(npc.getId()) != player.getRace()) {
			return htmlText + NO_HTML;
		}

		switch (MASTERS.get(npc.getId())) {
			case HUMAN -> htmlText = onHumanTalk(npc, player, htmlText);
			case DWARF -> htmlText = onDwarfTalk(player, htmlText);
			case ELF, DARK_ELF, ORC -> htmlText = onElvenOrOrcsTalk(player, htmlText);
			default -> htmlText += NO_HTML;
		}
		return htmlText;
	}

	private String onElvenOrOrcsTalk(Player player, String htmlText) {
		if (player.getClassId().level() == 0) {
			if (player.isMageClass()) {
				htmlText += "mystic.html";
			} else {
				htmlText += FIGHTER_HTML;
			}
		} else if (player.getClassId().level() == 1) {
			htmlText += TRANSFER_1_HTML;
		} else {
			htmlText += TRANSFER_2_HTML;
		}
		return htmlText;
	}

	private String onHumanTalk(Npc npc, Player player, String htmlText) {
		if (player.getClassId().level() == 0) {
			if (player.isMageClass()) {
				if (npc.getId() == 30031) {
					htmlText += "mystic.html";
				} else {
					htmlText += NO_HTML;
				}
			} else if (npc.getId() == 30026) {
				htmlText += FIGHTER_HTML;
			} else {
				htmlText += NO_HTML;
			}
		} else if (player.getClassId().level() == 1) {
			htmlText += TRANSFER_1_HTML;
		} else {
			htmlText += TRANSFER_2_HTML;
		}
		return htmlText;
	}

	private String onDwarfTalk(Player player, String htmlText) {
		if (player.getClassId().level() == 0) {
			htmlText += FIGHTER_HTML;
		} else if (player.getClassId().level() == 1) {
			htmlText += TRANSFER_1_HTML;
		} else {
			htmlText += TRANSFER_2_HTML;
		}
		return htmlText;
	}

	public static FirstClassTransferTalk provider()
	{
		return new FirstClassTransferTalk();
	}
}
