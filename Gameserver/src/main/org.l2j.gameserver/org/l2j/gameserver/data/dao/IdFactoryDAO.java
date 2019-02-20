package org.l2j.gameserver.data.dao;

import io.github.joealisson.primitive.sets.IntSet;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface IdFactoryDAO extends DAO {

    @Query("SELECT obj_id AS id FROM characters UNION SELECT object_id AS id FROM items UNION SELECT clan_id AS id FROM clan_data " +
        "UNION SELECT ally_id AS id FROM ally_data UNION SELECT objId AS id FROM pets UNION SELECT id FROM couples")
    IntSet findUsedObjectIds();
}
