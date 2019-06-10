package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface DailyMissionDAO extends DAO {

    @Query("DELETE FROM character_daily_missions WHERE mission_id = :id:")
    void deleteById(int id);
}
