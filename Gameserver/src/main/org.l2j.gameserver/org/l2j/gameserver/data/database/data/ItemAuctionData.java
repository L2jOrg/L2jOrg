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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.item.auction.ItemAuctionState;

/**
 * @author JoeAlisson
 */
@Table("item_auction")
public class ItemAuctionData {

    private int auction;
    private int instance;

    @Column("auction_item")
    private int auctionItem;

    @Column("starting_time")
    private long startingTime;

    @Column("ending_time")
    private long endingTime;

    @Column("auction_state")
    private ItemAuctionState auctionState;

    public int getAuction() {
        return auction;
    }

    public int getInstance() {
        return instance;
    }

    public int getAuctionItem() {
        return auctionItem;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public long getEndingTime() {
        return endingTime;
    }

    public void updateEndingTime(long time) {
        endingTime += time;
    }

    public ItemAuctionState getAuctionState() {
        return auctionState;
    }

    public void setAuctionState(ItemAuctionState auctionState) {
        this.auctionState = auctionState;
    }

    public static ItemAuctionData of(int auctionId, int instanceId, int auctionItemId, long startingTime, long endingTime, ItemAuctionState auctionState) {
        var data = new ItemAuctionData();
        data.auction = auctionId;
        data.instance = instanceId;
        data.auctionItem = auctionItemId;
        data.startingTime = startingTime;
        data.endingTime = endingTime;
        data.auctionState = auctionState;
        return data;
    }
}
