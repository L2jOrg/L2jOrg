package org.l2j.gameserver.handler;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Interface for chat handlers
 *
 * @author durgus
 */
public interface IChatHandler {
    /**
     * Handles a specific type of chat messages
     *
     * @param type
     * @param player
     * @param target
     * @param text
     */
    void handleChat(ChatType type, Player player, String target, String text);

    /**
     * Returns a list of all chat types registered to this handler
     *
     * @return
     */
    ChatType[] getChatTypeList();
}