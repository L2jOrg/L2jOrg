package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface PrimeShopDAO extends DAO {

    @Query("SELECT SUM(count) FROM shop_history WHERE product_id = :productId: AND bidder=:playerObjectId: AND sell_date = CURRENT_DATE")
    int countBougthItemToday(int playerObjectId, int productId);

    @Query("INSERT INTO shop_history ( product_id, count, bidder ) VALUES ( :productId:, :count:, :playerObjectId:)")
    void addHistory(int productId, int count, int playerObjectId);
}
