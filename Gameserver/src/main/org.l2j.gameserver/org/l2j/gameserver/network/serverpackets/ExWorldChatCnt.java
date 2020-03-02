package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ChatSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class ExWorldChatCnt extends ServerPacket {
    private final int worldChatCount;

    public ExWorldChatCnt(Player activeChar) {
        worldChatCount = canUseWorldChat(activeChar) ? Math.max(activeChar.getWorldChatPoints() - activeChar.getWorldChatUsed(), 0) : 0;
    }

    private boolean canUseWorldChat(Player activeChar) {
        return activeChar.getLevel() >= getSettings(ChatSettings.class).worldChatMinLevel()|| activeChar.getVipTier() > 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_WORLD_CHAT_CNT);
        writeInt(worldChatCount);
    }

}
