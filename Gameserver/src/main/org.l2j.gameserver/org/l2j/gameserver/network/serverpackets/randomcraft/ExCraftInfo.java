package org.l2j.gameserver.network.serverpackets.randomcraft;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerRandomCraft;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCraftInfo  extends ServerPacket {
    private final Player _player;

    public ExCraftInfo(Player player)
    {
        _player = player;
    }
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_CRAFT_INFO, buffer );
        final PlayerRandomCraft rc = _player.getRandomCraft();
        buffer.writeInt(rc.getFullCraftPoints()); // Full points owned
        buffer.writeInt(rc.getCraftPoints()); // Craft Points (10k = 1%)
        buffer.writeByte(rc.isSayhaRoll() ? 0x01 : 0x00); // Will get sayha?
    }
}
