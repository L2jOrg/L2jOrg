package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Mobius
 */
public class ExPVPMatchCCRecord extends ServerPacket {
    public static final int INITIALIZE = 0;
    public static final int UPDATE = 1;
    public static final int FINISH = 2;

    private final int _state;
    private final Map<Player, Integer> _players;

    public ExPVPMatchCCRecord(int state, Map<Player, Integer> players) {
        _state = state;
        _players = players;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PVPMATCH_CC_RECORD);
        writeInt(_state); // 0 - initialize, 1 - update, 2 - finish
        writeInt(_players.size());
        for (Entry<Player, Integer> entry : _players.entrySet()) {
            writeString(entry.getKey().getName());
            writeInt(entry.getValue());
        }
    }

}
