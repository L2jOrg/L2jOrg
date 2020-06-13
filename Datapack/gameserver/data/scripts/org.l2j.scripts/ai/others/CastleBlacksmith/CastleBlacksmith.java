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
package ai.others.CastleBlacksmith;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Castle Blacksmith AI.
 * @author malyelfik
 */
public final class CastleBlacksmith extends AbstractNpcAI
{
	// Blacksmith IDs
	private static final int[] NPCS =
	{
		35098, // Blacksmith (Gludio)
		35140, // Blacksmith (Dion)
		35182, // Blacksmith (Giran)
		35224, // Blacksmith (Oren)
		35272, // Blacksmith (Aden)
		35314, // Blacksmith (Innadril)
		35361, // Blacksmith (Goddard)
		35507, // Blacksmith (Rune)
		35553, // Blacksmith (Schuttgart)
	};
	
	private CastleBlacksmith()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(NPCS);
	}
	
	private boolean hasRights(Player player, Npc npc)
	{
		final boolean isMyLord = player.isClanLeader() ? (player.getClan().getCastleId() == (npc.getCastle() != null ? npc.getCastle().getId() : -1)) : false;
		return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || isMyLord || ((player.getClanId() == npc.getCastle().getOwnerId()) && player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		return (event.equalsIgnoreCase(npc.getId() + "-02.html") && hasRights(player, npc)) ? event : null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return (hasRights(player, npc)) ? npc.getId() + "-01.html" : "no.html";
	}
	
	public static AbstractNpcAI provider()
	{
		return new CastleBlacksmith();
	}
}