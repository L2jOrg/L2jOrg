/*
 * Copyright (C) 2004-2016 L2J Unity
 * 
 * This file is part of L2J Unity.
 * 
 * L2J Unity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Unity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.compound;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.data.xml.impl.CombinationItemsData;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.request.CompoundRequest;
import com.l2jmobius.gameserver.model.items.combination.CombinationItem;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import com.l2jmobius.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;

/**
 * @author Sdw
 */
public class RequestNewEnchantRetryToPutItems implements IClientIncomingPacket
{
	private int _firstItemObjectId;
	private int _secondItemObjectId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_firstItemObjectId = packet.readD();
		_secondItemObjectId = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		else if (activeChar.isInStoreMode())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		
		final CompoundRequest request = new CompoundRequest(activeChar);
		if (!activeChar.addRequest(request))
		{
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			return;
		}
		
		// Make sure player owns first item.
		request.setItemOne(_firstItemObjectId);
		final L2ItemInstance itemOne = request.getItemOne();
		if (itemOne == null)
		{
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			activeChar.removeRequest(request.getClass());
			return;
		}
		
		// Make sure player owns second item.
		request.setItemTwo(_secondItemObjectId);
		final L2ItemInstance itemTwo = request.getItemTwo();
		if (itemTwo == null)
		{
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			activeChar.removeRequest(request.getClass());
			return;
		}
		
		final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());
		
		// Not implemented or not able to merge!
		if (combinationItem == null)
		{
			client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
			activeChar.removeRequest(request.getClass());
			return;
		}
		client.sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
	}
}
