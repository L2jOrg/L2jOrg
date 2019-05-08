package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExTacticalSign extends IClientOutgoingPacket {
    private final L2Character _target;
    private final int _tokenId;

    public ExTacticalSign(L2Character target, int tokenId) {
        _target = target;
        _tokenId = tokenId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_TACTICAL_SIGN.writeId(packet);

        packet.putInt(_target.getObjectId());
        packet.putInt(_tokenId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}