/*
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.ShopPreviewInfo;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;

import java.util.EnumMap;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * @author Gnacik
 * @author JoeAlisson
 */
public final class RequestPreviewItem extends ClientPacket {
    private int listId;
    private int count;
    private int[] items;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        readInt(); // unk
        listId = readInt();

        if(listId >= 4000000) {
            throw new InvalidDataPacketException();
        }

        count = readInt();

        if (count <= 0 || count > 100) {
            throw new InvalidDataPacketException();
        }

        items = new int[count];

        for (int i = 0; i < count; i++) {
            items[i] = readInt();
        }
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            return;
        }

        var player = client.getPlayer();

        if (player.getReputation() < 0 && !CharacterSettings.canPkShop()) {
            return;
        }

        var target = player.getLastFolkNPC();
        if( !(target instanceof Merchant) || !isInsideRadius2D(player, target, Npc.INTERACTION_DISTANCE) ) {
            return;
        }

        var buyList = BuyListData.getInstance().getBuyList(listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(player, "Warning!! " + player + " sent a false BuyList list_id " + listId);
            return;
        }

        long totalPrice = 0;
        final EnumMap<InventorySlot, Integer> slotItems = new EnumMap<>(InventorySlot.class);

        for (int i = 0; i < count; i++) {
            final int itemId = this.items[i];

            var product = buyList.getProductByItemId(itemId);
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!!" + player + " sent a false BuyList list_id " + listId + " and item_id " + itemId);
                return;
            }

            var slot = product.getBodyPart().slot();
            if (isNull(slot)) {
                continue;
            }

            if (slotItems.containsKey(slot)) {
                player.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                return;
            }

            slotItems.put(slot, itemId);
            totalPrice += GeneralSettings.wearPrice();
            if (totalPrice > CharacterSettings.maxAdena()) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! " + player + " tried to purchase over " + CharacterSettings.maxAdena() + " adena worth of goods.");
                return;
            }
        }

        if (totalPrice < 0 || !player.reduceAdena("Wear", totalPrice, target, true)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }

        if (!slotItems.isEmpty()) {
            player.sendPacket(new ShopPreviewInfo(slotItems));
            ThreadPool.schedule(new RemoveWearItemsTask(player), GeneralSettings.wearDelay());
        }
    }

    private static class RemoveWearItemsTask implements Runnable {
        private final Player player;

        protected RemoveWearItemsTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT);
            player.sendPacket(new ExUserInfoEquipSlot(player));
        }
    }

}
