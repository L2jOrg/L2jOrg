package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface ForumDAO extends DAO<Object> {

    @Query("DELETE FROM forums WHERE (forum_parent=2 AND forum_owner_id NOT IN (SELECT clan_id FROM clan_data)) OR (forum_parent=3 AND forum_owner_id NOT IN (SELECT charId FROM characters));")
    int deleteWithoutOwner();
}
