package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * TODO: Rewrite!!!!!!
 *
 * @author KenM
 */
public class ExShowFortressMapInfo extends ServerPacket {
    private final Fort _fortress;

    public ExShowFortressMapInfo(Fort fortress) {
        _fortress = fortress;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_FORTRESS_MAP_INFO);

        writeInt(_fortress.getId());
        writeInt(_fortress.getSiege().isInProgress() ? 1 : 0); // fortress siege status
        writeInt(_fortress.getFortSize()); // barracks count

        final Collection<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortress.getId());
        if ((commanders != null) && (commanders.size() != 0) && _fortress.getSiege().isInProgress()) {
            switch (commanders.size()) {
                case 3: {
                    for (FortSiegeSpawn spawn : commanders) {
                        if (isSpawned(spawn.getId())) {
                            writeInt(0);
                        } else {
                            writeInt(1);
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
                            writeInt(1); // TODO: control room emulated
                        }
                        if (isSpawned(spawn.getId())) {
                            writeInt(0);
                        } else {
                            writeInt(1);
                        }
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < _fortress.getFortSize(); i++) {
                writeInt(0);
            }
        }
    }



    /**
     * @param npcId
     * @return
     */
    private boolean isSpawned(int npcId) {
        boolean ret = false;
        for (Spawn spawn : _fortress.getSiege().getCommanders()) {
            if (spawn.getId() == npcId) {
                ret = true;
                break;
            }
        }
        return ret;
    }
}
