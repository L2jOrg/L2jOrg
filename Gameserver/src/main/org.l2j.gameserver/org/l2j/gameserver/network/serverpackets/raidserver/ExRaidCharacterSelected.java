package org.l2j.gameserver.network.serverpackets.raidserver;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExRaidCharacterSelected extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_RAID_CHARACTER_SELECTED);
        var player = client.getPlayer();
        writeInt(player.getX());
        writeInt(player.getY());
        writeInt(player.getZ());
        writeInt(true); // raid server ?
        writeBytes(new byte[84]);
    }
}
