package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class CharCreateOk extends IClientOutgoingPacket {
    public static final CharCreateOk STATIC_PACKET = new CharCreateOk();

    private CharCreateOk() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.CHARACTER_CREATE_SUCCESS);

        writeInt(0x01);
    }

}
