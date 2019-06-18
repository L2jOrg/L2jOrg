package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO;

public class ExElementalSpiritEvolutionInfo extends ServerPacket {

    @Override
    protected void writeImpl(L2GameClient client) throws Exception {
        writeId(EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO);

        writeByte(2); // current element spirit
        writeInt(2); // current element class id

        writeInt(2); // count

        for (int i = 0; i < 2; i++) { // for each count
            writeInt(2); // elemental class Id
            writeDouble(50); // evol probability
        }

        writeInt(2); // material item count
        for (int i = 0; i < 2;  i++) { // for each material
            writeInt(57); // item id
            writeInt( 10000); // item count
            writeInt( 1000000); // owned count
        }

    }
}
