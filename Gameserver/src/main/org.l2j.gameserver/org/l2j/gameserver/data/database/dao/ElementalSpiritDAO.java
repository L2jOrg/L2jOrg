package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ElementalSpiritDAO extends DAO<ElementalSpiritData> {

    @Query("SELECT * FROM character_spirits WHERE charId = :playerId:")
    List<ElementalSpiritData> findByPlayerId(int playerId);

    @Query("UPDATE character_spirits SET in_use = type = :type: WHERE charId = :playerId:")
    void updateActiveSpirit(int playerId, byte type);
}
