package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class ExVariationCancelResult extends ServerPacket {
    public static final ExVariationCancelResult STATIC_PACKET_SUCCESS = new ExVariationCancelResult(1);
    public static final ExVariationCancelResult STATIC_PACKET_FAILURE = new ExVariationCancelResult(0);

    private final int _result;

    private ExVariationCancelResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_VARIATION_CANCEL_RESULT);

        writeInt(_result);
    }

}
