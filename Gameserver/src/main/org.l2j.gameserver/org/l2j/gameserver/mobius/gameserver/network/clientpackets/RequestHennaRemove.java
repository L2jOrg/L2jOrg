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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

/**
 * @author Zoey76
 */
public final class RequestHennaRemove extends IClientIncomingPacket
{
	private int _symbolId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_symbolId = packet.getInt();
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
		
		if (!client.getFloodProtectors().getTransaction().tryPerformAction("HennaRemove"))
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Henna henna;
		boolean found = false;
		for (int i = 1; i <= 3; i++)
		{
			henna = activeChar.getHenna(i);
			if ((henna != null) && (henna.getDyeId() == _symbolId))
			{
				if (activeChar.getAdena() >= henna.getCancelFee())
				{
					activeChar.removeHenna(i);
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					client.sendPacket(ActionFailed.STATIC_PACKET);
				}
				found = true;
				break;
			}
		}
		// TODO: Test.
		if (!found)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Player " + activeChar + " requested Henna Draw remove without any henna.");
			client.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}
