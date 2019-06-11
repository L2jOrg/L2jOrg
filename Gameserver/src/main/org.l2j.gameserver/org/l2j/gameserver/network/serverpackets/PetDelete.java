package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PetDelete extends IClientOutgoingPacket {
    private final int _petType;
    private final int _petObjId;

    public PetDelete(int petType, int petObjId) {
        _petType = petType; // Summon Type
        _petObjId = petObjId; // objectId
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PET_DELETE);

        writeInt(_petType);
        writeInt(_petObjId);
    }

}
