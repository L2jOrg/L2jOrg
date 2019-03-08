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
import org.l2j.gameserver.mobius.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * RequestBBSwrite client packet implementation.
 * @author -Wooden-, Zoey76
 */
public final class RequestBBSwrite extends IClientIncomingPacket
{
	private String _url;
	private String _arg1;
	private String _arg2;
	private String _arg3;
	private String _arg4;
	private String _arg5;
	
	@Override
	public final boolean read(L2GameClient client, PacketReader packet)
	{
		_url = readString(packet);
		_arg1 = readString(packet);
		_arg2 = readString(packet);
		_arg3 = readString(packet);
		_arg4 = readString(packet);
		_arg5 = readString(packet);
		return true;
	}
	
	@Override
	public final void run(L2GameClient client)
	{
		CommunityBoardHandler.getInstance().handleWriteCommand(client.getActiveChar(), _url, _arg1, _arg2, _arg3, _arg4, _arg5);
	}
}