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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Community Board handler.
 *
 * @author Zoey76
 * @author JoeAlisson
 */
public final class CommunityBoardHandler implements IHandler<IParseBoardHandler, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityBoardHandler.class);

    private final Map<String, IParseBoardHandler> handlers = new HashMap<>();
    private final IntMap<String> bypasses = new CHashIntMap<>();

    private CommunityBoardHandler() {
    }

    @Override
    public void registerHandler(IParseBoardHandler handler) {
        for (String cmd : handler.getCommunityBoardCommands()) {
            handlers.put(cmd.toLowerCase(), handler);
        }
    }

    @Override
    public synchronized void removeHandler(IParseBoardHandler handler) {
        for (String cmd : handler.getCommunityBoardCommands()) {
            handlers.remove(cmd.toLowerCase());
        }
    }

    @Override
    public IParseBoardHandler getHandler(String cmd) {
        return handlers.get(cmd.toLowerCase());
    }

    @Override
    public int size() {
        return handlers.size();
    }

    /**
     * Verifies if the string is a registered community board command.
     *
     * @param cmd the command to verify
     * @return {@code true} if the command has been registered, {@code false} otherwise
     */
    public boolean isCommunityBoardCommand(String cmd) {
        var whitespaceIndex = cmd.indexOf(" ");
        if(whitespaceIndex < 0){
            return nonNull(getHandler(cmd));
        }
        return nonNull(getHandler(cmd.substring(0, whitespaceIndex)));
    }

    /**
     * Parses a community board command.
     *
     * @param command the command
     * @param player  the player
     */
    public void handleParseCommand(String command, Player player) {
        if (isNull(player) || isNullOrEmpty(command)) {
            return;
        }

        if (!Config.ENABLE_COMMUNITY_BOARD) {
            player.sendPacket(SystemMessageId.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
            return;
        }

        final var tokens = new StringTokenizer(command);

        final var cb = getHandler(tokens.nextToken());
        if (isNull(cb)) {
            LOGGER.warn("Couldn't find parse handler for command {}!", command);
            return;
        }

        cb.parseCommunityBoardCommand(command, tokens, player);
        addBypass(player, cb.name(), command);
    }

    /**
     * Writes a command into the client.
     *
     * @param player the player
     * @param url    the command URL
     * @param arg1   the first argument
     * @param arg2   the second argument
     * @param arg3   the third argument
     * @param arg4   the fourth argument
     * @param arg5   the fifth argument
     */
    public void handleWriteCommand(Player player, String url, String arg1, String arg2, String arg3, String arg4, String arg5) {
        if (player == null) {
            return;
        }

        if (!Config.ENABLE_COMMUNITY_BOARD) {
            player.sendPacket(SystemMessageId.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
            return;
        }

        final var cb = getHandler(url);
        if (isNull(cb)) {
            LOGGER.warn("Couldn't find write handler for command {}!", url);
            return;
        }

        if (!(cb instanceof IWriteBoardHandler writable)) {
            LOGGER.warn("{} doesn't implement write!",  cb.getClass().getSimpleName());
        } else {
            writable.writeCommunityBoardCommand(player, arg1, arg2, arg3, arg4, arg5);
        }
    }

    /**
     * Sets the last bypass used by the player.
     *
     * @param player the player
     * @param title  the title
     * @param bypass the bypass
     */
    public void addBypass(Player player, String title, String bypass) {
        bypasses.put(player.getObjectId(), title + "&" + bypass);
    }

    /**
     * Removes the last bypass used by the player.
     *
     * @param player the player
     * @return the last bypass used
     */
    public String removeBypass(Player player) {
        return bypasses.remove(player.getObjectId());
    }

    /**
     * Separates and send an HTML into multiple packets, to display into the community board.<br>
     * The limit is 16383 characters.
     *
     * @param html   the HTML to send
     * @param player the player
     */
    public static void separateAndSend(String html, Player player) {
        GameUtils.sendCBHtml(player, html);
    }

    public static CommunityBoardHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CommunityBoardHandler INSTANCE = new CommunityBoardHandler();
    }
}
