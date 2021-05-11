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
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.model.Clan;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
@Table("clanhall_auctions_bidders")
public class Bidder {
    private long bid;
    private long bidTime;
    @Column("clanId")
    private int clanId;
    @Column("clanHallId")
    private int clanHallId;

    @NonUpdatable
    private Clan clan;

    public Clan getClan() {
        if(isNull(clan)) {
            clan = ClanEngine.getInstance().getClan(clanId);
        }
        return clan;
    }

    public String getClanName() {
        return getClan().getName();
    }

    public long getBid() {
        return bid;
    }

    public long getBidTime() {
        return bidTime;
    }

    public static Bidder of(int clanHallId, Clan clan, long bid, long bidTime) {
        var bidder = new Bidder();
        bidder.clan = clan;
        bidder.clanHallId = clanHallId;
        bidder.clanId = clan.getId();
        bidder.bid = bid;
        bidder.bidTime = bidTime;
        return bidder;
    }
}
