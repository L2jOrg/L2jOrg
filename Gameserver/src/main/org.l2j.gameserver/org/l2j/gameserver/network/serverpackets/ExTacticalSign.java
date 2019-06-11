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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_TACTICAL_SIGN);

        writeInt(_target.getObjectId());
        writeInt(_tokenId);
    }

}