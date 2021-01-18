package org.l2j.gameserver.network.serverpackets.bless;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExBlessOptionEnchant extends ServerPacket {

    private final boolean _result;

    public ExBlessOptionEnchant(boolean result) {
        this._result = result;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_BLESS_OPTION_ENCHANT, buffer );
        buffer.writeByte(this._result);
    }
}