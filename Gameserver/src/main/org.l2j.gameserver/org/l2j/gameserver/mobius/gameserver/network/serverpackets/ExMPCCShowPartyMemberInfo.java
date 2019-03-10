package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author chris_00
 */
public class ExMPCCShowPartyMemberInfo extends IClientOutgoingPacket {
    private final L2Party _party;

    public ExMPCCShowPartyMemberInfo(L2Party party) {
        _party = party;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MPCCSHOW_PARTY_MEMBER_INFO.writeId(packet);

        packet.putInt(_party.getMemberCount());
        for (L2PcInstance pc : _party.getMembers()) {
            writeString(pc.getName(), packet);
            packet.putInt(pc.getObjectId());
            packet.putInt(pc.getClassId().getId());
        }
    }
}
