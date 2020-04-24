package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class ExEnterWorld extends ServerPacket {
    private final int zoneIdOffsetSeconds;
    private final int epochInSeconds;

    public ExEnterWorld() {
        zoneIdOffsetSeconds = OffsetDateTime.now(ZoneId.systemDefault()).getOffset().getTotalSeconds();
        epochInSeconds = (int)((System.currentTimeMillis() / 1000) + zoneIdOffsetSeconds);
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_ENTER_WORLD);
        writeInt(epochInSeconds);
        writeInt(-zoneIdOffsetSeconds);
        writeInt(0);
        writeInt(40);
    }
}