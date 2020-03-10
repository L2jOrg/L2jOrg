package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface CursedWeaponDAO extends DAO<Object> {

    @Query("DELETE FROM cursed_weapons WHERE itemId = :itemId:")
    void deleteByItem(int itemId);
}
