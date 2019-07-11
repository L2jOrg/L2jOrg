package org.l2j.gameserver.network.serverpackets.commission;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
@StaticPacket
public class ExResponseCommissionRegister extends ServerPacket {
    public static final ExResponseCommissionRegister SUCCEED = new ExResponseCommissionRegister(1);
    public static final ExResponseCommissionRegister FAILED = new ExResponseCommissionRegister(0);

    private final int _result;

    private ExResponseCommissionRegister(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_RESPONSE_COMMISSION_REGISTER);

        writeInt(_result);
    }

}
