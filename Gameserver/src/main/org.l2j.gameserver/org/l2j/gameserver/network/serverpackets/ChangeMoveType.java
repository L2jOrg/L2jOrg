package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ChangeMoveType extends ServerPacket {
    public static final int WALK = 0;
    public static final int RUN = 1;

    private final int _charObjId;
    private final boolean _running;

    public ChangeMoveType(Creature character) {
        _charObjId = character.getObjectId();
        _running = character.isRunning();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHANGE_MOVE_TYPE);

        writeInt(_charObjId);
        writeInt(_running ? RUN : WALK);
        writeInt(0); // c2
    }

}
