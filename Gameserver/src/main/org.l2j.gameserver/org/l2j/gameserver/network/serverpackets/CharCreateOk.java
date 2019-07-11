package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class CharCreateOk extends ServerPacket {
    public static final CharCreateOk STATIC_PACKET = new CharCreateOk();

    private CharCreateOk() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHARACTER_CREATE_SUCCESS);

        writeInt(0x01);
    }

}
