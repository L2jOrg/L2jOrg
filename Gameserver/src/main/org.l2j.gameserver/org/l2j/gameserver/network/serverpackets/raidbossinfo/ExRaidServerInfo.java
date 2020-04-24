package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExRaidServerInfo extends ServerPacket {
    public ExRaidServerInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RAID_SERVER_INFO);
    }

}
