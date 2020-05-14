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
