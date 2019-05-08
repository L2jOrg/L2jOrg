package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowAdventurerGuideBook extends IClientOutgoingPacket {
    public static final ExShowAdventurerGuideBook STATIC_PACKET = new ExShowAdventurerGuideBook();

    private ExShowAdventurerGuideBook() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_ADVENTURER_GUIDE_BOOK.writeId(packet);

    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
