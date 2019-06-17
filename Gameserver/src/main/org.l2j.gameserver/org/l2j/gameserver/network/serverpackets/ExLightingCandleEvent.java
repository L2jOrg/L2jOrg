package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class ExLightingCandleEvent extends ServerPacket {

    public static final ExLightingCandleEvent ENABLED = new ExLightingCandleEvent((short) 1);
    public static final ExLightingCandleEvent DISABLED = new ExLightingCandleEvent((short) 1);

    private short enabled;

    private ExLightingCandleEvent(short enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_LIGHTING_CANDLE_EVENT);
        writeShort(enabled);
    }
}
