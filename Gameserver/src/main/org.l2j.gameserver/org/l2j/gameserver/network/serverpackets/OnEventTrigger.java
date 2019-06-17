package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik, UnAfraid
 */
public class OnEventTrigger extends ServerPacket {
    private final int _emitterId;
    private final int _enabled;

    public OnEventTrigger(int emitterId, boolean enabled) {
        _emitterId = emitterId;
        _enabled = enabled ? 1 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EVENT_TRIGGER);

        writeInt(_emitterId);
        writeByte((byte) _enabled);
    }

}