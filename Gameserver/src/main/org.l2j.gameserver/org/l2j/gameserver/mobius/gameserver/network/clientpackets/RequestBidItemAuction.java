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
import org.l2j.gameserver.mobius.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.itemauction.ItemAuction;
import org.l2j.gameserver.mobius.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * @author Forsaiken
 */
public final class RequestBidItemAuction extends IClientIncomingPacket
{
	private int _instanceId;
	private long _bid;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_instanceId = packet.getInt();
		_bid = packet.getLong();
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
		
		// can't use auction fp here
		if (!client.getFloodProtectors().getTransaction().tryPerformAction("auction"))
		{
			activeChar.sendMessage("You are bidding too fast.");
			return;
		}
		
		if ((_bid < 0) || (_bid > Inventory.MAX_ADENA))
		{
			return;
		}
		
		final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
		if (instance != null)
		{
			final ItemAuction auction = instance.getCurrentAuction();
			if (auction != null)
			{
				auction.registerBid(activeChar, _bid);
			}
		}
	}
}