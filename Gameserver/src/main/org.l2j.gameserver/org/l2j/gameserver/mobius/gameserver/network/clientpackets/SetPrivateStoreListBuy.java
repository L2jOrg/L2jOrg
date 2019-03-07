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
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.data.xml.impl.EnsoulData;
import com.l2jmobius.gameserver.datatables.ItemTable;
import com.l2jmobius.gameserver.enums.AttributeType;
import com.l2jmobius.gameserver.enums.PrivateStoreType;
import com.l2jmobius.gameserver.model.TradeItem;
import com.l2jmobius.gameserver.model.TradeList;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.ensoul.EnsoulOption;
import com.l2jmobius.gameserver.model.items.L2Item;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import com.l2jmobius.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import com.l2jmobius.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import com.l2jmobius.gameserver.taskmanager.AttackStanceTaskManager;
import com.l2jmobius.gameserver.util.Util;

import java.util.Arrays;

import static com.l2jmobius.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

public final class SetPrivateStoreListBuy implements IClientIncomingPacket
{
	private TradeItem[] _items = null;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		final int count = packet.readD();
		if ((count < 1) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			return false;
		}
		
		_items = new TradeItem[count];
		for (int i = 0; i < count; i++)
		{
			int itemId = packet.readD();
			
			final L2Item template = ItemTable.getInstance().getTemplate(itemId);
			if (template == null)
			{
				_items = null;
				return false;
			}
			
			final int enchantLevel = packet.readH();
			packet.readH(); // TODO analyse this
			
			long cnt = packet.readQ();
			long price = packet.readQ();
			
			if ((itemId < 1) || (cnt < 1) || (price < 0))
			{
				_items = null;
				return false;
			}
			
			final int option1 = packet.readD();
			final int option2 = packet.readD();
			final short attackAttributeId = (short) packet.readH();
			final int attackAttributeValue = packet.readH();
			final int defenceFire = packet.readH();
			final int defenceWater = packet.readH();
			final int defenceWind = packet.readH();
			final int defenceEarth = packet.readH();
			final int defenceHoly = packet.readH();
			final int defenceDark = packet.readH();
			final int visualId = packet.readD();
			
			final EnsoulOption[] soulCrystalOptions = new EnsoulOption[packet.readC()];
			for (int k = 0; k < soulCrystalOptions.length; k++)
			{
				soulCrystalOptions[k] = EnsoulData.getInstance().getOption(packet.readD());
			}
			final EnsoulOption[] soulCrystalSpecialOptions = new EnsoulOption[packet.readC()];
			for (int k = 0; k < soulCrystalSpecialOptions.length; k++)
			{
				soulCrystalSpecialOptions[k] = EnsoulData.getInstance().getOption(packet.readD());
			}
			
			final TradeItem item = new TradeItem(template, cnt, price);
			item.setEnchant(enchantLevel);
			item.setAugmentation(option1, option2);
			item.setAttackElementType(AttributeType.findByClientId(attackAttributeId));
			item.setAttackElementPower(attackAttributeValue);
			item.setElementDefAttr(AttributeType.FIRE, defenceFire);
			item.setElementDefAttr(AttributeType.WATER, defenceWater);
			item.setElementDefAttr(AttributeType.WIND, defenceWind);
			item.setElementDefAttr(AttributeType.EARTH, defenceEarth);
			item.setElementDefAttr(AttributeType.HOLY, defenceHoly);
			item.setElementDefAttr(AttributeType.DARK, defenceDark);
			item.setVisualId(visualId);
			item.setSoulCrystalOptions(Arrays.asList(soulCrystalOptions));
			item.setSoulCrystalSpecialOptions(Arrays.asList(soulCrystalSpecialOptions));
			_items[i] = item;
		}
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (_items == null)
		{
			player.setPrivateStoreType(PrivateStoreType.NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(new PrivateStoreManageListBuy(1, player));
			player.sendPacket(new PrivateStoreManageListBuy(2, player));
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			player.sendPacket(new PrivateStoreManageListBuy(1, player));
			player.sendPacket(new PrivateStoreManageListBuy(2, player));
			player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		TradeList tradeList = player.getBuyList();
		tradeList.clear();
		
		// Check maximum number of allowed slots for pvt shops
		if (_items.length > player.getPrivateBuyStoreLimit())
		{
			player.sendPacket(new PrivateStoreManageListBuy(1, player));
			player.sendPacket(new PrivateStoreManageListBuy(2, player));
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		
		long totalCost = 0;
		for (TradeItem i : _items)
		{
			if ((MAX_ADENA / i.getCount()) < i.getPrice())
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price more than " + MAX_ADENA + " adena in Private Store - Buy.", Config.DEFAULT_PUNISH);
				return;
			}
			
			tradeList.addItemByItemId(i.getItem().getId(), i.getCount(), i.getPrice());
			
			totalCost += (i.getCount() * i.getPrice());
			if (totalCost > MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set total price more than " + MAX_ADENA + " adena in Private Store - Buy.", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		// Check for available funds
		if (totalCost > player.getAdena())
		{
			player.sendPacket(new PrivateStoreManageListBuy(1, player));
			player.sendPacket(new PrivateStoreManageListBuy(2, player));
			player.sendPacket(SystemMessageId.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
			return;
		}
		
		player.sitDown();
		player.setPrivateStoreType(PrivateStoreType.BUY);
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgBuy(player));
	}
}
