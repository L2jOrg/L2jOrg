package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class TargetUnselected extends IClientOutgoingPacket {
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param character
     */
    public TargetUnselected(L2Character character) {
        _targetObjId = character.getObjectId();
        _x = character.getX();
        _y = character.getY();
        _z = character.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TARGET_UNSELECTED.writeId(packet);

        packet.putInt(_targetObjId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(0x00); // ??
    }
}
