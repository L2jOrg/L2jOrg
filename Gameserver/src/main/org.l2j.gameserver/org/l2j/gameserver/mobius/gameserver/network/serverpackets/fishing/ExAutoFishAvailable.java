package org.l2j.gameserver.mobius.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author bit
 */
public class ExAutoFishAvailable extends IClientOutgoingPacket {
    public static ExAutoFishAvailable YES = new ExAutoFishAvailable(true);
    public static ExAutoFishAvailable NO = new ExAutoFishAvailable(false);

    private final boolean _available;

    private ExAutoFishAvailable(boolean available) {
        _available = available;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_AUTO_FISH_AVAILABLE.writeId(packet);
        packet.put((byte) (_available ? 1 : 0));
    }
}
