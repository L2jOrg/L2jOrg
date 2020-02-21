package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.Shortcut;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface ShortcutDAO extends DAO<Shortcut> {

    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId: AND client_id=:clientId: AND class_index=:classIndex:")
    void delete(int playerId, int clientId, int classIndex);

    @Query("SELECT * FROM character_shortcuts WHERE player_id=:playerId: AND class_index=:classIndex:")
    List<Shortcut> findByPlayer(int playerId, int classIndex);

    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId: AND class_index=:classIndex:")
    void deleteFromSubclass(int playerId, int classIndex);

    @Query("DELETE FROM character_shortcuts WHERE player_id=:playerId:")
    void deleteAll(int playerId);
}
