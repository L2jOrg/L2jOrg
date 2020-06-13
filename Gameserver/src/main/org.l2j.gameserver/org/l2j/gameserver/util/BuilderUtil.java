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
package org.l2j.gameserver.util;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;

/**
 * @author lord_rex
 */
public final class BuilderUtil {
    private BuilderUtil() {
        // utility class
    }

    /**
     * Sends builder system message to the player.
     *
     * @param player
     * @param message
     */
    public static void sendSysMessage(Player player, String message) {
        if (Config.GM_STARTUP_BUILDER_HIDE) {
            player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "SYS", message));
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendSystemMessage(Player player, String message, Object... args) {
        sendSysMessage(player, String.format(message, args));
    }

    /**
     * Sends builder html message to the player.
     *
     * @param player
     * @param message
     */
    public static void sendHtmlMessage(Player player, String message) {
        player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "HTML", message));
    }

    /**
     * Changes player's hiding state.
     *
     * @param player
     * @param hide
     * @return {@code true} if hide state was changed, otherwise {@code false}
     */
    public static boolean setHiding(Player player, boolean hide) {
        if (player.isInvisible() && hide) {
            // already hiding
            return false;
        }

        if (!player.isInvisible() && !hide) {
            // already visible
            return false;
        }

        player.setSilenceMode(hide);
        player.setIsInvul(hide);
        player.setInvisible(hide);

        player.broadcastUserInfo();
        player.sendPacket(new ExUserInfoAbnormalVisualEffect(player));
        return true;
    }
}
