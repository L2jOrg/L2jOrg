package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowDelete extends ServerPacket {
    private final Player _member;

    public PartySmallWindowDelete(Player member) {
        _member = member;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE);

        writeInt(_member.getObjectId());
        writeString(_member.getName());
    }

}
