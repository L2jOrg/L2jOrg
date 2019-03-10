package org.l2j.gameserver.mobius.gameserver.network.serverpackets.adenadistribution;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExDivideAdenaDone extends IClientOutgoingPacket {
    private final boolean _isPartyLeader;
    private final boolean _isCCLeader;
    private final long _adenaCount;
    private final long _distributedAdenaCount;
    private final int _memberCount;
    private final String _distributorName;

    public ExDivideAdenaDone(boolean isPartyLeader, boolean isCCLeader, long adenaCount, long distributedAdenaCount, int memberCount, String distributorName) {
        _isPartyLeader = isPartyLeader;
        _isCCLeader = isCCLeader;
        _adenaCount = adenaCount;
        _distributedAdenaCount = distributedAdenaCount;
        _memberCount = memberCount;
        _distributorName = distributorName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DIVIDE_ADENA_DONE.writeId(packet);

        packet.put((byte) (_isPartyLeader ? 0x01 : 0x00));
        packet.put((byte) (_isCCLeader ? 0x01 : 0x00));
        packet.putInt(_memberCount);
        packet.putLong(_distributedAdenaCount);
        packet.putLong(_adenaCount);
        writeString(_distributorName, packet);
    }
}
