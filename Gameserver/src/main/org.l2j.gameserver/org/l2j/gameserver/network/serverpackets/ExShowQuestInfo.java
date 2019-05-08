package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Luca Baldi
 */
@StaticPacket
public class ExShowQuestInfo extends IClientOutgoingPacket {
    public static final ExShowQuestInfo STATIC_PACKET = new ExShowQuestInfo();

    private ExShowQuestInfo() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_QUEST_INFO.writeId(packet);

    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
