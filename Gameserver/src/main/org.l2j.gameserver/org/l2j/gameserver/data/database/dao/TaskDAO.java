/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
