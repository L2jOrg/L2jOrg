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
package org.l2j.gameserver.model.clanhallauction;

import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.dao.ClanHallDAO;
import org.l2j.gameserver.data.database.data.Bidder;
import org.l2j.gameserver.engine.clan.clanhall.ClanHall;
import org.l2j.gameserver.engine.clan.clanhall.ClanHallEngine;
import org.l2j.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.item.CommonItem;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ClanHallAuction {
    private final int clanHallId;
    private final IntMap<Bidder> bidders;

    public ClanHallAuction(int clanHallId) {
        this.clanHallId = clanHallId;
        bidders = getDAO(ClanHallDAO.class).loadBidders(clanHallId);
    }

    public IntMap<Bidder> getBids() {
        return bidders;
    }

    public void addBid(Clan clan, long bid) {
        addBid(clan, bid, System.currentTimeMillis());
    }

    public void addBid(Clan clan, long bid, long bidTime) {
        Bidder bidder = Bidder.of(clanHallId, clan, bid, bidTime);
        bidders.put(clan.getId(), bidder);
        getDAO(ClanHallDAO.class).save(bidder);
    }

    public void removeBid(Clan clan) {
        bidders.remove(clan.getId());
        getDAO(ClanHallDAO.class).deleteBidder(clan.getId());
    }

    public long getHighestBid() {
        final ClanHall clanHall = ClanHallEngine.getInstance().getClanHallById(clanHallId);
        return bidders.values().stream().mapToLong(Bidder::getBid).max().orElse(clanHall.getMinBid());
    }

    public long getClanBid(Clan clan) {
        return Util.zeroIfNullOrElseLong(bidders.get(clan.getId()), Bidder::getBid);
    }

    public Optional<Bidder> getHighestBidder() {
        return bidders.values().stream().max(Comparator.comparingLong(Bidder::getBid));
    }

    public int getBidCount() {
        return bidders.size();
    }

    public void returnAdenas(Bidder bidder) {
        bidder.getClan().getWarehouse().addItem("Clan Hall Auction Outbid", CommonItem.ADENA, bidder.getBid(), null, null);
    }

    public void finalizeAuctions() {
        final Optional<Bidder> potentialHighestBidder = getHighestBidder();

        if (potentialHighestBidder.isPresent()) {
            final Bidder highestBidder = potentialHighestBidder.get();
            final ClanHall clanHall = ClanHallEngine.getInstance().getClanHallById(clanHallId);
            clanHall.setOwner(highestBidder.getClan());
            bidders.clear();

            getDAO(ClanHallDAO.class).deleteBidders(clanHallId);
        }
    }

    public int getClanHallId() {
        return clanHallId;
    }

    public long getRemaingTime() {
        return ClanHallAuctionManager.getInstance().getScheduler("endAuction").getRemainingTime(TimeUnit.MILLISECONDS);
    }
}
