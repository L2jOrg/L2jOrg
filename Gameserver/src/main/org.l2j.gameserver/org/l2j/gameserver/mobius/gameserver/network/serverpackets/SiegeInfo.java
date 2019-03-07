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

import com.l2jmobius.Config;
import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.data.sql.impl.ClanTable;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.entity.Castle;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.Calendar;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related?
 * @author KenM
 */
public class SiegeInfo implements IClientOutgoingPacket
{
	private final Castle _castle;
	private final L2PcInstance _player;
	
	public SiegeInfo(Castle castle, L2PcInstance player)
	{
		_castle = castle;
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.CASTLE_SIEGE_INFO.writeId(packet);
		
		if (_castle != null)
		{
			packet.writeD(_castle.getResidenceId());
			
			final int ownerId = _castle.getOwnerId();
			
			packet.writeD(((ownerId == _player.getClanId()) && (_player.isClanLeader())) ? 0x01 : 0x00);
			packet.writeD(ownerId);
			if (ownerId > 0)
			{
				final L2Clan owner = ClanTable.getInstance().getClan(ownerId);
				if (owner != null)
				{
					packet.writeS(owner.getName()); // Clan Name
					packet.writeS(owner.getLeaderName()); // Clan Leader Name
					packet.writeD(owner.getAllyId()); // Ally ID
					packet.writeS(owner.getAllyName()); // Ally Name
				}
				else
				{
					LOGGER.warning("Null owner for castle: " + _castle.getName());
				}
			}
			else
			{
				packet.writeS(""); // Clan Name
				packet.writeS(""); // Clan Leader Name
				packet.writeD(0); // Ally ID
				packet.writeS(""); // Ally Name
			}
			
			packet.writeD((int) (System.currentTimeMillis() / 1000));
			if (!_castle.getIsTimeRegistrationOver() && _player.isClanLeader() && (_player.getClanId() == _castle.getOwnerId()))
			{
				final Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(_castle.getSiegeDate().getTimeInMillis());
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				
				packet.writeD(0x00);
				packet.writeD(Config.SIEGE_HOUR_LIST.size());
				for (int hour : Config.SIEGE_HOUR_LIST)
				{
					cal.set(Calendar.HOUR_OF_DAY, hour);
					packet.writeD((int) (cal.getTimeInMillis() / 1000));
				}
			}
			else
			{
				packet.writeD((int) (_castle.getSiegeDate().getTimeInMillis() / 1000));
				packet.writeD(0x00);
			}
		}
		return true;
	}
}
