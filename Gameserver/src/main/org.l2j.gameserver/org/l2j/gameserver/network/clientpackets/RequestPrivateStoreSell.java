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
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.ItemRequest;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public final class RequestPrivateStoreSell extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPrivateStoreSell.class);
    private int _storePlayerId;
    private ItemRequest[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _storePlayerId = readInt();
        int itemsCount = readInt();
        if ((itemsCount <= 0) || (itemsCount > Config.MAX_ITEM_IN_PACKET)) {
            throw new InvalidDataPacketException();
        }
        _items = new ItemRequest[itemsCount];

        for (int i = 0; i < itemsCount; i++) {
            final int slot = readInt();
            final int itemId = readInt();
            readShort(); // TODO analyse this
            readShort(); // TODO analyse this
            final long count = readLong();
            final long price = readLong();
            readInt(); // visual id
            readInt(); // option 1
            readInt(); // option 2
            int soulCrystals = readByte();
            for (int s = 0; s < soulCrystals; s++) {
                readInt(); // soul crystal option
            }
            int soulCrystals2 = readByte();
            for (int s = 0; s < soulCrystals2; s++) {
                readInt(); // sa effect
            }
            if (/* (slot < 1) || */ (itemId < 1) || (count < 1) || (price < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items[i] = new ItemRequest(slot, itemId, count, price);
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (_items == null) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isOnEvent()) // custom event message
        {
            player.sendMessage("You cannot open a private store while participating in an event.");
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("privatestoresell")) {
            player.sendMessage("You are selling items too fast.");
            return;
        }

        final Player storePlayer = World.getInstance().findPlayer(_storePlayerId);
        if ((storePlayer == null) || !isInsideRadius3D(player, storePlayer, INTERACTION_DISTANCE)) {
            return;
        }

        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return;
        }

        if (storePlayer.getPrivateStoreType() != PrivateStoreType.BUY) {
            return;
        }

        final TradeList storeList = storePlayer.getBuyList();
        if (storeList == null) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!storeList.privateStoreSell(player, _items)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("PrivateStore sell has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
            return;
        }

        // Update offline trade record, if realtime saving is enabled
        if (Config.OFFLINE_TRADE_ENABLE && Config.STORE_OFFLINE_TRADE_IN_REALTIME && ((storePlayer.getClient() == null) || storePlayer.getClient().isDetached())) {
            OfflineTradersTable.onTransaction(storePlayer, storeList.getItemCount() == 0, false);
        }

        if (storeList.getItemCount() == 0) {
            storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
            storePlayer.broadcastUserInfo();
        }
    }
}
