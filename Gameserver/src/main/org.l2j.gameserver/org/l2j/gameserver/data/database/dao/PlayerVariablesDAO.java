package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.PlayerVariableData;

/**
 * @author JoeAlisson
 */
public interface PlayerVariablesDAO extends DAO<PlayerVariableData> {

    @Query("SELECT * FROM player_variables WHERE player_id = :playerId:")
    PlayerVariableData findById(int playerId);

    @Query("UPDATE player_variables SET revenge_locations = 5, revenge_teleports = 5")
    void resetRevengeData();
}
