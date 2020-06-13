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
package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.actor.Creature;

/**
 * Interface of AI and client state.<br>
 * To correctly send messages to client we need it's state.<br>
 * For example, if we've sent 'StartAutoAttack' message, we need to send 'StopAutoAttack' message before any other action.<br>
 * Or if we've sent 'MoveToPawn', we need to send 'StopMove' when the movement of a character is canceled (by Root spell or any other reason).<br>
 * Thus, we need to know the state of client, i.e. which messages we've sent and how the client will show the scene.<br>
 * Close to this task is the task of AI.<br>
 * If a player's character is attacking a mob, his ATTACK may be interrupted by an event, that temporary disable attacking.<br>
 * But when the possibility to ATTACK will be enabled, the character must continue the ATTACK.<br>
 * For mobs it may be more complex, since we want them to decide when to use magic, or when to follow the player for physical combat, or when to escape, to help another mob, etc.<br>
 * This interface is hiding complexity of server<->client interaction and multiple states of a character.<br>
 * It allows to set a desired, simple "wish" of a character, and the implementation of this interface will take care about the rest.<br>
 * The goal of a character may be like "ATTACK", "random walk" and so on.<br>
 * To reach the goal implementation will split it into several small actions, several steps (possibly repeatable).<br>
 * Like "run to target" then "hit it", then if target is not dead - repeat.<br>
 * This flow of simpler steps may be interrupted by incoming events.<br>
 * Like a character's movement was disabled (by Root spell, for instance).<br>
 * Depending on character's ability AI may choose to wait, or to use magic ATTACK and so on.<br>
 * Additionally incoming events are compared with client's state of the character,<br>
 * and required network messages are sent to client's, i.e. if we have incoming event that character's movement was disabled, it causes changing if its behavior,<br>
 * and if client's state for the character is "moving" we send messages to clients to stop the avatar/mob.
 */
public interface Ctrl {
    /**
     * Gets the actor.
     *
     * @return the actor
     */
    Creature getActor();

    /**
     * Gets the intention.
     *
     * @return the intention
     */
    CtrlIntention getIntention();

    /**
     * Set general state/intention for AI, with optional data.
     *
     * @param intention the new intention
     */
    void setIntention(CtrlIntention intention);

    /**
     * Sets the intention.
     *
     * @param intention the intention
     * @param args
     */
    void setIntention(CtrlIntention intention, Object... args);

    /**
     * Event, that notifies about previous step result, or user command, that does not change current general intention.
     *
     * @param evt the event
     */
    void notifyEvent(CtrlEvent evt);

    /**
     * Notify an event.
     *
     * @param evt  the event
     * @param arg0 the arg0
     */
    void notifyEvent(CtrlEvent evt, Object arg0);

    /**
     * Notify an event.
     *
     * @param evt  the event
     * @param arg0 the arg0
     * @param arg1 the arg1
     */
    void notifyEvent(CtrlEvent evt, Object arg0, Object arg1);
}
