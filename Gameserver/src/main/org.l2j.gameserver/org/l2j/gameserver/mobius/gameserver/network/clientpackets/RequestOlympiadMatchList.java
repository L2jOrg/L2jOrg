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
import org.l2j.gameserver.mobius.gameserver.handler.BypassHandler;
import org.l2j.gameserver.mobius.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * format ch c: (id) 0xD0 h: (subid) 0x13
 * @author -Wooden-
 */
public final class RequestOlympiadMatchList extends IClientIncomingPacket
{
	private static final String COMMAND = "arenalist";
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if ((activeChar == null) || !activeChar.inObserverMode())
		{
			return;
		}
		
		final IBypassHandler handler = BypassHandler.getInstance().getHandler(COMMAND);
		if (handler != null)
		{
			handler.useBypass(COMMAND, activeChar, null);
		}
	}
}