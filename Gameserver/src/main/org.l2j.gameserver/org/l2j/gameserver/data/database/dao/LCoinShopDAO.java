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
import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface LCoinShopDAO extends DAO<Object> {

    @Query("""
            INSERT INTO lcoin_shop_history (product_id, account, count, restriction_type)
            VALUES (:id:, :account:, :count:, :restrictionPeriod: )
            ON DUPLICATE KEY UPDATE count=count + :count:""")
    void saveHistory(String account, int id, int count, RestrictionPeriod restrictionPeriod);

    @Query("""
            DELETE FROM lcoin_shop_history 
            WHERE (restriction_type = 'DAY' AND sell_date < CURRENT_DATE)
              OR (restriction_type = 'MONTH' AND sell_date < CURRENT_DATE - INTERVAL 30 DAY)""")
    void deleteExpired();

    @Query("""
           SELECT product_id, `account`,  SUM(`count`) AS `sum` FROM lcoin_shop_history
           GROUP BY product_id, `account`;
           """)
    void loadAllGrouped(Consumer<ResultSet> result);

}
