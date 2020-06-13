/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.item.auction;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;

/**
 * @author Forsaiken
 */
public final class AuctionItem {
    private final int _auctionItemId;
    private final int _auctionLength;
    private final long _auctionInitBid;

    private final int _itemId;
    private final long _itemCount;
    @SuppressWarnings("unused")
    private final StatsSet _itemExtra;

    public AuctionItem(int auctionItemId, int auctionLength, long auctionInitBid, int itemId, long itemCount, StatsSet itemExtra) {
        _auctionItemId = auctionItemId;
        _auctionLength = auctionLength;
        _auctionInitBid = auctionInitBid;

        _itemId = itemId;
        _itemCount = itemCount;
        _itemExtra = itemExtra;
    }

    public final boolean checkItemExists() {
        return ItemEngine.getInstance().getTemplate(_itemId) != null;
    }

    public final int getAuctionItemId() {
        return _auctionItemId;
    }

    public final int getAuctionLength() {
        return _auctionLength;
    }

    public final long getAuctionInitBid() {
        return _auctionInitBid;
    }

    public final int getItemId() {
        return _itemId;
    }

    public final long getItemCount() {
        return _itemCount;
    }

    public final Item createNewItemInstance() {
        final Item item = new Item(IdFactory.getInstance().getNextId(), _itemId);
        World.getInstance().addObject(item);
        item.setCount(_itemCount);
        return item;
    }
}