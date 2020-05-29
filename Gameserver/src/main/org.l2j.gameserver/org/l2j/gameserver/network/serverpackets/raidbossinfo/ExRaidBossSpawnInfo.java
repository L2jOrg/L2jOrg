package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Map;

/**
 * @author Mobius
 */
public class ExRaidBossSpawnInfo extends ServerPacket {
    private final Map<Integer, Integer> _bossIds;

    public ExRaidBossSpawnInfo(Map<Integer, Integer> bossIds) {
        _bossIds = bossIds;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RAID_BOSS_SPAWN_INFO);

        writeInt(_bossIds.size()); // alive count
        for(Map.Entry<Integer, Integer> boss : _bossIds.entrySet()) {
            writeInt(boss.getKey()); // boss id
            writeInt(boss.getValue()); // boss state: 1 -> alive : 0 -> dead : 2 -> in battle
            writeInt(0); // unknown
        }
    }

}
