package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_TACTICAL_SIGN);

        writeInt(_target.getObjectId());
        writeInt(_tokenId);
    }

}