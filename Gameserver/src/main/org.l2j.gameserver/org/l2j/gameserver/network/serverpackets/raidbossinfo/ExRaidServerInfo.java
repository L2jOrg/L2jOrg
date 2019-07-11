package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExRaidServerInfo extends ServerPacket {
    public ExRaidServerInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_RAID_SERVER_INFO);
    }

}
