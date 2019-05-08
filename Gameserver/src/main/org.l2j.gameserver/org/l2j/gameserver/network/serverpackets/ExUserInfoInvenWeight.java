package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExUserInfoInvenWeight extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExUserInfoInvenWeight(L2PcInstance cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_USER_INFO_INVEN_WEIGHT.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_activeChar.getCurrentLoad());
        packet.putInt(_activeChar.getMaxLoad());
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}