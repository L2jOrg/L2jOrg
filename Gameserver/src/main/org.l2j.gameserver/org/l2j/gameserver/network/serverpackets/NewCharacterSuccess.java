package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public final class NewCharacterSuccess extends IClientOutgoingPacket {

    public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();

    private NewCharacterSuccess() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NEW_CHARACTER_SUCCESS.writeId(packet);
    }
}
