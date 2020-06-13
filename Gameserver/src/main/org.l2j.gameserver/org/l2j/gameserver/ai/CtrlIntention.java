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
package org.l2j.gameserver.ai;

/**
 * Enumeration of generic intentions of an NPC/PC, an intention may require several steps to be completed.
 */
public enum CtrlIntention {
    /**
     * Do nothing, disconnect AI of NPC if no players around
     */
    AI_INTENTION_IDLE,
    /**
     * Alerted state without goal : scan attackable targets, random walk, etc
     */
    AI_INTENTION_ACTIVE,
    /**
     * Rest (sit until attacked)
     */
    AI_INTENTION_REST,
    /**
     * Attack target (cast combat magic, go to target, combat), may be ignored, if target is locked on another character or a peaceful zone and so on.
     */
    AI_INTENTION_ATTACK,
    /**
     * Cast a spell, depending on the spell - may start or stop attacking
     */
    AI_INTENTION_CAST,
    /**
     * Just move to another location
     */
    AI_INTENTION_MOVE_TO,
    /**
     * Like move, but check target's movement and follow it
     */
    AI_INTENTION_FOLLOW,
    /**
     * PickUp and item, (got to item, pickup it, become idle
     */
    AI_INTENTION_PICK_UP,
    /**
     * Move to target, then interact
     */
    AI_INTENTION_INTERACT
}
