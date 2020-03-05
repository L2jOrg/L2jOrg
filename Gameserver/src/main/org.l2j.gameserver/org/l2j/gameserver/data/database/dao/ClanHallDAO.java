package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

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
}
