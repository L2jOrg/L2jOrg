package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author chris_00
 */
public class ExMPCCPartyInfoUpdate extends IClientOutgoingPacket {
    private final int _mode;
    private final int _LeaderOID;
    private final int _memberCount;
    private final String _name;

    /**
     * @param party
     * @param mode  0 = Remove, 1 = Add
     */
    public ExMPCCPartyInfoUpdate(L2Party party, int mode) {
        _name = party.getLeader().getName();
        _LeaderOID = party.getLeaderObjectId();
        _memberCount = party.getMemberCount();
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MPCCPARTY_INFO_UPDATE.writeId(packet);

        writeString(_name, packet);
        packet.putInt(_LeaderOID);
        packet.putInt(_memberCount);
        packet.putInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
    }
}
