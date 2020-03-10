package org.l2j.gameserver.data.database.dao;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

/**
 * @author JoeAlisson
 */
public interface IdFactoryDAO extends DAO<Object> {

    @Query("""
            SELECT charId AS id FROM characters
            UNION SELECT object_id AS id FROM items
            UNION SELECT clan_id AS id FROM clan_data
            UNION SELECT object_id AS id FROM itemsonground
            UNION SELECT messageId AS id FROM messages""")
    IntSet findUsedObjectIds();

}
