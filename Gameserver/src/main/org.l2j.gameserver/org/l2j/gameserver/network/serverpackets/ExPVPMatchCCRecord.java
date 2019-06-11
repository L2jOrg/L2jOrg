package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Mobius
 */
public class ExPVPMatchCCRecord extends IClientOutgoingPacket {
    public static final int INITIALIZE = 0;
    public static final int UPDATE = 1;
    public static final int FINISH = 2;

    private final int _state;
    private final Map<L2PcInstance, Integer> _players;

    public ExPVPMatchCCRecord(int state, Map<L2PcInstance, Integer> players) {
        _state = state;
        _players = players;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PVP_MATCH_CCRECORD);
        writeInt(_state); // 0 - initialize, 1 - update, 2 - finish
        writeInt(_players.size());
        for (Entry<L2PcInstance, Integer> entry : _players.entrySet()) {
            writeString(entry.getKey().getName());
            writeInt(entry.getValue());
        }
    }

}
