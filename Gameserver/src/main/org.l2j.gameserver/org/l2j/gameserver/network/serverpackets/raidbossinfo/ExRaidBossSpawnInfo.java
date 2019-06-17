package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author Mobius
 */
public class ExRaidBossSpawnInfo extends ServerPacket {
    private final List<Integer> _bossIds;

    public ExRaidBossSpawnInfo(List<Integer> bossIds) {
        _bossIds = bossIds;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_RAID_BOSS_SPAWN_INFO);

        writeInt(_bossIds.size()); // alive count
        for (int id : _bossIds) // alive ids
        {
            writeInt(id);
        }
    }

}
