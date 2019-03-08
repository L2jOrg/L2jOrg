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

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * RequestShowBoard client packet implementation.
 * @author Zoey76
 */
public final class RequestShowBoard extends IClientIncomingPacket
{
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_unknown = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		CommunityBoardHandler.getInstance().handleParseCommand(Config.BBS_DEFAULT, client.getActiveChar());
	}
}
