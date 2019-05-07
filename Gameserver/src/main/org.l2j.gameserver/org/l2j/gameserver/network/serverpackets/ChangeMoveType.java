package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ChangeMoveType extends IClientOutgoingPacket {
    public static final int WALK = 0;
    public static final int RUN = 1;

    private final int _charObjId;
    private final boolean _running;

    public ChangeMoveType(L2Character character) {
        _charObjId = character.getObjectId();
        _running = character.isRunning();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHANGE_MOVE_TYPE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_running ? RUN : WALK);
        packet.putInt(0); // c2
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}
