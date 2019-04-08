package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.model.Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public interface CharacterDAO extends DAO {

    @Query("UPDATE characters SET online = 0")
    void setAllCharactersOffline();

    @Query("SELECT * FROM characters WHERE charId = :objectId:")
    Character findById(int objectId);
}
