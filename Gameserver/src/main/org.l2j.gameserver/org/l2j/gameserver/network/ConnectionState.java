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
package org.l2j.gameserver.network;

import java.util.EnumSet;

/**
 * @author Nos
 * @author JoeAlisson
 */
public enum ConnectionState {
    CONNECTED,
    DISCONNECTED,
    CLOSING,
    AUTHENTICATED,
    JOINING_GAME,
    IN_GAME;

    static final EnumSet<ConnectionState> IN_GAME_STATES = EnumSet.of(IN_GAME);
    static final EnumSet<ConnectionState> AUTHENTICATED_STATES = EnumSet.of(AUTHENTICATED);
    static final EnumSet<ConnectionState> CONNECTED_STATES = EnumSet.of(CONNECTED);
    static final EnumSet<ConnectionState> JOINING_GAME_STATES = EnumSet.of(JOINING_GAME);
    static final EnumSet<ConnectionState> AUTHENTICATED_AND_IN_GAME = EnumSet.of(AUTHENTICATED, IN_GAME);
    static final EnumSet<ConnectionState> JOINING_GAME_AND_IN_GAME = EnumSet.of(JOINING_GAME, IN_GAME);
    static final EnumSet<ConnectionState> ALL = EnumSet.allOf(ConnectionState.class);
    static final EnumSet<ConnectionState> EMPTY = EnumSet.noneOf(ConnectionState.class);
}


