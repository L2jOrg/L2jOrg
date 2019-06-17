package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class MagicSkillCanceld extends ServerPacket {
    private final int _objectId;

    public MagicSkillCanceld(int objectId) {
        _objectId = objectId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MAGIC_SKILL_CANCELED);

        writeInt(_objectId);
    }

}
