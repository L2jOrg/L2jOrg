package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface FortDAO extends DAO<Object> {

    @Query("UPDATE fort SET owner=0 WHERE owner NOT IN (SELECT clan_id FROM clan_data)")
    void resetWithoutOwner();
}
