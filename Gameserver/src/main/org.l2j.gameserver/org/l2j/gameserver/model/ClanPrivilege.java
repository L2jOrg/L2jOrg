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
