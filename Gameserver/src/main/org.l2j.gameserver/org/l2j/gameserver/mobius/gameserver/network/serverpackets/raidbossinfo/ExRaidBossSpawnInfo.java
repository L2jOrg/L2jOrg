package org.l2j.gameserver.mobius.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Mobius
 */
public class ExRaidBossSpawnInfo extends IClientOutgoingPacket {
    private final List<Integer> _bossIds;

    public ExRaidBossSpawnInfo(List<Integer> bossIds) {
        _bossIds = bossIds;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RAID_BOSS_SPAWN_INFO.writeId(packet);

        packet.putInt(_bossIds.size()); // alive count
        for (int id : _bossIds) // alive ids
        {
            packet.putInt(id);
        }
    }
}
