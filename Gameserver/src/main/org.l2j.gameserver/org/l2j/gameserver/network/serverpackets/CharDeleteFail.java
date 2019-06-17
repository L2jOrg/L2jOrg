package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CharDeleteFail extends ServerPacket {
    private final int _error;

    public CharDeleteFail(CharacterDeleteFailType type) {
        _error = type.ordinal();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CHARACTER_DELETE_FAIL);

        writeInt(_error);
    }

}
