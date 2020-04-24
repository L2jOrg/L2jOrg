package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class Ex2ndPasswordCheck extends ServerPacket {
    // TODO: Enum
    public static final int PASSWORD_NEW = 0x00;
    public static final int PASSWORD_PROMPT = 0x01;
    public static final int PASSWORD_OK = 0x02;

    private final int _windowType;

    public Ex2ndPasswordCheck(int windowType) {
        _windowType = windowType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_2ND_PASSWORD_CHECK);

        writeInt(_windowType);
        writeInt(0x00);
    }

}
