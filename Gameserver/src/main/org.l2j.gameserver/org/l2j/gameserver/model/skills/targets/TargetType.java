package org.l2j.gameserver.model.skills.targets;

/**
 * Target type enumerated.
 *
 * @author Zoey76
 */
public enum TargetType {
    /**
     * Advance Head Quarters (Outposts).
     */
    ADVANCE_BASE,
    /**
     * Enemies in high terrain or protected by castle walls and doors.
     */
    ARTILLERY,
    /**
     * Doors or treasure chests.
     */
    DOOR_TREASURE,
    /**
     * Any enemies (included allies).
     */
    ENEMY,
    /**
     * Friendly.
     */
    ENEMY_NOT,
    /**
     * Only enemies (not included allies).
     */
    ENEMY_ONLY,
    /**
     * Fortress's Flagpole.
     */
    FORTRESS_FLAGPOLE,
    /**
     * Ground.
     */
    GROUND,
    /**
     * Holy Artifacts from sieges.
     */
    HOLYTHING,
    /**
     * Items.
     */
    ITEM,
    /**
     * Nothing.
     */
    NONE,
    /**
     * NPC corpses.
     */
    NPC_BODY,
    /**
     * Others, except caster.
     */
    OTHERS,
    /**
     * Player corpses.
     */
    PC_BODY,
    /**
     * Self.
     */
    SELF,
    /**
     * Servitor or pet.
     */
    SUMMON,
    /**
     * Anything targetable.
     */
    TARGET,
    /**
     * Wyverns.
     */
    WYVERN_TARGET,
    /**
     * Mentee's Mentor.
     */
    MY_MENTOR,
    /**
     * Me or my party (if any). Seen in aura skills.
     */
    MY_PARTY,
    /**
     * Pet's owner.
     */
    OWNER_PET,
}
