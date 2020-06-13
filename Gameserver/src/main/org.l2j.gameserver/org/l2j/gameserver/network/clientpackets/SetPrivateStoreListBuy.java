/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Arrays;

public final class SetPrivateStoreListBuy extends ClientPacket {
    private TradeItem[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int count = readInt();
        if ((count < 1) || (count > Config.MAX_ITEM_IN_PACKET)) {
            throw new InvalidDataPacketException();
        }

        _items = new TradeItem[count];
        for (int i = 0; i < count; i++) {
            int itemId = readInt();

            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
            if (template == null) {
                _items = null;
                throw new InvalidDataPacketException();
            }

            final int enchantLevel = readShort();
            readShort(); // TODO analyse this

            long cnt = readLong();
            long price = readLong();

            if ((itemId < 1) || (cnt < 1) || (price < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }

            final int option1 = readInt();
            final int option2 = readInt();
            final short attackAttributeId = readShort();
            final int attackAttributeValue = readShort();
            final int defenceFire = readShort();
            final int defenceWater = readShort();
            final int defenceWind = readShort();
            final int defenceEarth = readShort();
            final int defenceHoly = readShort();
            final int defenceDark = readShort();
            readInt(); // Visual ID is not used on Classic

            final EnsoulOption[] soulCrystalOptions = new EnsoulOption[readByte()];
            for (int k = 0; k < soulCrystalOptions.length; k++) {
                soulCrystalOptions[k] = EnsoulData.getInstance().getOption(readInt());
            }
            final EnsoulOption[] soulCrystalSpecialOptions = new EnsoulOption[readByte()];
            for (int k = 0; k < soulCrystalSpecialOptions.length; k++) {
                soulCrystalSpecialOptions[k] = EnsoulData.getInstance().getOption(readInt());
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
            item.setSoulCrystalOptions(Arrays.asList(soulCrystalOptions));
            item.setSoulCrystalSpecialOptions(Arrays.asList(soulCrystalSpecialOptions));
            _items[i] = item;
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (_items == null) {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel()) {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isInsideZone(ZoneType.NO_STORE)) {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        TradeList tradeList = player.getBuyList();
        tradeList.clear();

        // Check maximum number of allowed slots for pvt shops
        if (_items.length > player.getPrivateBuyStoreLimit()) {
            player.sendPacket(new PrivateStoreManageListBuy(1, player));
            player.sendPacket(new PrivateStoreManageListBuy(2, player));
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        long totalCost = 0;
        for (TradeItem i : _items) {
            if ((Inventory.MAX_ADENA / i.getCount()) < i.getPrice()) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price more than " + Inventory.MAX_ADENA + " adena in Private Store - Buy.");
                return;
            }

            tradeList.addItemByItemId(i.getItem().getId(), i.getCount(), i.getPrice());

            totalCost += (i.getCount() * i.getPrice());
            if (totalCost > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set total price more than " + Inventory.MAX_ADENA + " adena in Private Store - Buy.");
                return;
            }
        }

        // Check for available funds
        if (totalCost > player.getAdena()) {
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
