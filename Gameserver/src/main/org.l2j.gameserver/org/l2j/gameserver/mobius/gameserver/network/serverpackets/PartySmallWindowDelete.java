package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowDelete extends IClientOutgoingPacket {
    private final L2PcInstance _member;

    public PartySmallWindowDelete(L2PcInstance member) {
        _member = member;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_DELETE.writeId(packet);

        packet.putInt(_member.getObjectId());
        writeString(_member.getName(), packet);
    }
}
