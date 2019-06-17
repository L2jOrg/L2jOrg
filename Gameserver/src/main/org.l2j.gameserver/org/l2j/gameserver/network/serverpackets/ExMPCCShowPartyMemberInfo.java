package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author chris_00
 */
public class ExMPCCShowPartyMemberInfo extends ServerPacket {
    private final L2Party _party;

    public ExMPCCShowPartyMemberInfo(L2Party party) {
        _party = party;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_MPCCSHOW_PARTY_MEMBER_INFO);

        writeInt(_party.getMemberCount());
        for (L2PcInstance pc : _party.getMembers()) {
            writeString(pc.getName());
            writeInt(pc.getObjectId());
            writeInt(pc.getClassId().getId());
        }
    }

}
