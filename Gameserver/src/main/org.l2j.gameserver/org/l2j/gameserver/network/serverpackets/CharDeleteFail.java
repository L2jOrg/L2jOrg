package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class CharDeleteFail extends IClientOutgoingPacket {
    private final int _error;

    public CharDeleteFail(CharacterDeleteFailType type) {
        _error = type.ordinal();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.CHARACTER_DELETE_FAIL);

        writeInt(_error);
    }

}
