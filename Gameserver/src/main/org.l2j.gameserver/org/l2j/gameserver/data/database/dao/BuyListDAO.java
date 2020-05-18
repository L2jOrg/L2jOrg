package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.BuyListInfo;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface BuyListDAO extends DAO<BuyListInfo> {

    @Query("SELECT * FROM buylists")
    List<BuyListInfo> findAll();
}
