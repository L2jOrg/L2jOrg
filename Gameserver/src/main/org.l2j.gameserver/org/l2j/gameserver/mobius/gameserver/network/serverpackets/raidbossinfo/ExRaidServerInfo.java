package org.l2j.gameserver.mobius.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExRaidServerInfo extends IClientOutgoingPacket {
    public ExRaidServerInfo() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RAID_SERVER_INFO.writeId(packet);
    }
}
