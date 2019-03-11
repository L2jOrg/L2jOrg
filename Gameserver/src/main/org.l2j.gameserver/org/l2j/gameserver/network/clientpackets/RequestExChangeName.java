package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExNeedToChangeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Reply for {@link ExNeedToChangeName}
 *
 * @author JIV
 */
public class RequestExChangeName extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExChangeName.class);
    private String _newName;
    private int _type;
    private int _charSlot;

    @Override
    public void readImpl(ByteBuffer packet) {
        _type = packet.getInt();
        _newName = readString(packet);
        _charSlot = packet.getInt();
    }

    @Override
    public void runImpl() {
        LOGGER.info("Recieved " + getClass().getSimpleName() + " name: " + _newName + " type: " + _type + " CharSlot: " + _charSlot);
    }
}
