package org.l2j.gameserver.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

// TODO Rename to CharacterDAO after Remove it
public interface ICharacterDAO extends DAO {

    @Query("UPDATE characters SET online = 0")
    void updateCharactersOfflineStatus();
}
