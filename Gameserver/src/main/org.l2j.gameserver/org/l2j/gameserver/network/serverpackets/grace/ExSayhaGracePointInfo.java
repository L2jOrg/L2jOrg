package org.l2j.gameserver.network.serverpackets.grace;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSayhaGracePointInfo extends ServerPacket {

    private final int _sayhaPoints;

    public ExSayhaGracePointInfo(int sayhaPoints) {
        _sayhaPoints = sayhaPoints;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        {
            writeId(ServerExPacketId.EX_VITAL_EX_INFO, buffer );

            buffer.writeInt(_sayhaPoints);
        }
    }
}
