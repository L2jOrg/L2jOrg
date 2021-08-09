package org.l2j.gameserver.network.serverpackets.bless;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOpenBlessOptionScroll extends ServerPacket {

    private final int itemId;

    public ExOpenBlessOptionScroll(int itemId) {
        this.itemId = itemId;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_OPEN_BLESS_OPTION_SCROLL, buffer );
        buffer.writeInt(this.itemId);
    }
}
