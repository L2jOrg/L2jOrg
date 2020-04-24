package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Luca Baldi
 */
@StaticPacket
public class ExShowQuestInfo extends ServerPacket {
    public static final ExShowQuestInfo STATIC_PACKET = new ExShowQuestInfo();

    private ExShowQuestInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_QUEST_INFO);

    }

}
