package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExTacticalSign extends ServerPacket {
    private final Creature _target;
    private final int _tokenId;

    public ExTacticalSign(Creature target, int tokenId) {
        _target = target;
        _tokenId = tokenId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_TACTICAL_SIGN);

        writeInt(_target.getObjectId());
        writeInt(_tokenId);
    }

}