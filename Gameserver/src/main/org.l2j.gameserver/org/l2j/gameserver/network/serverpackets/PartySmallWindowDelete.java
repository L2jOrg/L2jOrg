package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowDelete extends ServerPacket {
    private final L2PcInstance _member;

    public PartySmallWindowDelete(L2PcInstance member) {
        _member = member;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE);

        writeInt(_member.getObjectId());
        writeString(_member.getName());
    }

}
