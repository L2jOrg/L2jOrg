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
package org.l2j.gameserver.data.database.dao;

import io.github.joealisson.primitive.ConcurrentIntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.Bidder;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface ClanHallDAO extends DAO<Object> {

    @Query("SELECT * FROM clanhall WHERE id=:id:")
    void findById(int id, Consumer<ResultSet> action);

    @Query("REPLACE INTO clanhall (id, owner_id, paid_until) VALUES (:id:,:owner:,:paidUntil:)")
    void save(int id, int owner, long paidUntil);

    @Query("DELETE FROM clanhall_auctions_bidders WHERE clanHallId=:id:")
    void deleteBidders(int id);

    @Query("DELETE FROM clanhall_auctions_bidders WHERE clanId=:id:")
    void deleteBidder(int id);

    @Query("SELECT clanId, clanHallId, bid, bidTime FROM clanhall_auctions_bidders WHERE clanHallId=:clanHallId:")
    ConcurrentIntMap<Bidder> loadBidders(int clanHallId);
}
