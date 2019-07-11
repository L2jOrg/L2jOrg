package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Yme
 */
public class PetStatusShow extends ServerPacket {
    private final int _summonType;
    private final int _summonObjectId;

    public PetStatusShow(Summon summon) {
        _summonType = summon.getSummonType();
        _summonObjectId = summon.getObjectId();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PET_STATUS_SHOW);

        writeInt(_summonType);
        writeInt(_summonObjectId);
    }

}
