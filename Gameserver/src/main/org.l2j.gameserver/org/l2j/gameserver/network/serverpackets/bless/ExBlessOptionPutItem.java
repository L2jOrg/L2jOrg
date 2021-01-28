package org.l2j.gameserver.network.serverpackets.bless;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBlessOptionPutItem extends ServerPacket {

    private final boolean result;

    public ExBlessOptionPutItem(boolean result) {
        this.result = result;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_BLESS_OPTION_PUT_ITEM, buffer );
        buffer.writeByte(this.result);
    }
}
