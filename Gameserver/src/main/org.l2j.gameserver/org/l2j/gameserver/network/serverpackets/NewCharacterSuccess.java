package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public final class NewCharacterSuccess extends ServerPacket {

    public static final NewCharacterSuccess STATIC_PACKET = new NewCharacterSuccess();

    private NewCharacterSuccess() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NEW_CHARACTER_SUCCESS);
    }

}
