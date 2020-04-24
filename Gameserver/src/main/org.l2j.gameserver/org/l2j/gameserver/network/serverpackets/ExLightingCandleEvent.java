package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

@StaticPacket
public class ExLightingCandleEvent extends ServerPacket {

    public static final ExLightingCandleEvent ENABLED = new ExLightingCandleEvent((short) 1);
    public static final ExLightingCandleEvent DISABLED = new ExLightingCandleEvent((short) 1);

    private short enabled;

    private ExLightingCandleEvent(short enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_LIGHTING_CANDLE);
        writeShort(enabled);
    }
}
