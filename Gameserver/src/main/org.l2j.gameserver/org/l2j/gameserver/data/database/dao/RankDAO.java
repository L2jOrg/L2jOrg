package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.RankData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface RankDAO extends DAO<RankData> {

    @Query("TRUNCATE rankers_snap")
    void clearSnapshot();

    @Query("TRUNCATE rankers_race_snap")
    void  clearRaceSnapshot();

    @Query("INSERT INTO rankers_snap SELECT * FROM rankers")
    void updateSnapshot();

    @Query("INSERT INTO rankers_race_snap SELECT * FROM rankers_race")
    void updateRaceSnapshot();

    @Query("SELECT * FROM rankers")
    List<RankData> findAll();

    @Query("SELECT * FROM rankers_snap")
    List<RankData> findAllSnapshot();
}
