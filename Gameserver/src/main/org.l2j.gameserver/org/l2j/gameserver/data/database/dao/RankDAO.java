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

import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.data.database.data.RankHistoryData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface RankDAO extends DAO<RankData> {

    @Query("TRUNCATE rankers_snapshot")
    void clearSnapshot();

    @Query("INSERT INTO rankers_snapshot SELECT id, exp, `rank`, rank_race FROM rankers LIMIT 150")
    void updateSnapshot();

    @Query("SELECT * FROM rankers")
    List<RankData> findAll();

    @Query("SELECT * FROM rankers_snapshot")
    IntMap<RankData> findAllSnapshot();

    @Query("SELECT * FROM rankers  WHERE id = :playerId:")
    RankData findPlayerRank(int playerId);

    @Query("SELECT * FROM rankers_race WHERE race = :race:")
    List<RankData> findAllByRace(int race);

    @Query("SELECT * FROM rankers WHERE clan_id = :clanId:")
    List<RankData> findByClan(int clanId);

    @Query("SELECT * FROM rankers WHERE id IN (:playerId:, (SELECT friend_id FROM character_relationship WHERE char_id = :playerId: AND relation = 'FRIEND'))")
    List<RankData> findFriendRankers(int playerId);

    @Query("""
            SELECT rankers.*
            FROM rankers, ( SELECT @base_rank := CONVERT( (SELECT rankers.`rank` FROM rankers WHERE id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND rankers.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<RankData> findRankersNextToPlayer(int playerId);

    @Query("""
            SELECT rankers_race.*
            FROM rankers_race, ( SELECT @base_rank := CONVERT( (SELECT rankers_race.`rank` FROM rankers_race WHERE id = :playerId:), SIGNED ) ) dummy
            WHERE @base_rank IS NOT NULL AND  rankers_race.race = :race:  AND rankers_race.`rank` BETWEEN  @base_rank - 10 AND @base_rank + 10
            """)
    List<RankData> findRaceRankersNextToPlayer(int playerId, int race);

    @Query("INSERT INTO rankers_history SELECT  id, exp, `rank`, :date: FROM rankers_snapshot")
    void updateRankersHistory(long date);

    @Query("DELETE FROM rankers_history WHERE date < :baseDate:")
    void removeOldRankersHistory(long baseDate);

    @Query("SELECT * FROM rankers_history WHERE id = :playerId: ORDER BY date")
    List<RankHistoryData> findPlayerHistory(int playerId);
}