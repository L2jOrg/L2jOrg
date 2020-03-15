package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface IUserCommandHandler {
    Logger LOGGER = LoggerFactory.getLogger(IUserCommandHandler.class.getName());

    /**
     * this is the worker method that is called when someone uses an admin command.
     *
     * @param id
     * @param player
     * @return command success
     */
    boolean useUserCommand(int id, Player player);

    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known itemIds
     */
    int[] getUserCommandList();
}
