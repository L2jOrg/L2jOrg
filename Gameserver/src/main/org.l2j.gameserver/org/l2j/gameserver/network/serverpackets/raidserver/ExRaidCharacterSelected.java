package org.l2j.gameserver.network.serverpackets.raidserver;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExRaidCharacterSelected extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_RAID_CHARACTER_SELECTED, buffer );
        var player = client.getPlayer();
        buffer.writeInt(player.getX());
        buffer.writeInt(player.getY());
        buffer.writeInt(player.getZ());
        buffer.writeInt(true); // raid server ?
        buffer.writeBytes(new byte[84]);
    }
}
