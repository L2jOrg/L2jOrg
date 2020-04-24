package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RAID_BOSS_SPAWN_INFO);

        writeInt(_bossIds.size()); // alive count
        _bossIds.forEach(this::writeInt);  // alive ids
    }

}
