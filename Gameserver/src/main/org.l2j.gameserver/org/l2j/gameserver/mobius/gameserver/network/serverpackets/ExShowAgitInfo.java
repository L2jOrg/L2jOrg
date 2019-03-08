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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.mobius.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowAgitInfo implements IClientOutgoingPacket
{
	public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();
	
	private ExShowAgitInfo()
	{
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_AGIT_INFO.writeId(packet);
		
		final Collection<ClanHall> clanHalls = ClanHallData.getInstance().getClanHalls();
		packet.writeD(clanHalls.size());
		clanHalls.forEach(clanHall ->
		{
			packet.writeD(clanHall.getResidenceId());
			packet.writeS(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()); // owner clan name
			packet.writeS(clanHall.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()); // leader name
			packet.writeD(clanHall.getType().getClientVal()); // Clan hall type
		});
		return true;
	}
}
