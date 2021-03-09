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

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface L2StoreDAO extends DAO<Object> {

    @Query("""
            SELECT SUM(count) FROM l2store_history 
            WHERE product_id = :productId: AND account=:account: AND sell_date = CURRENT_DATE""")
    int countBoughtProductToday(String account, int productId);

    @Query("""
            INSERT INTO l2store_history ( product_id, count, account) 
            VALUES ( :productId:, :count:, :account:) 
            ON DUPLICATE KEY UPDATE count=count + :count:""")
    void addHistory(int productId, int count, String account);

    @Query("""
            SELECT EXISTS ( 
            SELECT 1 FROM l2store_history 
            WHERE account=:account: AND sell_date = CURRENT_DATE AND product_id BETWEEN :minProductId: AND :maxProductId: )""")
    boolean hasBoughtAnyProductInRangeToday(String account, int minProductId, int maxProductId);

    @Query("SELECT SUM(count) FROM l2store_history WHERE account = :account: AND product_id = :productId:")
    int countBoughtProduct(String account, int productId);

    @Query("SELECT SUM(count) FROM l2store_history WHERE account=:account: AND product_id = :productId: AND CURRENT_DATE - INTERVAL :days: DAY <= sell_date")
    int countBoughtProductInDays(String account, int productId, int days);
}
