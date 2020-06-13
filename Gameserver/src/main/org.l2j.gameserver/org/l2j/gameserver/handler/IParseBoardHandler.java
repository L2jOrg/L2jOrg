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
package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;

import java.util.StringTokenizer;

/**
 * Community Board interface.
 *
 * @author Zoey76
 */
public interface IParseBoardHandler {

    /**
     * Parses a community board command.
     *
     * @param command the command
     * @param tokens command tokenized
     * @param player  the player
     * @return true if the command was completed
     */
    boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player);

    /**
     * Gets the community board commands.
     *
     * @return the community board commands
     */
    String[] getCommunityBoardCommands();

    default String name() {
        return getClass().getSimpleName().replace("Board", "");
    }
}
