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
import org.l2j.gameserver.data.database.data.PlayerVariableData;

/**
 * @author JoeAlisson
 */
public interface PlayerVariablesDAO extends DAO<PlayerVariableData> {

    @Query("SELECT * FROM player_variables WHERE player_id = :playerId:")
    PlayerVariableData findById(int playerId);

    @Query("UPDATE player_variables SET revenge_locations = 5, revenge_teleports = 5")
    void resetRevengeData();

    @Query("UPDATE player_variables SET world_chat_used = 1")
    void resetWorldChatPoint();

    @Query("UPDATE player_variables SET claimed_clan_rewards = 0")
    void resetClaimedClanReward();
}
