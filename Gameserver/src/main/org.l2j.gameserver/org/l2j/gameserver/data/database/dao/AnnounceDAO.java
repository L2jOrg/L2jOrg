package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.announce.AnnounceData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface AnnounceDAO extends DAO<AnnounceData> {

    @Query("SELECT * FROM announcements")
    List<AnnounceData> findAll();

    @Query("DELETE FROM announcements WHERE id = :id:")
    void deleteById(int id);
}
