package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author chris_00
 */
public class ExMPCCPartyInfoUpdate extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_MPCCPARTY_INFO_UPDATE);

        writeString(_name);
        writeInt(_LeaderOID);
        writeInt(_memberCount);
        writeInt(_mode); // mode 0 = Remove Party, 1 = AddParty, maybe more...
    }

}
