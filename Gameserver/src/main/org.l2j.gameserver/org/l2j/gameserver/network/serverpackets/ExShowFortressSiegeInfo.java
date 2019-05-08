package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * TODO: Rewrite!!!
 *
 * @author KenM
 */
public class ExShowFortressSiegeInfo extends IClientOutgoingPacket {
    private final int _fortId;
    private final int _size;
    private final int _csize;
    private final int _csize2;

    public ExShowFortressSiegeInfo(Fort fort) {
        _fortId = fort.getResidenceId();
        _size = fort.getFortSize();
        final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortId);
        _csize = ((commanders == null) ? 0 : commanders.size());
        _csize2 = fort.getSiege().getCommanders().size();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_FORTRESS_SIEGE_INFO.writeId(packet);

        packet.putInt(_fortId); // Fortress Id
        packet.putInt(_size); // Total Barracks Count
        if (_csize > 0) {
            switch (_csize) {
                case 3: {
                    switch (_csize2) {
                        case 0: {
                            packet.putInt(0x03);
                            break;
                        }
                        case 1: {
                            packet.putInt(0x02);
                            break;
                        }
                        case 2: {
                            packet.putInt(0x01);
                            break;
                        }
                        case 3: {
                            packet.putInt(0x00);
                            break;
                        }
                    }
                    break;
                }
                case 4: // TODO: change 4 to 5 once control room supported
                {
                    switch (_csize2) {
                        // TODO: once control room supported, update packet.putInt(0x0x) to support 5th room
                        case 0: {
                            packet.putInt(0x05);
                            break;
                        }
                        case 1: {
                            packet.putInt(0x04);
                            break;
                        }
                        case 2: {
                            packet.putInt(0x03);
                            break;
                        }
                        case 3: {
                            packet.putInt(0x02);
                            break;
                        }
                        case 4: {
                            packet.putInt(0x01);
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < _size; i++) {
                packet.putInt(0x00);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 17 + _size * 4;
    }
}
