package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExWorldChatCnt extends IClientOutgoingPacket {
    private final int _points;

    public ExWorldChatCnt(L2PcInstance activeChar) {
        _points = activeChar.getLevel() < Config.WORLD_CHAT_MIN_LEVEL ? 0 : Math.max(activeChar.getWorldChatPoints() - activeChar.getWorldChatUsed(), 0);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_WORLD_CHAT_CNT.writeId(packet);

        packet.putInt(_points);
    }
}
