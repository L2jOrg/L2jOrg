package org.l2j.gameserver.mobius.gameserver.network.clientpackets.raidbossinfo;

import org.l2j.gameserver.mobius.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.DBSpawnManager.DBStatusType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mobius
 */
public class RequestRaidBossSpawnInfo extends IClientIncomingPacket {
    private final List<Integer> _bossIds = new ArrayList<>();

    @Override
    public void readImpl(ByteBuffer packet) {
        final int count = packet.getInt();
        for (int i = 0; i < count; i++) {
            final int bossId = packet.getInt();
            if (DBSpawnManager.getInstance().getNpcStatusId(bossId) == DBStatusType.ALIVE) {
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
