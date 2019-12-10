package org.l2j.gameserver.network.serverpackets.autoplay;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
@StaticPacket
public final class ExAutoPlayDoMacro extends ServerPacket {

    public static ExAutoPlayDoMacro STATIC = new ExAutoPlayDoMacro();

    private ExAutoPlayDoMacro() {

    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_AUTOPLAY_DO_MACRO);
        writeInt(0x114); // macro number ?
    }
}
