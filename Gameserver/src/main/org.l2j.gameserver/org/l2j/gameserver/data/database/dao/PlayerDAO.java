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

import io.github.joealisson.primitive.IntKeyIntValue;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.*;
import org.l2j.gameserver.engine.skill.api.Skill;

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

    @Query("SELECT * FROM characters WHERE account_name = :account: ORDER BY createDate")
    List<PlayerData> findPlayersByAccount(String account);

    @Query("SELECT * FROM characters WHERE charId = :objectId:")
    PlayerData findById(int objectId);

    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, title='', apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data)")
    void resetClanInfoOfNonexistentClan();

    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, title='', apprentice=0, sponsor=0, clan_join_expiry_time=:clanJoinExpiryTime:, clan_create_expiry_time=:clanCreateExpiryTime: WHERE charId = :playerId:")
    void deleteClanInfoOfMember(int playerId, long clanJoinExpiryTime, long clanCreateExpiryTime);

    @Query("DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= :timestamp:")
    void deleteExpiredSavedSkills(long timestamp);

    @Query("DELETE FROM character_skills_save WHERE charId=:playerId:")
    void deleteSavedSkills(int playerId);

    @Query("SELECT * FROM character_skills_save WHERE charId=:playerId: ORDER BY buff_index")
    void findSavedSkill(int playerId, Consumer<ResultSet> action);

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

    @Query("""
          SELECT c.char_name, c.level, c.classid, c.charId, c.title, c.power_grade, c.apprentice, c.sponsor, c.sex, c.race, cm.last_reputation_level
          FROM characters c LEFT JOIN clan_members cm ON c.charId = cm.player_id AND c.clanid = cm.clan_id
          WHERE clanid=:clanId:
          """)
    List<ClanMember> findClanMembers(int clanId);

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

    @Query("DELETE FROM character_skills_save WHERE skill_id=:skillId:")
    void deleteSkillSave(int skillId);

    @Query("REPLACE INTO character_reco_bonus (charId,rec_have,rec_left,time_left) VALUES (:playerId:,:recommend:,:recommendLeft:,:timeLeft:)")
    void saveRecommends(int playerId, int recommend, int recommendLeft, long timeLeft);

    @Query("UPDATE character_reco_bonus SET rec_left = 20, rec_have = GREATEST(CAST(rec_have AS SIGNED)  -20 , 0)")
    void resetRecommends();

    @Query("UPDATE characters SET vitality_points = :points:")
    void resetVitality(int points);

    @Query("DELETE FROM recipes WHERE player_id=:playerId: AND id=:recipeId:")
    void deleteRecipe(int playerId, int recipeId);

    @Query("SELECT id FROM recipes WHERE player_id=:playerId:")
    IntSet findAllRecipes(int playerId);

    @Query("INSERT INTO recipes (player_id, id) values(:playerId:,:id:)")
    void addRecipe(int playerId, int id);

    @Query("UPDATE characters SET online=:online:, lastAccess=:lastAccess: WHERE charId=:playerId:")
    void updateOnlineStatus(int playerId, boolean online, long lastAccess);

    @Query("DELETE FROM character_skills WHERE skill_id=:skillId: AND charId=:playerId:")
    void deleteSkill(int playerId, int skillId);

    @Query("DELETE FROM character_hennas WHERE charId=:playerId: AND slot=:slot:")
    void deleteHenna(int playerId, int slot);

    @Query("UPDATE character_tpbookmark SET icon=:icon:,tag=:tag:,name=:name: where charId=:playerId: AND Id=:id:")
    void updateTeleportBookMark(int playerId, int id, int icon, String tag, String name);

    @Query("DELETE FROM character_tpbookmark WHERE charId=:playerId: AND Id=:id:")
    void deleteTeleportBookMark(int playerId, int id);

    @Query("UPDATE characters SET power_grade=:powerGrade: WHERE charId=:playerId:")
    void updatePowerGrade(int playerId, int powerGrade);

    @Query("UPDATE characters SET apprentice=:apprentice:,sponsor=:sponsor: WHERE charId= :playerId:")
    void updateApprenticeAndSponsor(int playerId, int apprentice, int sponsor);

    @Query("DELETE FROM character_contacts WHERE charId =:playeId: and contactId = :contactId:")
    void deleteContact(int playerId, int contactId);

    @Query("INSERT INTO character_contacts (charId, contactId) VALUES (:playerId:, :contactId:)")
    void addContact(int playerId, int contactId);

    @Query("SELECT contactId FROM character_contacts WHERE charId = :objectId:")
    void findContacts(int objectId, Consumer<ResultSet> resultSet);

    @Query("DELETE FROM character_instance_time WHERE charId=:playerId: AND instanceId=:id:")
    void deleteInstanceTime(int playerId, int id);

    @Query("DELETE FROM character_item_reuse_save WHERE charId=:playerId:")
    void deleteSavedItemReuse(int playerId);

    @Query("SELECT * FROM character_item_reuse_save WHERE charId=:playerId:")
    void findSavedItemReuse(int playerId, Consumer<ResultSet> action);

    @Query("REPLACE INTO character_skills (charId,skill_id,skill_level) VALUES (:playerId:,:skillId:,:skillLevel:)")
    void saveSkill(int playerId, int skillId, int skillLevel);

    @Query("SELECT skill_id,skill_level,skill_sub_level FROM character_skills WHERE charId=:playerId:")
    List<Skill> findSkills(int playerId);

    @Query("SELECT slot, symbol_id FROM character_hennas WHERE charId=:playerId:")
    void findHennas(int playerId, Consumer<ResultSet> action);

    @Query("INSERT INTO character_hennas (charId,symbol_id,slot) VALUES (:playerId:, :dyeId:, :slot:)")
    void saveHenna(int playerId, int dyeId, int slot);

    @Query("SELECT id,x,y,z,icon,tag,name,charId FROM character_tpbookmark WHERE charId=:playerId:")
    IntMap<TeleportBookmark> findTeleportBookmark(int playerId);

    void save(TeleportBookmark bookmark);

    @Query("SELECT rec_have, rec_left FROM character_reco_bonus WHERE charId = :playerId:")
    IntKeyIntValue findRecommends(int playerId);

    @Query("INSERT INTO clan_members (clan_id, player_id, last_reputation_level) VALUES (:clanId:, :playerId:, :level:) AS v ON DUPLICATE KEY UPDATE last_reputation_level = v.last_reputation_level")
    void saveLastReputationLevel(int clanId, int playerId, byte level);
}
