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
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AskJoinPledge;

/**
 * @author Mobius
 */
public class RequestClanAskJoinByName extends IClientIncomingPacket
{
	private String _playerName;
	private int _pledgeType;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_playerName = readString(packet);
		_pledgeType = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if ((activeChar == null) || (activeChar.getClan() == null))
		{
			return;
		}
		
		final L2PcInstance invitedPlayer = L2World.getInstance().getPlayer(_playerName);
		if (!activeChar.getClan().checkClanJoinCondition(activeChar, invitedPlayer, _pledgeType))
		{
			return;
		}
		if (!activeChar.getRequest().setRequest(invitedPlayer, this))
		{
			return;
		}
		
		invitedPlayer.sendPacket(new AskJoinPledge(activeChar, _pledgeType, activeChar.getClan().getName()));
	}
}