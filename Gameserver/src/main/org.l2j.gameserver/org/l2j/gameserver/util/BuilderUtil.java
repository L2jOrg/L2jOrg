package org.l2j.gameserver.util;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
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
    public static void sendSysMessage(L2PcInstance player, String message) {
        if (Config.GM_STARTUP_BUILDER_HIDE) {
            player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "SYS", message));
        } else {
            player.sendMessage(message);
        }
    }

    /**
     * Sends builder html message to the player.
     *
     * @param player
     * @param message
     */
    public static void sendHtmlMessage(L2PcInstance player, String message) {
        player.sendPacket(new CreatureSay(0, ChatType.GENERAL, "HTML", message));
    }

    /**
     * Changes player's hiding state.
     *
     * @param player
     * @param hide
     * @return {@code true} if hide state was changed, otherwise {@code false}
     */
    public static boolean setHiding(L2PcInstance player, boolean hide) {
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
