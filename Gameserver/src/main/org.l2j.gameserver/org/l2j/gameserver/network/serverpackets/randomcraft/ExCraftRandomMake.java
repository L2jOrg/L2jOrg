package org.l2j.gameserver.network.serverpackets.randomcraft;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCraftRandomMake extends ServerPacket {
    private final int _itemId;
    private final long _itemCount;

    public ExCraftRandomMake(int itemId, long itemCount)
    {
        _itemId = itemId;
        _itemCount = itemCount;
    }
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_CRAFT_RANDOM_MAKE, buffer );
        buffer.writeByte(0x00); // Close window
        buffer.writeShort(0x0F); // Unknown
        buffer.writeInt(_itemId);
        buffer.writeLong(_itemCount);
        buffer.writeByte(0x00); // Enchantment level
    }
}
