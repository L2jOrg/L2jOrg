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

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.ItemRequest;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.TradeList.TradeResult;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public final class RequestPrivateStoreBuy extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPrivateStoreBuy.class);
    private static final int BATCH_LENGTH = 20; // length of the one item

    private int _storePlayerId;
    private Set<ItemRequest> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _storePlayerId = readInt();
        final int count = readInt();
        if (count <= 0 || count > CharacterSettings.maxItemInPacket() || count * BATCH_LENGTH != available()) {
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
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("privatestorebuy")) {
            return;
        }

        final var player = client.getPlayer();
        final var storePlayer = World.getInstance().findPlayer(_storePlayerId);

        if (!canBuy(player, storePlayer)) {
            return;
        }

        final TradeList storeList = storePlayer.getSellList();
        if (storeList == null) {
            return;
        }

        if (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL) {
            if (storeList.getItemCount() > _items.size()) {
                final String msgErr = "[RequestPrivateStoreBuy] player " + client.getPlayer().getName() + " tried to buy less items than sold by package-sell, ban this player for bot usage!";
                GameUtils.handleIllegalPlayerAction(client.getPlayer(), msgErr);
                return;
            }
        }

        var result = storeList.privateStoreBuy(player, _items);
        if (result != TradeResult.OK) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            if (result != TradeResult.CANCELED) {
                LOGGER.warn("PrivateStore buy has failed due to invalid list or request. {}, Private store of: {} ", player, storePlayer);
            }
            return;
        }

        if (storeList.getItemCount() == 0) {
            storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
            storePlayer.broadcastUserInfo();
        }
    }

    private boolean canBuy(Player player, Player storePlayer) {
        if (player == null || storePlayer == null) {
            return false;
        }

        if (player.isOnEvent()) // custom event message
        {
            player.sendMessage("You cannot open a private store while participating in an event.");
            return false;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return false;
        }

        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return false;
        }

        if(!(storePlayer.getPrivateStoreType() == PrivateStoreType.SELL || storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)) {
            return false;
        }
        return isInsideRadius3D(player, storePlayer, Npc.INTERACTION_DISTANCE);
    }
}
