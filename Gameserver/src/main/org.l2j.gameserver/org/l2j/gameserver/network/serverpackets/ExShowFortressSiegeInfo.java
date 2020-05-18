package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * TODO: Rewrite!!!
 *
 * @author KenM
 */
public class ExShowFortressSiegeInfo extends ServerPacket {
    private final int _csize = 0;
    private final int _csize2 = 0;


    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_FORTRESS_SIEGE_INFO);

        writeInt(0); // Fortress Id
        writeInt(0); // Total Barracks Count
        if (_csize > 0) {
            switch (_csize) {
                case 3: {
                    switch (_csize2) {
                        case 0: {
                            writeInt(0x03);
                            break;
                        }
                        case 1: {
                            writeInt(0x02);
                            break;
                        }
                        case 2: {
                            writeInt(0x01);
                            break;
                        }
                        case 3: {
                            writeInt(0x00);
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
                            writeInt(0x05);
                            break;
                        }
                        case 1: {
                            writeInt(0x04);
                            break;
                        }
                        case 2: {
                            writeInt(0x03);
                            break;
                        }
                        case 3: {
                            writeInt(0x02);
                            break;
                        }
                        case 4: {
                            writeInt(0x01);
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < 0; i++) {
                writeInt(0x00);
            }
        }
    }

}
