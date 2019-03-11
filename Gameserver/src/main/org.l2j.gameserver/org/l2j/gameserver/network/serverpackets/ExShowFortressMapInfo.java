package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.model.L2Spawn;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * TODO: Rewrite!!!!!!
 *
 * @author KenM
 */
public class ExShowFortressMapInfo extends IClientOutgoingPacket {
    private final Fort _fortress;

    public ExShowFortressMapInfo(Fort fortress) {
        _fortress = fortress;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_FORTRESS_MAP_INFO.writeId(packet);

        packet.putInt(_fortress.getResidenceId());
        packet.putInt(_fortress.getSiege().isInProgress() ? 1 : 0); // fortress siege status
        packet.putInt(_fortress.getFortSize()); // barracks count

        final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortress.getResidenceId());
        if ((commanders != null) && (commanders.size() != 0) && _fortress.getSiege().isInProgress()) {
            switch (commanders.size()) {
                case 3: {
                    for (FortSiegeSpawn spawn : commanders) {
                        if (isSpawned(spawn.getId())) {
                            packet.putInt(0);
                        } else {
                            packet.putInt(1);
                        }
                    }
                    break;
                }
                case 4: // TODO: change 4 to 5 once control room supported
                {
                    int count = 0;
                    for (FortSiegeSpawn spawn : commanders) {
                        count++;
                        if (count == 4) {
                            packet.putInt(1); // TODO: control room emulated
                        }
                        if (isSpawned(spawn.getId())) {
                            packet.putInt(0);
                        } else {
                            packet.putInt(1);
                        }
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < _fortress.getFortSize(); i++) {
                packet.putInt(0);
            }
        }
    }

    /**
     * @param npcId
     * @return
     */
    private boolean isSpawned(int npcId) {
        boolean ret = false;
        for (L2Spawn spawn : _fortress.getSiege().getCommanders()) {
            if (spawn.getId() == npcId) {
                ret = true;
                break;
            }
        }
        return ret;
    }
}
