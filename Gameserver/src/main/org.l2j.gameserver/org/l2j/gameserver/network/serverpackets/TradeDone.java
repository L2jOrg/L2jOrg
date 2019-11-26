package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
@StaticPacket
public class TradeDone extends ServerPacket {

    public static final TradeDone CANCELLED = new TradeDone(false);
    public static final TradeDone COMPLETED = new TradeDone(true);

    private final boolean completed;

    private TradeDone(boolean completed) {
        this.completed = completed;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_DONE);
        writeInt(completed);
    }

}
