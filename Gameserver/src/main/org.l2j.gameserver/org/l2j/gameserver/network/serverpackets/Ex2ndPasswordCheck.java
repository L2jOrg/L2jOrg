package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class Ex2ndPasswordCheck extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int PASSWORD_NEW = 0x00;
    public static final int PASSWORD_PROMPT = 0x01;
    public static final int PASSWORD_OK = 0x02;

    private final int _windowType;

    public Ex2ndPasswordCheck(int windowType) {
        _windowType = windowType;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_2ND_PASSWORD_CHECK);

        writeInt(_windowType);
        writeInt(0x00);
    }

}
