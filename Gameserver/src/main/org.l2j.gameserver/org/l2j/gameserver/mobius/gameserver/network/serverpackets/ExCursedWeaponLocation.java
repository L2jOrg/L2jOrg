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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * Format: (ch) d[ddddd]
 * @author -Wooden-
 */
public class ExCursedWeaponLocation implements IClientOutgoingPacket
{
	private final List<CursedWeaponInfo> _cursedWeaponInfo;
	
	public ExCursedWeaponLocation(List<CursedWeaponInfo> cursedWeaponInfo)
	{
		_cursedWeaponInfo = cursedWeaponInfo;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CURSED_WEAPON_LOCATION.writeId(packet);
		
		if (!_cursedWeaponInfo.isEmpty())
		{
			packet.writeD(_cursedWeaponInfo.size());
			for (CursedWeaponInfo w : _cursedWeaponInfo)
			{
				packet.writeD(w.id);
				packet.writeD(w.activated);
				
				packet.writeD(w.pos.getX());
				packet.writeD(w.pos.getY());
				packet.writeD(w.pos.getZ());
			}
		}
		else
		{
			packet.writeD(0);
		}
		return true;
	}
	
	public static class CursedWeaponInfo
	{
		public Location pos;
		public int id;
		public int activated; // 0 - not activated ? 1 - activated
		
		public CursedWeaponInfo(Location p, int ID, int status)
		{
			pos = p;
			id = ID;
			activated = status;
		}
		
	}
}
