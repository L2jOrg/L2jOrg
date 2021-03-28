/*
 * Copyright © 2019 L2J Mobius
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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;

/**
 * @author Forsaiken
 */
@Table("item_auction_bid")
public final class ItemAuctionBid {
    @Column("auction")
    private int auction;
    @Column("player_id")
    private int playerId;
    private long bid;

    public ItemAuctionBid(int auction, int playerObjId, long lastBid) {
        this.auction = auction;
        playerId = playerObjId;
        bid = lastBid;
    }

    public final int getPlayerObjId() {
        return playerId;
    }

    public final long getLastBid() {
        return bid;
    }

    public final void setLastBid(long lastBid) {
        bid = lastBid;
    }

    public final void cancelBid() {
        bid = -1;
    }

    public final boolean isCanceled() {
        return bid <= 0;
    }

    public final Player getPlayer() {
        return World.getInstance().findPlayer(playerId);
    }
}