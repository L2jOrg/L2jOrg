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
package org.l2j.gameserver.model;

/**
 * This enum is used for clan privileges.<br>
 * The ordinal of each entry is the bit index in the privilege bitmask.
 *
 * @author HorridoJoho
 */
public enum ClanPrivilege {
    /**
     * dummy entry
     */
    DUMMY,
    /**
     * Privilege to join clan
     */
    CL_JOIN_CLAN,
    /**
     * Privilege to give a title
     */
    CL_GIVE_TITLE,
    /**
     * Privilege to view warehouse content
     */
    CL_VIEW_WAREHOUSE,
    /**
     * Privilege to manage clan ranks
     */
    CL_MANAGE_RANKS,
    CL_PLEDGE_WAR,
    CL_DISMISS,
    /**
     * Privilege to register clan crest
     */
    CL_REGISTER_CREST,
    CL_APPRENTICE,
    CL_TROOPS_FAME,
    CL_SUMMON_AIRSHIP,
    /**
     * Privilege to open a door
     */
    CH_OPEN_DOOR,
    CH_OTHER_RIGHTS,
    CH_AUCTION,
    CH_DISMISS,
    CH_SET_FUNCTIONS,
    CS_OPEN_DOOR,
    CS_MANOR_ADMIN,
    CS_MANAGE_SIEGE,
    CS_USE_FUNCTIONS,
    CS_DISMISS,
    CS_TAXES,
    CS_MERCENARIES,
    CS_SET_FUNCTIONS;

    public int getBitmask() {
        return 1 << ordinal();
    }
}
