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
