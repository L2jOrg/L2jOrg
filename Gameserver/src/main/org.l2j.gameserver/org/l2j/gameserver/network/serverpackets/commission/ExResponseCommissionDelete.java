package org.l2j.gameserver.network.serverpackets.commission;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
@StaticPacket
public class ExResponseCommissionDelete extends ServerPacket {
    public static final ExResponseCommissionDelete SUCCEED = new ExResponseCommissionDelete(1);
    public static final ExResponseCommissionDelete FAILED = new ExResponseCommissionDelete(0);

    private final int _result;

    private ExResponseCommissionDelete(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_RESPONSE_COMMISSION_DELETE);

        writeInt(_result);
    }

}
