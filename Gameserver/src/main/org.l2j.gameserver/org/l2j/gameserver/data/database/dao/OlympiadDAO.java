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
import org.l2j.gameserver.data.database.data.OlympiadRankData;

import java.util.Collection;
import java.util.List;

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

    @Query("SELECT * FROM olympiad_rankers LIMIT 100")
    List<OlympiadRankData> findRankers();

    @Query("""
            SELECT olympiad_rankers.*
            FROM olympiad_rankers, ( SELECT @base_rank := CONVERT( (SELECT olympiad_rankers.`rank` FROM olympiad_rankers WHERE player_id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND olympiad_rankers.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<OlympiadRankData> findRankersNextToPlayer(int playerId);

    @Query("SELECT * FROM olympiad_rankers_class WHERE class_id = :classId: AND (:server: = 0 OR server = :server:) LIMIT 50")
    List<OlympiadRankData> findRankersByClass(int classId, int server);

    @Query("""
            SELECT rc.*
            FROM olympiad_rankers_class rc, ( SELECT @base_rank := CONVERT( (SELECT olympiad_rankers_class.`rank` FROM olympiad_rankers_class WHERE player_id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND  rc.class_id = :classId:  AND rc.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<OlympiadRankData> findRankersNextToPlayerByClass(int playerId, int classId);

    @Query("""
            SELECT rc.*
            FROM olympiad_rankers_class_snapshot rc, ( SELECT @base_rank := CONVERT( (SELECT olympiad_rankers_class_snapshot.`rank` FROM olympiad_rankers_class_snapshot WHERE player_id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND  rc.class_id = :classId:  AND rc.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<OlympiadRankData> findPreviousRankersNextToPlayerByClass(int playerId, int classId);

    @Query("SELECT * FROM olympiad_rankers_class_snapshot WHERE class_id = :classId: AND (:server: = 0 OR server = :server: ) LIMIT 50")
    List<OlympiadRankData> findPreviousRankersByClass(int classId, int server);

    @Query("SELECT * FROM olympiad_rankers_snapshot LIMIT 100")
    List<OlympiadRankData> findPreviousRankers();

    @Query("""
            SELECT olympiad_rankers_snapshot.*
            FROM olympiad_rankers_snapshot, ( SELECT @base_rank := CONVERT( (SELECT olympiad_rankers_snapshot.`rank` FROM olympiad_rankers_snapshot WHERE player_id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND olympiad_rankers_snapshot.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<OlympiadRankData> findPreviousRankersNextToPlayer(int playerId);

    @Query("SELECT COUNT(1) FROM olympiad_participants")
    int countParticipants();

    @Query("SELECT COUNT(1) FROM olympiad_rankers_snapshot")
    int countPreviousParticipants();

    @Query("SELECT * FROM olympiad_rankers WHERE player_id = :playerId: AND  server = :server:")
    OlympiadRankData findRankData(int playerId, int server);

    @Query("SELECT * FROM olympiad_rankers_snapshot WHERE player_id = :playerId: AND  server = :server:")
    OlympiadRankData findPreviousRankData(int playerId, int server);
}
