/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.util;

/**
 * Flood protector configuration
 *
 * @author fordfrog
 */
public final class FloodProtectorConfig {
    /**
     * Type used for identification of logging output.
     */
    public String FLOOD_PROTECTOR_TYPE;
    /**
     * Flood protection interval in game ticks.
     */
    public int FLOOD_PROTECTION_INTERVAL;
    /**
     * Whether flooding should be logged.
     */
    public boolean LOG_FLOODING;
    /**
     * If specified punishment limit is exceeded, punishment is applied.
     */
    public int PUNISHMENT_LIMIT;
    /**
     * Punishment type. Either 'none', 'kick', 'ban' or 'jail'.
     */
    public String PUNISHMENT_TYPE;
    /**
     * For how long should the char/account be punished.
     */
    public long PUNISHMENT_TIME;

    /**
     * Creates new instance of FloodProtectorConfig.
     *
     * @param floodProtectorType {@link #FLOOD_PROTECTOR_TYPE}
     */
    public FloodProtectorConfig(String floodProtectorType) {
        super();
        FLOOD_PROTECTOR_TYPE = floodProtectorType;
    }
}
