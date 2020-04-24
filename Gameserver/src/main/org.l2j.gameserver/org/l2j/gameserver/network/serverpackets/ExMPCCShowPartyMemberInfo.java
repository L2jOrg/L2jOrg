package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author chris_00
 */
public class ExMPCCShowPartyMemberInfo extends ServerPacket {
    private final Party _party;

    public ExMPCCShowPartyMemberInfo(Party party) {
        _party = party;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_MPCC_SHOW_PARTY_MEMBERS_INFO);

        writeInt(_party.getMemberCount());
        for (Player pc : _party.getMembers()) {
            writeString(pc.getName());
            writeInt(pc.getObjectId());
            writeInt(pc.getClassId().getId());
        }
    }

}
