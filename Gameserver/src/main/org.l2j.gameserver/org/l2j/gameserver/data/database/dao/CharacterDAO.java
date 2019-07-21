package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.CharacterData;

import java.util.List;

public interface CharacterDAO extends DAO<CharacterData> {

    @Query("UPDATE characters SET online = 0")
    void setAllCharactersOffline();

    @Query("SELECT * FROM characters WHERE charId = :objectId:")
    CharacterData findById(int objectId);

    @Query("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data)")
    void resetClanInfoOfInextentClan();

    @Query("DELETE FROM character_instance_time WHERE time <= :timestamp:")
    void deleteExpiredInstances(long timestamp);

    @Query("DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= :timestamp:")
    void deleteExpiredSavedSkills(long timestamp);

    @Query("SELECT charId, createDate FROM characters WHERE DAYOFMONTH(createDate) = :day: AND MONTH(createDate) = :month: AND YEAR(createDate) < :year:")
    List<CharacterData> findBirthdayCharacters(int year, int month, int day);

    @Query("SELECT charId, accesslevel FROM characters WHERE char_name=:name:")
    CharacterData findIdAndAccessLevelByName(String name);
}
