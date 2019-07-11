package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author Sdw
 */
public class ExMPCCPartymasterList extends ServerPacket {
    private final Set<String> _leadersName;

    public ExMPCCPartymasterList(Set<String> leadersName) {
        _leadersName = leadersName;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_MPCC_PARTYMASTER_LIST);

        writeInt(_leadersName.size());
        _leadersName.forEach(this::writeString);
    }

}
