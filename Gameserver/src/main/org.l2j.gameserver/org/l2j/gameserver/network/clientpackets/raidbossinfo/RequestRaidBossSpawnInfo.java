package org.l2j.gameserver.network.clientpackets.raidbossinfo;

import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mobius
 */
public class RequestRaidBossSpawnInfo extends ClientPacket {
    private final List<Integer> _bossIds = new ArrayList<>();

    @Override
    public void readImpl() {
        final int count = readInt();
        for (int i = 0; i < count; i++) {
            final int bossId = readInt();
            if (DBSpawnManager.getInstance().getNpcStatusId(bossId) == RaidBossStatus.ALIVE) {
                _bossIds.add(bossId);
            } else if (GrandBossManager.getInstance().getBossStatus(bossId) == 0) {
                _bossIds.add(bossId);
            }
        }
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExRaidBossSpawnInfo(_bossIds));
    }
}
