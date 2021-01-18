package org.l2j.gameserver.network.serverpackets.randomcraft;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCraftRandomLockSlot extends ServerPacket {
    public ExCraftRandomLockSlot()
    {
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_CRAFT_RANDOM_LOCK_SLOT, buffer );
        buffer.writeByte(0x00);
    }
}
