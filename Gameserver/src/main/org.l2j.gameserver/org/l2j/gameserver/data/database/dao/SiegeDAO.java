/*
 * Copyright © 2019-2020 L2JOrg
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

/**
 * @author JoeAlisson
 */
public interface SiegeDAO extends DAO<Object> {

    @Query("DELETE FROM castle_siege_guards WHERE npcId = :npcId: AND x = :x: AND y = :y: AND z = :z: AND isHired = 1")
    void deleteGuard(int npcId, int x, int y, int z);

    @Query("DELETE FROM castle_siege_guards WHERE castleId = :castleId: AND isHired = 1")
    void deleteGuardsOfCastle(int castleId);

    @Query("SELECT EXISTS(SELECT 1 FROM siege_clans WHERE clan_id=:clanId: AND castle_id=:castleId:)")
    boolean isRegistered(int clanId, int castleId);
}
