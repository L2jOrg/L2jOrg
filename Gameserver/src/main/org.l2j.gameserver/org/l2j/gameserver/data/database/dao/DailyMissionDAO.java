package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;

public interface DailyMissionDAO extends DAO<DailyMissionPlayerData> {

    @Query("DELETE FROM character_daily_missions WHERE mission_id = :id:")
    void deleteById(int id);

    @Query("SELECT * FROM character_daily_missions WHERE char_id = :playerId: AND mission_id = :missionId:")
    DailyMissionPlayerData findById(int playerId, int missionId);
}
