package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class OnEventTrigger extends IClientOutgoingPacket {
    private final int _emitterId;
    private final int _enabled;

    public OnEventTrigger(int emitterId, boolean enabled) {
        _emitterId = emitterId;
        _enabled = enabled ? 1 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EVENT_TRIGGER.writeId(packet);

        packet.putInt(_emitterId);
        packet.put((byte) _enabled);
    }
}