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

import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.TimeRestrictZoneInfo;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public interface TimeRestrictDAO extends DAO<TimeRestrictZoneInfo> {

    void save(Collection<TimeRestrictZoneInfo> values);

    @Query("SELECT zone, player_id, remaining_time, recharged_time FROM player_time_restrict_zones WHERE player_id = :playerId:")
    IntMap<TimeRestrictZoneInfo> loadTimeRestrictZoneInfo(int playerId);

    @Query("TRUNCATE player_time_restrict_zones")
    void deleteRestrictZoneInfo();

    @Query("DELETE FROM player_time_restrict_zones WHERE reset_cycle = :cycle:")
    void deleteRestrictZoneInfo(TimeRestrictZone.ResetCycle cycle);

}
