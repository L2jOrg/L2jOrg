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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.mobius.gameserver.model.CursedWeapon;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExCursedWeaponLocation;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Format: (ch)
 * @author -Wooden-
 */
public final class RequestCursedWeaponLocation extends IClientIncomingPacket
{
	@Override
	public void readImpl(ByteBuffer packet)
	{
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final List<CursedWeaponInfo> list = new LinkedList<>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			if (!cw.isActive())
			{
				continue;
			}
			
			final Location pos = cw.getWorldPosition();
			if (pos != null)
			{
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
			}
		}
		
		// send the ExCursedWeaponLocation
		if (!list.isEmpty())
		{
			client.sendPacket(new ExCursedWeaponLocation(list));
		}
	}
}
