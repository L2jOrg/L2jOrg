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
 * This class contains an enum of each possibles events that can happen on an AI character.
 */
public enum CtrlEvent {
    /**
     * Something has changed, usually a previous step has being completed or maybe was completed, the AI must thing on next action.
     */
    EVT_THINK,
    /**
     * The actor was attacked. This event comes each time a physical or magical<br>
     * attack was done on the actor. NPC may start attack in response, or ignore<br>
     * this event if they already attack someone, or change target and so on.
     */
    EVT_ATTACKED,
    /**
     * Increase/decrease aggression towards a target, or reduce global aggression if target is null
     */
    EVT_AGGRESSION,
    /**
     * Actor is in stun state
     */
    EVT_ACTION_BLOCKED,
    /**
     * Actor is in rooted state (cannot move)
     */
    EVT_ROOTED,
    /**
     * Actor evaded hit
     **/
    EVT_EVADED,
    /**
     * An event that previous action was completed. The action may be an attempt to physically/magically hit an enemy, or an action that discarded attack attempt has finished.
     */
    EVT_READY_TO_ACT,
    /**
     * The actor arrived to assigned location, or it's a time to modify movement destination (follow, interact, random move and others intentions).
     */
    EVT_ARRIVED,
    /**
     * The actor arrived to an intermediate point, and needs to revalidate destination. This is sent when follow/move to pawn if destination is far away.
     */
    EVT_ARRIVED_REVALIDATE,
    /**
     * The actor cannot move anymore.
     */
    EVT_ARRIVED_BLOCKED,
    /**
     * Forgets an object (if it's used as attack target, follow target and so on
     */
    EVT_FORGET_OBJECT,
    /**
     * Attempt to cancel current step execution, but not change the intention.<br>
     * For example, the actor was put into a stun, so it's current attack<br>
     * or movement has to be canceled. But after the stun state expired,<br>
     * the actor may try to attack again. Another usage for CANCEL is a user's<br>
     * attempt to cancel a cast/bow attack and so on.
     */
    EVT_CANCEL,
    /**
     * The character is dead
     */
    EVT_DEAD,
    /**
     * The character looks like dead
     */
    EVT_FAKE_DEATH,
    /**
     * The character attack anyone randomly
     **/
    EVT_CONFUSED,
    /**
     * The character cannot cast spells anymore
     **/
    EVT_MUTED,
    /**
     * The character flee in random directions
     **/
    EVT_AFRAID,
    /**
     * The character finish casting
     **/
    EVT_FINISH_CASTING,
    /**
     * The character betrayed its master
     */
    EVT_BETRAYED
}
