package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExWorldChatCnt extends ServerPacket {
    private final int worldChatCount;

    public ExWorldChatCnt(L2PcInstance activeChar) {
        worldChatCount = canUseWorldChat(activeChar) ? Math.max(activeChar.getWorldChatPoints() - activeChar.getWorldChatUsed(), 0) : 0;
    }

    private boolean canUseWorldChat(L2PcInstance activeChar) {
        return activeChar.getLevel() >= Config.WORLD_CHAT_MIN_LEVEL || activeChar.getVipTier() > 0;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_WORLD_CHAT_CNT);
        writeInt(worldChatCount);
    }

}
