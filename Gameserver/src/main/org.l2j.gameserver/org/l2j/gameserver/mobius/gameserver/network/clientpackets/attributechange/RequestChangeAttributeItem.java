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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.attributechange;

import com.l2jmobius.Config;
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.enums.AttributeType;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.itemcontainer.PcInventory;
import com.l2jmobius.gameserver.model.items.enchant.attribute.AttributeHolder;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import com.l2jmobius.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;
import com.l2jmobius.gameserver.network.serverpackets.attributechange.ExChangeAttributeOk;
import com.l2jmobius.gameserver.util.Util;

/**
 * @author Mobius
 */
public class RequestChangeAttributeItem implements IClientIncomingPacket
{
	private int _consumeItemId;
	private int _itemObjId;
	private int _newElementId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_consumeItemId = packet.readD();
		_itemObjId = packet.readD();
		_newElementId = packet.readD();
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
		
		final PcInventory inventory = activeChar.getInventory();
		final L2ItemInstance item = inventory.getItemByObjectId(_itemObjId);
		
		// attempting to destroy item
		if (activeChar.getInventory().destroyItemByItemId("ChangeAttribute", _consumeItemId, 1, activeChar, item) == null)
		{
			client.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			client.sendPacket(ExChangeAttributeFail.STATIC);
			Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to change attribute without an attribute change crystal.", Config.DEFAULT_PUNISH);
			return;
		}
		
		// get values
		final int oldElementId = item.getAttackAttributeType().getClientId();
		final int elementValue = item.getAttackAttribute().getValue();
		item.clearAllAttributes();
		item.setAttribute(new AttributeHolder(AttributeType.findByClientId(_newElementId), elementValue), true);
		
		// send packets
		final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
		msg.addItemName(item);
		msg.addAttribute(oldElementId);
		msg.addAttribute(_newElementId);
		activeChar.sendPacket(msg);
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		for (L2ItemInstance i : activeChar.getInventory().getItemsByItemId(_consumeItemId))
		{
			iu.addItem(i);
		}
		activeChar.sendPacket(iu);
		activeChar.broadcastUserInfo();
		activeChar.sendPacket(ExChangeAttributeOk.STATIC);
	}
}
