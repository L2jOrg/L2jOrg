package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface CharacterDAO extends DAO {

    @Query("UPDATE characters SET online = 0")
    int setAllCharactersOffline();
}
