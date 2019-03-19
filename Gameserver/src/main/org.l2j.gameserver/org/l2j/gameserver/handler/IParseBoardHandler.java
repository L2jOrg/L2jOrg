package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Community Board interface.
 *
 * @author Zoey76
 */
public interface IParseBoardHandler {
    Logger LOG = LoggerFactory.getLogger(IParseBoardHandler.class.getName());

    /**
     * Parses a community board command.
     *
     * @param command the command
     * @param player  the player
     * @return
     */
    boolean parseCommunityBoardCommand(String command, L2PcInstance player);

    /**
     * Gets the community board commands.
     *
     * @return the community board commands
     */
    String[] getCommunityBoardCommands();
}
