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

import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.auction.ItemAuction;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.model.item.container.Inventory;

/**
 * @author Forsaiken
 */
public final class RequestBidItemAuction extends ClientPacket {
    private int _instanceId;
    private long _bid;

    @Override
    public void readImpl() {
        _instanceId = readInt();
        _bid = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        // can't use auction fp here
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("auction")) {
            activeChar.sendMessage("You are bidding too fast.");
            return;
        }

        if ((_bid < 0) || (_bid > Inventory.MAX_ADENA)) {
            return;
        }

        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
        if (instance != null) {
            final ItemAuction auction = instance.getCurrentAuction();
            if (auction != null) {
                auction.registerBid(activeChar, _bid);
            }
        }
    }
}