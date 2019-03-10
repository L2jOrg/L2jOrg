package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_2ND_PASSWORD_CHECK.writeId(packet);

        packet.putInt(_windowType);
        packet.putInt(0x00);
    }
}
