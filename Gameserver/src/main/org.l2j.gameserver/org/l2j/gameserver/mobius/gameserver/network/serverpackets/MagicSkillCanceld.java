package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class MagicSkillCanceld extends IClientOutgoingPacket {
    private final int _objectId;

    public MagicSkillCanceld(int objectId) {
        _objectId = objectId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MAGIC_SKILL_CANCELED.writeId(packet);

        packet.putInt(_objectId);
    }
}
