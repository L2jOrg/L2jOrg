/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.OlympiadData;
import org.l2j.gameserver.data.database.data.OlympiadParticipantData;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public interface OlympiadDAO extends DAO<OlympiadData> {

    @Query("SELECT * FROM olympiad_data ORDER BY current_cycle DESC LIMIT 1")
    OlympiadData findData();

    @Query("SELECT * FROM olympiad_participants WHERE player_id = :playerId: AND server = :server:")
    OlympiadParticipantData findParticipantData(int playerId, int server);

    void save(OlympiadParticipantData data);

    @Query("UPDATE olympiad_participants SET points = :points:, battles_won = :battlesWon: WHERE player_id = :playerId: AND server = :server:")
    void updateVictory(int playerId, int server, short points, int battlesWon);

    @Query("UPDATE olympiad_participants SET points = :points:, battles_lost = :battlesLost: WHERE player_id = :playerId: AND server = :server:")
    void updateDefeat(int playerId, int server, short points, short battlesLost);

    void save(Collection<OlympiadParticipantData> data);
}
