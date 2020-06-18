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
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;


/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPrivateStoreBuy extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPrivateStoreBuy.class);
    private static final int BATCH_LENGTH = 20; // length of the one item

    private int _storePlayerId;
    private Set<ItemRequest> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _storePlayerId = readInt();
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }
        _items = new HashSet<>();

        for (int i = 0; i < count; i++) {
            final int objectId = readInt();
            final long cnt = readLong();
            final long price = readLong();

            if ((objectId < 1) || (cnt < 1) || (price < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }

            _items.add(new ItemRequest(objectId, cnt, price));
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

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("privatestorebuy")) {
            player.sendMessage("You are buying items too fast.");
            return;
        }

        final WorldObject object = World.getInstance().findPlayer(_storePlayerId);
        if (object == null) {
            return;
        }

        final Player storePlayer = (Player) object;
        if (!isInsideRadius3D(player, storePlayer, Npc.INTERACTION_DISTANCE)) {
            return;
        }

        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return;
        }

        if (!((storePlayer.getPrivateStoreType() == PrivateStoreType.SELL) || (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL))) {
            return;
        }

        final TradeList storeList = storePlayer.getSellList();
        if (storeList == null) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL) {
            if (storeList.getItemCount() > _items.size()) {
                final String msgErr = "[RequestPrivateStoreBuy] player " + client.getPlayer().getName() + " tried to buy less items than sold by package-sell, ban this player for bot usage!";
                GameUtils.handleIllegalPlayerAction(client.getPlayer(), msgErr);
                return;
            }
        }

        final int result = storeList.privateStoreBuy(player, _items);
        if (result > 0) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            if (result > 1) {
                LOGGER.warn("PrivateStore buy has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
            }
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
