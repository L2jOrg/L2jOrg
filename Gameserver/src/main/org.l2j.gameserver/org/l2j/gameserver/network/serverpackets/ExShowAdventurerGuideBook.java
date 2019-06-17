package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowAdventurerGuideBook extends ServerPacket {
    public static final ExShowAdventurerGuideBook STATIC_PACKET = new ExShowAdventurerGuideBook();

    private ExShowAdventurerGuideBook() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_ADVENTURER_GUIDE_BOOK);

    }

}
