package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PetDelete extends ServerPacket {
    private final int _petType;
    private final int _petObjId;

    public PetDelete(int petType, int petObjId) {
        _petType = petType; // Summon Type
        _petObjId = petObjId; // objectId
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PET_DELETE);

        writeInt(_petType);
        writeInt(_petObjId);
    }

}
