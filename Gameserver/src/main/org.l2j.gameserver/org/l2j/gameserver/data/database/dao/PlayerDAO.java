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
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.*;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public interface PlayerDAO extends DAO<PlayerData> {

    @Query("UPDATE characters SET online = 0")
    void setAllCharactersOffline();

    @Query("SELECT * FROM characters WHERE charId = :objectId:")
    PlayerData findById(int objectId);

    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, title='', lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data)")
    void resetClanInfoOfNonexistentClan();

    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, title='', lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=:clanJoinExpiryTime:, clan_create_expiry_time=:clanCreateExpiryTime: WHERE charId = :playerId:")
    void deleteClanInfoOfMember(int playerId, long clanJoinExpiryTime, long clanCreateExpiryTime);

    @Query("DELETE FROM character_instance_time WHERE time <= :timestamp:")
    void deleteExpiredInstances(long timestamp);

    @Query("DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= :timestamp:")
    void deleteExpiredSavedSkills(long timestamp);

    @Query("SELECT charId, createDate FROM characters WHERE DAYOFMONTH(createDate) = :day: AND MONTH(createDate) = :month: AND YEAR(createDate) < :year:")
    List<PlayerData> findBirthdayCharacters(int year, int month, int day);

    @Query("SELECT charId, accesslevel FROM characters WHERE char_name=:name:")
    PlayerData findIdAndAccessLevelByName(String name);

    @Query("REPLACE INTO character_relationship (char_id, friend_id) VALUES (:playerId:, :otherId:), (:otherId:, :playerId:)")
    void saveFriendship(int playerId, int otherId);

    @Query("SELECT friend_id FROM character_relationship WHERE char_id=:playerId: AND relation='FRIEND'")
    IntSet findFriendsById(int playerId);

    @Query("DELETE FROM character_relationship WHERE (char_id=:playerId: AND friend_id=:friendId:) OR (char_id=:friendId: AND friend_id=:playerId:)")
    void deleteFriendship(int playerId, int friendId);

    @Query("SELECT friend_id FROM character_relationship WHERE char_id=:playerId: AND relation='BLOCK'")
    IntSet findBlockListById(int playerId);

    @Query("REPLACE INTO character_relationship (char_id, friend_id, relation) VALUES (:playerId:, :blockedId:, 'BLOCK')")
    void saveBlockedPlayer(int playerId, int blockedId);

    @Query("DELETE FROM character_relationship WHERE char_id=:playerId: AND friend_id=:blockedId: AND relation='BLOCK'")
    void deleteBlockedPlayer(int playerId, int blockedId);

    @Query("SELECT char_name, classid, level, lastAccess, clanid, createDate FROM characters WHERE charId = :friendId:")
    PlayerData findFriendData(int friendId);

    @Query("UPDATE characters SET clan_create_expiry_time = 0, clan_join_expiry_time = 0 WHERE char_name=:name:")
    void removeClanPenalty(String name);

    @Query("UPDATE characters SET accesslevel=:level: WHERE char_name=:name:")
    boolean updateAccessLevelByName(String name, int level);

    @Query("UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE charId=:objectId:")
    void updateToValidLocation(int objectId);

    @Query("REPLACE INTO player_killers (player_id, killer_id, kill_time) VALUES (:player:, :killer:, :time: )")
    void updatePlayerKiller(int player, int killer, long time);

    @Query("""
            SELECT pk.killer_id, pk.kill_time, c.char_name as name, IFNULL(cd.clan_name, '') AS clan, c.level, c.race, c.classid as active_class, c.online
            FROM player_killers pk
            JOIN characters c on pk.killer_id = c.charId
            LEFT JOIN  clan_data cd on c.clanid = cd.clan_id
            WHERE pk.player_id = :player: AND pk.kill_time >= :since:
            """)
    List<KillerData> findKillersByPlayer(int player, long since);

    @Query("UPDATE characters SET accesslevel=:level: WHERE charId=:playerId:")
    void updateAccessLevel(int playerId, int level);

    @Query("UPDATE characters SET x=:x:, y=:y:, z=:z: WHERE char_name=:name:")
    boolean updateLocationByName(String name, int x, int y, int z);

    @Query("SELECT char_name,accesslevel FROM characters WHERE charId=:id:")
    PlayerData findNameAndAccessLevelById(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM characters WHERE char_name=:name:)")
    boolean existsByName(String name);

    @Query("SELECT COUNT(1) as count FROM characters WHERE account_name=:account:")
    int playerCountByAccount(String account);

    @Query("SELECT classid FROM characters WHERE charId=:id: ")
    int findClassIdById(int id);

    @Query("SELECT charId, char_name, accesslevel FROM characters")
    void withPlayersDataDo(Consumer<ResultSet> action);

    @Query("SELECT char_name,level,classid,charId,title,power_grade,subpledge,apprentice,sponsor,sex,race FROM characters WHERE clanid=:clanId:")
    List<PlayerData> findClanMembers(int clanId);

    @Query("UPDATE characters SET apprentice=0 WHERE apprentice=:playerId:")
    void deleteApprentice(int playerId);

    @Query("UPDATE characters SET sponsor=0 WHERE sponsor=:playerId:")
    void deleteSponsor(int playerId);

    @Query("UPDATE characters SET clan_privs = :privs: WHERE charId = :id:")
    void updateClanPrivs(int id, int privs);

    void save(PlayerStatsData statsData);

    @Query("SELECT * FROM player_stats_points WHERE player_id=:playerId:")
    PlayerStatsData findPlayerStatsData(int playerId);

    void save(Collection<CostumeData> costumes);

    @Query("SELECT id, player_id, amount, locked FROM player_costumes WHERE player_id = :playerId:")
    IntMap<CostumeData> findCostumes(int playerId);

    @Query("DELETE FROM player_costumes WHERE player_id = :playerId: AND id = :costumeId:")
    void removeCostume(int playerId, int costumeId);

    @Query("SELECT * FROM player_costume_collection WHERE player_id = :playerId:")
    CostumeCollectionData findPlayerCostumeCollection(int playerId);

    void save(CostumeCollectionData activeCostumesCollection);

    @Query("DELETE FROM player_costume_collection WHERE player_id = :playerId:")
    void deleteCostumeCollection(int playerId);

    @Query("SELECT teleport_id FROM player_teleports WHERE player_id = :playerId:")
    IntSet findTeleportFavorites(int playerId);

    @Query(value = "REPLACE INTO player_teleports VALUES (:playerId:, :teleports: )", batchIndex = 1)
    void saveTeleportFavorites(int playerId, IntSet teleports);

    @Query("DELETE FROM player_teleports WHERE player_id = :playerId:")
    void removeTeleportFavorites(int playerId);

    @Query("DELETE FROM characters WHERE charId=:playerId:")
    void deleteById(int playerId);

    @Query("UPDATE characters SET deletetime=:deleteTime: WHERE charId=:playerId:")
    void updateDeleteTime(int playerId, long deleteTime);
}
