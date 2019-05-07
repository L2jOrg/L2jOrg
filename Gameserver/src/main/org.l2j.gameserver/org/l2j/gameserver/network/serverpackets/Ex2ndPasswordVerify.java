package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author mrTJO
 */
public class Ex2ndPasswordVerify extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int PASSWORD_OK = 0x00;
    public static final int PASSWORD_WRONG = 0x01;
    public static final int PASSWORD_BAN = 0x02;

    private final int _wrongTentatives;
    private final int _mode;

    public Ex2ndPasswordVerify(int mode, int wrongTentatives) {
        _mode = mode;
        _wrongTentatives = wrongTentatives;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_2ND_PASSWORD_VERIFY.writeId(packet);

        packet.putInt(_mode);
        packet.putInt(_wrongTentatives);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}
