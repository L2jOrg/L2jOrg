package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExUserInfoCubic extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExUserInfoCubic(L2PcInstance cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_USER_INFO_CUBIC.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putShort((short) _activeChar.getCubics().size());

        _activeChar.getCubics().keySet().forEach(key -> packet.putShort(key.shortValue()));

        packet.putInt(_activeChar.getAgathionId());
    }
}
