package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class Revive extends IClientOutgoingPacket {
    private final int _objectId;

    public Revive(L2Object obj) {
        _objectId = obj.getObjectId();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.REVIVE.writeId(packet);

        packet.putInt(_objectId);
    }
}
