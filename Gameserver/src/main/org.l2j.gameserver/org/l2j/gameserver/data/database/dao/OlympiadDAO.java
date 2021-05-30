/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.*;

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

    @Query("UPDATE olympiad_participants SET points = :points:, battles = battles+1, battles_today = :battlesToday:,  battles_won = battles_won+1 WHERE player_id = :playerId: AND server = :server:")
    void updateVictory(int playerId, int server, short points, short battlesToday);

    @Query("UPDATE olympiad_participants SET points = :points:, battles_lost = battles_lost+1, battles = battles+1, battles_today=:battlesToday: WHERE player_id = :playerId: AND server = :server:")
    void updateDefeat(int playerId, int server, short points, short battlesToday);

    @Query("UPDATE olympiad_participants SET points = :points:, battles = battles+1, battles_today=:battlesToday: WHERE player_id = :playerId: AND server = :server:")
    void updateTie(int playerId, int server, int points, short battlesToday);

    void save(Collection<OlympiadParticipantData> data);

    @Query("SELECT * FROM olympiad_rankers LIMIT 100")
    List<OlympiadRankData> findRankers();

    @Query("""
    WITH base AS (SELECT CONVERT(`rank`, SIGNED) AS base_rank FROM olympiad_rankers WHERE player_id = :playerId:)
    SELECT olympiad_rankers.*
    FROM olympiad_rankers, base
    WHERE base_rank IS NOT NULL AND olympiad_rankers.`rank` BETWEEN  base_rank - 10 AND base_rank + 10
    """)
    List<OlympiadRankData> findRankersNextToPlayer(int playerId);

    @Query("SELECT * FROM olympiad_rankers_class WHERE class_id = :classId: AND (:server: = 0 OR server = :server:) LIMIT 50")
    List<OlympiadRankData> findRankersByClass(int classId, int server);

    @Query("""
    WITH base AS (SELECT CONVERT(`rank`, SIGNED) AS base_rank FROM olympiad_rankers_class WHERE player_id = :playerId:)
    SELECT * FROM olympiad_rankers_class, base
    WHERE base_rank IS NOT NULL AND class_id = :classId: AND `rank` BETWEEN  base.base_rank - 10 AND base.base_rank + 10
    """)
    List<OlympiadRankData> findRankersNextToPlayerByClass(int playerId, int classId);

    @Query("""
    WITH base AS (SELECT CONVERT(`rank`, SIGNED) AS base_rank FROM olympiad_rankers_class_snapshot WHERE player_id = :playerId:)
    SELECT rc.*, c.level, c.char_name as player_name, IFNULL(cl.clan_name, '') as clan_name, IFNULL(cl.clan_level, 0) as clan_level, h.hero_count, h.legend_count
    FROM base, olympiad_rankers_class_snapshot rc
    JOIN characters c ON c.charId = rc.player_id
    LEFT JOIN clan_data cl  ON c.clanid = cl.clan_id
    LEFT JOIN olympiad_heroes_history h ON rc.player_id = h.player_id AND rc.server = h.server
    WHERE base_rank IS NOT NULL AND  rc.class_id = :classId:  AND rc.`rank` BETWEEN  base_rank - 10 AND base_rank + 10
    ORDER BY rc.`rank`
    """)
    List<OlympiadRankData> findPreviousRankersNextToPlayerByClass(int playerId, int classId);

    @Query("""
    SELECT r.*, c.level, c.char_name as player_name, IFNULL(cl.clan_name, '') as clan_name, IFNULL(cl.clan_level, 0) as clan_level, h.hero_count, h.legend_count
    FROM olympiad_rankers_class_snapshot r
    JOIN characters c ON c.charId = r.player_id
    LEFT JOIN clan_data cl  ON c.clanid = cl.clan_id
    LEFT JOIN olympiad_heroes_history h ON r.player_id = h.player_id AND r.server = h.server
    WHERE r.class_id = :classId: AND (:server: = 0 OR r.server = :server: )
    ORDER BY r.`rank`
    LIMIT 50
    """)
    List<OlympiadRankData> findPreviousRankersByClass(int classId, int server);

    @Query("""
    SELECT r.*, c.level, c.char_name as player_name, IFNULL(cl.clan_name, '') as clan_name, IFNULL(cl.clan_level, 0) as clan_level, hero_count, h.legend_count
    FROM olympiad_rankers_snapshot r
    JOIN characters c ON c.charId = r.player_id
    LEFT JOIN clan_data cl  ON c.clanid = cl.clan_id
    LEFT JOIN olympiad_heroes_history h ON r.player_id = h.player_id AND r.server = h.server
    ORDER BY r.`rank`
    LIMIT 100
    """)
    List<OlympiadRankData> findPreviousRankers();

    @Query("""
    WITH base AS (SELECT CONVERT(`rank`, SIGNED) AS base_rank FROM olympiad_rankers_snapshot WHERE player_id = :playerId:)
    SELECT r.*, c.level, c.char_name as player_name, IFNULL(cl.clan_name, '') as clan_name, IFNULL(cl.clan_level, 0) as clan_level, h.hero_count, h.legend_count
    FROM  base, olympiad_rankers_snapshot r
    JOIN characters c ON c.charId = r.player_id
    LEFT JOIN clan_data cl  ON c.clanid = cl.clan_id
    LEFT JOIN olympiad_heroes_history h ON r.player_id = h.player_id AND r.server = h.server
    WHERE base_rank IS NOT NULL AND r.`rank` BETWEEN  base_rank - 10 AND base_rank + 10
    ORDER BY r.`rank`
    """)
    List<OlympiadRankData> findPreviousRankersNextToPlayer(int playerId);

    @Query("SELECT COUNT(1) FROM olympiad_participants")
    int countParticipants();

    @Query("SELECT COUNT(1) FROM olympiad_rankers_snapshot")
    int countPreviousParticipants();

    @Query("SELECT * FROM olympiad_rankers WHERE player_id = :playerId: AND  server = :server:")
    OlympiadRankData findRankData(int playerId, int server);

    @Query("""
    SELECT r.*, c.level, c.char_name as player_name, IFNULL(cl.clan_name, '') as clan_name, IFNULL(cl.clan_level, 0) as clan_level, h.hero_count, h.legend_count
    FROM olympiad_rankers_snapshot r
    JOIN characters c ON c.charId = r.player_id
    LEFT JOIN clan_data cl  ON c.clanid = cl.clan_id
    LEFT JOIN olympiad_heroes_history h ON r.player_id = h.player_id AND r.server = h.server
    WHERE r.player_id = :playerId: AND  r.server = :server:
    ORDER BY r.`rank`
    """)
    OlympiadRankData findPreviousRankData(int playerId, int server);

    @Query("""
    REPLACE INTO olympiad_rankers_snapshot(player_id, server, `rank`, previous_rank, class_id, battles, battles_won, battles_lost, points, update_date, points_claimed)
    SELECT player_id, server, `rank`, previous_rank, classid, battles, battles_won, battles_lost, points, CURRENT_DATE, FALSE
    FROM ( SELECT op.player_id, op.server,
                  RANK() over (ORDER BY op.points DESC) AS `rank`,
                  IFNULL(ors.`rank`, 0) AS previous_rank,
                  c.classid, op.battles, op.battles_won, op.battles_lost, op.points
            FROM olympiad_participants op
            JOIN characters c on c.charId = op.player_id
            LEFT JOIN olympiad_rankers_snapshot ors on op.player_id = ors.player_id AND op.server = ors.server
            WHERE op.battles >= :minBattles:) AS r;
    """)
    void saveRankSnapshot(byte minBattles);

    @Query("DELETE FROM olympiad_rankers_snapshot WHERE update_date < CURRENT_DATE")
    void deletePreviousRankSnapshot();

    @Query("""
    REPLACE INTO olympiad_rankers_class_snapshot(player_id, server, `rank`, previous_rank, class_id, battles, battles_won, battles_lost, points, update_date)
    SELECT player_id, server, `rank`, previous_rank, classid, battles, battles_won, battles_lost, points, CURRENT_DATE
    FROM ( SELECT op.player_id,
                  op.server,
                  RANK() over (PARTITION BY  c.classid ORDER BY op.points DESC) AS `rank`,
                  IFNULL(ors.`rank`, 0) AS previous_rank,
                  c.classid, op.battles, op.battles_won, op.battles_lost, op.points
           FROM olympiad_participants op
           JOIN characters c on c.charId = op.player_id
           LEFT JOIN olympiad_rankers_snapshot ors on op.player_id = ors.player_id AND op.server = ors.server
           WHERE op.battles >= :minBattles:) AS r;
    """)
    void saveRankClassSnapshot(byte minBattles);

    @Query("DELETE FROM olympiad_rankers_class_snapshot WHERE update_date < CURRENT_DATE")
    void deletePreviousRankClassSnapshot();

    @Query("TRUNCATE olympiad_heroes")
    void deleteHeroes();

    @Query("""
    REPLACE INTO olympiad_heroes_history(player_id, server, class_id, hero_count, legend_count)
    SELECT oh.player_id, oh.server, oh.class_id, IFNULL(ohh.hero_count, 0) + 1, IFNULL(ohh.legend_count, 0)
    FROM olympiad_heroes oh
    LEFT JOIN olympiad_heroes_history ohh on oh.player_id = ohh.player_id AND oh.server = ohh.server
    """)
    void updateHeroesHistory();

    @Query("""
    INSERT INTO olympiad_heroes(player_id, server, class_id)
    SELECT rh.player_id, rh.server, rh.classid FROM (
        SELECT op.player_id,
               op.server,
               c.classid,
               RANK() over (PARTITION BY c.classid ORDER BY points DESC) AS `rank`
        FROM olympiad_participants op
        JOIN characters c on c.charId = op.player_id
        WHERE battles_won >= :minBattlesWon:
        ) AS rh
    WHERE rh.`rank` = 1
    """)
    void saveHeroes(byte minBattlesWon);

    @Query("TRUNCATE olympiad_participants")
    void deleteParticipants();

    @Query("""
    UPDATE olympiad_heroes_history h SET h.legend_count = h.legend_count + 1
    WHERE (h.player_id, h.server) = (SELECT s.player_id, s.server FROM olympiad_rankers_snapshot s WHERE `rank` = 1)
    """)
    void updateLegendHistory();

    @Query("""
    UPDATE olympiad_heroes h SET h.legend = TRUE
    WHERE (h.player_id, h.server) = (SELECT s.player_id, s.server FROM olympiad_rankers_snapshot s WHERE `rank` = 1)
    """)
    void updateLegend();

    @Query("""
    INSERT INTO olympiad_history(player_id, server, cycle, class_id, points, battles, battles_won, battles_lost, overall_rank, overall_count, overall_class_rank, overall_class_count, server_class_rank, server_class_count)
    WITH scr AS ( SELECT player_id, server, c.classid as class_id, RANK() over (PARTITION BY server, c.classid ORDER BY points DESC) as `rank`
                  FROM olympiad_participants
                           JOIN characters c on olympiad_participants.player_id = c.charId
                  WHERE battles >= 10 ),
         overall_count AS ( SELECT COUNT(1) AS c FROM olympiad_rankers_snapshot),
         class_count AS ( SELECT class_id, COUNT(1) AS `count` FROM olympiad_rankers_class_snapshot GROUP BY class_id),
         server_count AS (SELECT server, class_id, COUNT(1) AS `count` FROM scr GROUP BY server, class_id)
    SELECT r.player_id, r.server, :season:, r.class_id, r.points, r.battles, r.battles_won, r.battles_lost, r.`rank`, overall_count.c, rc.`rank`, cc.count, scr.`rank`, sc.count
    FROM overall_count, olympiad_rankers_snapshot r
    JOIN olympiad_rankers_class_snapshot rc on r.player_id = rc.player_id AND r.server = rc.server
    JOIN class_count cc ON r.class_id = cc.class_id
    JOIN server_count sc ON r.class_id = sc.class_id AND r.server = sc.server
    JOIN scr ON scr.player_id = r.player_id AND scr.server = r.server
    """)
    void updateOlympiadHistory(int season);

    @Query("SELECT * FROM olympiad_history WHERE player_id = :playerId: AND server = :server: AND cycle = :season:")
    OlympiadHistoryData findHistory(int playerId, int server, int season);

    @Query(""" 
    SELECT h.player_id, h.server, h.class_id, c.char_name AS name, IFNULL(cl.clan_name, '') AS clan_name, IFNULL(cl.clan_id, 0) AS clan_level, c.sex, c.race, c.level, ohh.hero_count, ohh.legend_count, orcs.battles_won, orcs.battles_lost, orcs.points
    FROM olympiad_heroes h
    JOIN characters c ON h.player_id = c.charId
    JOIN olympiad_heroes_history ohh on h.server = ohh.server AND h.player_id = ohh.player_id
    JOIN olympiad_rankers_class_snapshot orcs on h.player_id = h.player_id AND h.server = orcs.server
    LEFT JOIN clan_data cl ON c.clanid = cl.clan_id
    WHERE legend = TRUE
    """)
    OlympiadHeroData findRankLegend();

    @Query(""" 
    SELECT h.player_id, h.server, h.class_id, h.legend, c.char_name AS name, IFNULL(cl.clan_name, '') AS clan_name, IFNULL(cl.clan_level, 0) AS clan_level, c.sex, c.race, c.level, ohh.hero_count, ohh.legend_count, orcs.battles_won, orcs.battles_lost, orcs.points
    FROM olympiad_heroes h
    JOIN characters c ON h.player_id = c.charId
    JOIN olympiad_heroes_history ohh on h.server = ohh.server AND h.player_id = ohh.player_id
    JOIN olympiad_rankers_class_snapshot orcs on h.player_id = h.player_id AND h.server = orcs.server AND h.class_id = orcs.class_id
    LEFT JOIN clan_data cl ON c.clanid = cl.clan_id
    """)
    List<OlympiadHeroData> findRankHeroes();

    void save(OlympiadMatchResultData matchResult);

    @Query("""
    SELECT h.*, c.char_name as opponent_name, c.classid as opponent_class_id FROM olympiad_heroes_matches h
    JOIN characters c ON h.opponent = c.charId
    WHERE h.class_id = :classId:
    """)
    List<OlympiadMatchResultData> findHeroHistoryByClassId(int classId);

    @Query("""
    INSERT INTO olympiad_heroes_matches(num, player_id, server, class_id, opponent, date, duration, result, win, loss, tie)
    SELECT m.* FROM olympiad_matches m
    JOIN olympiad_heroes oh on m.player_id = oh.player_id AND  m.server = oh.server;
    """)
    void saveHeroesMatches();

    @Query("TRUNCATE olympiad_heroes_matches")
    void deleteHeroesMatches();

    @Query("TRUNCATE olympiad_matches")
    void deleteMatches();

    @Query("SELECT EXISTS(SELECT 1 FROM olympiad_heroes WHERE player_id = :playerId: AND server = :server: AND claimed = FALSE)")
    boolean isUnclaimedHero(int playerId, int server);

    @Query("UPDATE olympiad_heroes SET claimed = TRUE WHERE player_id = :playerId: AND server = :server:")
    void claimHero(int playerId, int server);

    @Query("SELECT EXISTS(SELECT 1 FROM olympiad_heroes WHERE player_id = :playerId: AND server = :server: AND claimed = TRUE)")
    boolean isHero(int playerId, int server);

    @Query("SELECT player_id FROM olympiad_heroes WHERE server = :server:")
    IntSet findHeroesId(int server);

    @Query("SELECT points FROM olympiad_rankers_snapshot WHERE player_id =:playerId: AND server = :server: AND  points_claimed = FALSE")
    int unclaimedPoints(int playerId, int server);

    @Query("UPDATE olympiad_rankers_snapshot SET points_claimed = TRUE WHERE player_id = :playerId: AND server = :server:")
    void claimPoints(int playerId, int server);
}
