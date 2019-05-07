package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Sdw
 */
public class ExMPCCPartymasterList extends IClientOutgoingPacket {
    private final Set<String> _leadersName;

    public ExMPCCPartymasterList(Set<String> leadersName) {
        _leadersName = leadersName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MPCC_PARTYMASTER_LIST.writeId(packet);

        packet.putInt(_leadersName.size());
        _leadersName.forEach(name -> writeString(name, packet));
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + _leadersName.size() * 2  + _leadersName.stream().mapToInt(l -> l.length() * 2).sum();
    }
}
