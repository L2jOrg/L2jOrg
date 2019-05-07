package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ChairSit extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final int _staticObjectId;

    /**
     * @param player
     * @param staticObjectId
     */
    public ChairSit(L2PcInstance player, int staticObjectId) {
        _activeChar = player;
        _staticObjectId = staticObjectId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHAIR_SIT.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_staticObjectId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}
