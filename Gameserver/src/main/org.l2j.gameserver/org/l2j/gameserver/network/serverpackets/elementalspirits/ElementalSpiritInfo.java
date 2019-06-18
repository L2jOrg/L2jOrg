package org.l2j.gameserver.network.serverpackets.elementalspirits;


import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritInfo extends ServerPacket {

    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_INFO);
        writeByte(0x01); // show spirit info 1
        writeByte(2); // active Spirit element

        writeByte(4); // spirit count

        for (byte i = 1; i <= 4; i++) {
            writeByte(i);
            writeByte(i == 2); // is open ?
            if(i == 2) { // is open ?
                writeByte(2); // evolve level
                writeInt(13566); // Spirit class id
                writeLong(45); // spirit exp
                writeLong(9556); // next exp
                writeLong(4484949849L); // max exp
                writeInt(3); // current level
                writeInt(10); // max level
                writeInt(5); // remain point
                writeInt(10); // atk point
                writeInt(45); // def point
                writeInt(10); // crit rate point
                writeInt(15); // crit pow point
                writeInt(25); // Max Atk point
                writeInt(20); // Max def point
                writeInt(15); // Max Crit Rate point
                writeInt(12); // Max crit pow point

                writeByte(2);

                for (int j = 0; j < 2; j++) {
                    writeShort(2);
                    writeLong(10);
                }
            }
        }

        writeInt(2); // talent count
        for (int j = 0; j < 2; j++) { // for each talent
            writeInt(57); // init talent item id
            writeLong(200000); // init talent item count
        }
    }
}