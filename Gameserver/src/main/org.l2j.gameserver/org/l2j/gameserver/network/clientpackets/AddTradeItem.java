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

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.TradeUpdate;
import org.l2j.gameserver.network.serverpackets.trade.TradeOtherAdd;
import org.l2j.gameserver.network.serverpackets.trade.TradeOwnAdd;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class AddTradeItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddTradeItem.class);
    private int tradeId;
    private int objectId;
    private long count;

    @Override
    public void readImpl() {
        tradeId = readInt();
        objectId = readInt();
        count = readLong();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        final TradeList trade = player.getActiveTradeList();
        if (isNull(trade)) {
            LOGGER.warn("Character: {} requested item: {}  add without active trade: {}", player, objectId, tradeId);
            return;
        }

        final Player partner = trade.getPartner();
        if (isNull(partner) || isNull(World.getInstance().findPlayer(partner.getObjectId())) || isNull(partner.getActiveTradeList())) {
            // Trade partner not found, cancel trade
            if (nonNull(partner)) {
                LOGGER.warn("Character: {} requested invalid trade object: {}", player,  objectId);
            }
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            player.cancelActiveTrade();
            return;
        }

        if (trade.isConfirmed() || partner.getActiveTradeList().isConfirmed()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED);
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            player.cancelActiveTrade();
            return;
        }

        if (!player.validateItemManipulation(objectId, "trade")) {
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }

        final Item item1 = player.getInventory().getItemByObjectId(objectId);
        final TradeItem item2 = trade.addItem(objectId, count);
        if (item2 != null) {
            player.sendPacket(new TradeOwnAdd(1, item2));
            player.sendPacket(new TradeOwnAdd(2, item2));
            player.sendPacket(new TradeUpdate(1, null, null, 0));
            player.sendPacket(new TradeUpdate(2, player, item2, item1.getCount() - item2.getCount()));
            partner.sendPacket(new TradeOtherAdd(1, item2));
            partner.sendPacket(new TradeOtherAdd(2, item2));
        }
    }
}
