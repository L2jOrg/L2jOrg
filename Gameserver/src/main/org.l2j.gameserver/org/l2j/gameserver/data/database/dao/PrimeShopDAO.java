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
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface PrimeShopDAO extends DAO<Object> {

    @Query("SELECT SUM(count) FROM shop_history WHERE product_id = :productId: AND bidder=:playerObjectId: AND sell_date = CURRENT_DATE")
    int countBougthItemToday(int playerObjectId, int productId);

    @Query("INSERT INTO shop_history ( product_id, count, bidder ) VALUES ( :productId:, :count:, :playerObjectId:)")
    void addHistory(int productId, int count, int playerObjectId);

    @Query("SELECT EXISTS ( SELECT 1 FROM shop_history WHERE bidder=:playerObjectId: AND sell_date = CURRENT_DATE AND product_id BETWEEN :minProductId: AND :maxProductId: )")
    boolean hasBougthAnyItemInRangeToday(int playerObjectId, int minProductId, int maxProductId);
}
