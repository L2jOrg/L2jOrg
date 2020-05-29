package org.l2j.gameserver.network.clientpackets.raidbossinfo;

import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author THOSS
 */
public class RequestRaidBossSpawnInfo extends ClientPacket {
    private Map<Integer, Integer> _bossIds = new HashMap<Integer, Integer>();

    @Override
    public void readImpl() {
        final int count = readInt();
        for (int i = 0; i < count; i++) {
            final int bossId = readInt();
            // boss state: 1 -> alive : 0 -> dead : 2 -> in battle
            if (GrandBossManager.getInstance().getBossStatus(bossId) > -1) {
                if(GrandBossManager.getInstance().getBoss(bossId) != null && GrandBossManager.getInstance().getBoss(bossId).getAggroList().size() > 0) {
                    _bossIds.put(bossId, 2);
                } else {
                    _bossIds.put(bossId, GrandBossManager.getInstance().getBossStatus(bossId));
                }
            } else {
                if(DBSpawnManager.getInstance().isDefined(bossId) && DBSpawnManager.getInstance().getNpcs().get(bossId) != null && ((Attackable) DBSpawnManager.getInstance().getNpcs().get(bossId)).getAggroList().size() > 0) {
                    _bossIds.put(bossId, 2);
                } else {
                    _bossIds.put(bossId, DBSpawnManager.getInstance().getNpcStatusId(bossId) == RaidBossStatus.ALIVE ? 1 : 0);
                }
            }
        }
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExRaidBossSpawnInfo(_bossIds));
    }
}
