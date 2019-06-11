package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.ExIncomingPackets;
import org.l2j.gameserver.network.InvalidDataPacketException;

/**
 * @author Nos
 */
public class ExPacket extends IClientIncomingPacket {
    // private static final Logger LOGGER = LoggerFactory.getLogger(ExPacket.class);

    private
    ExIncomingPackets _exIncomingPacket;
    private IClientIncomingPacket _exPacket;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int exPacketId = readShort() & 0xFFFF;
        if (exPacketId >= ExIncomingPackets.PACKET_ARRAY.length) {
            throw new InvalidDataPacketException();
        }

        _exIncomingPacket = ExIncomingPackets.PACKET_ARRAY[exPacketId];
        if (_exIncomingPacket == null) {
            throw new InvalidDataPacketException();
        }

        _exPacket = _exIncomingPacket.newIncomingPacket();
        _exPacket.read();
    }

    @Override
    public void runImpl() {
        if (!_exIncomingPacket.getConnectionStates().contains(client.getConnectionState())) {
            return;
        }
        _exPacket.run();
    }
}
