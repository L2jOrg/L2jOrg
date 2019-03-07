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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.dailymission;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;

/**
 * @author Mobius
 */
public class RequestTodoListHTML implements IClientIncomingPacket
{
	@SuppressWarnings("unused")
	private int _tab;
	@SuppressWarnings("unused")
	private String _linkName;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_tab = packet.readC();
		_linkName = packet.readS();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
	}
}
