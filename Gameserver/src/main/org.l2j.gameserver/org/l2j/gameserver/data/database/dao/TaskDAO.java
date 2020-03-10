package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.TaskData;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface TaskDAO extends DAO<TaskData> {

    @Query("SELECT * FROM global_tasks")
    List<TaskData> findAll();

    @Query("UPDATE global_tasks SET last_activation=:lastActivation: WHERE id=:id:")
    void updateLastActivation(int id, long lastActivation);

    @Query("SELECT EXISTS (SELECT 1 FROM global_tasks WHERE name=:task:)")
    boolean existsWithName(String task);
}
